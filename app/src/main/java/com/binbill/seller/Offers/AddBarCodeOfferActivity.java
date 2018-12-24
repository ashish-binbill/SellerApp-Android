package com.binbill.seller.Offers;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Order.OrderItem;
import com.binbill.seller.Order.SuggestionSku;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

@EActivity(R.layout.activity_add_bar_code_offer)
public class AddBarCodeOfferActivity extends BaseActivity implements ZXingScannerView.ResultHandler {


    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    AppButton btn_submit;

    @ViewById
    ProgressBar btn_submit_progress;

    @ViewById
    NestedScrollView scroll_view;

    @ViewById
    EditText et_barcode, et_mrp, et_discount, et_expiry_date;

    @ViewById
    ImageView iv_offer, ic_sku_image;

    @ViewById
    RelativeLayout rl_bar_code_scanner;

    @ViewById
    TextView tv_error_offer_expiry, tv_error_barcode, tv_item_amount_discounted, tv_error_discount, tv_error_mrp, tv_search, tv_item_name;

    @ViewById
    ImageView iv_reset;

    @ViewById
    LinearLayout ll_discount_layout;

    Calendar mCalendar = Calendar.getInstance();
    private int EDIT_OFFER = 1;
    private int mType = 0;
    private ZXingScannerView mScannerView;
    private SuggestionSku suggestionSku;
    private String mOfferId = "";
    private final int BARCODE_STATE_VERIFIED = 1;
    private final int BARCODE_STATE_UNVERIFIED = 2;
    private int barCodeStatus = BARCODE_STATE_UNVERIFIED;
    private OfferItem mOfferItem;


    @AfterViews
    public void setUpView() {

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        mScannerView.setAutoFocus(true);

        enableDisableVerifyButton();
        setUpToolbar();
        setUpListener();
        checkCameraPermission();

        if (getIntent().hasExtra(Constants.OFFER_ITEM)) {
            mType = EDIT_OFFER;
            mOfferItem = (OfferItem) getIntent().getSerializableExtra(Constants.OFFER_ITEM);
            setUpData(mOfferItem);
        }
    }

    private void setUpData(OfferItem offerItem) {

        if (getIntent().hasExtra(Constants.OFFER_TYPE)) {
            et_barcode.setText(offerItem.getBarCodeSuggestedOffer());
            et_mrp.setText(offerItem.getMrp());
            et_expiry_date.setText(Utility.getFormattedDate(12, offerItem.getOfferEndDate(), 1));

            int offerType = getIntent().getIntExtra(Constants.OFFER_TYPE, -1);
            if (offerType == Constants.OFFER_TYPE_DISCOUNTED) {
                try {
                    double mrp = Double.parseDouble(offerItem.getMrp());
                    double percentage = Double.parseDouble(offerItem.getOfferValue());

                    et_discount.setText(Utility.showDoubleString(percentage));
                } catch (Exception e) {
                }

                ll_discount_layout.setVisibility(View.VISIBLE);
            } else
                ll_discount_layout.setVisibility(View.GONE);

        } else {
            et_barcode.setText(offerItem.getSku().getBarCode());
            et_mrp.setText(offerItem.getSku().getMrp());
            et_discount.setText(offerItem.getOfferDiscount());
            ll_discount_layout.setVisibility(View.VISIBLE);

            et_expiry_date.setText(Utility.getFormattedDate(12, offerItem.getOfferEndDate(), 0));
        }
        et_barcode.setFocusableInTouchMode(false);
        et_barcode.setFocusable(false);

        mOfferId = offerItem.getOfferId();

        btn_submit.setText(getString(R.string.update));

        rl_bar_code_scanner.setVisibility(View.GONE);
        ic_sku_image.setVisibility(View.GONE);
        iv_offer.setVisibility(View.VISIBLE);

        String imageUrl = Constants.BASE_URL + "skus/" + mOfferItem.getSkuId() + "/measurements/" + mOfferItem.getSkuMeasurementId() + "/images";
        Picasso.get()
                .load(imageUrl)
                .config(Bitmap.Config.RGB_565)
                .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_placeholder_sku))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(iv_offer);

        if (getIntent().hasExtra(Constants.OFFER_TYPE)) {
            tv_item_name.setText(mOfferItem.getSkuTitle() + " - " + mOfferItem.getMeasurementValue() + " " + mOfferItem.getAcronym());
        } else {
            tv_item_name.setText(mOfferItem.getSku().getSkuTitle() + " - " + mOfferItem.getSku().getMeasurementValue() + " " + mOfferItem.getSku().getAcronym());
        }
        tv_item_name.setVisibility(View.VISIBLE);

        tv_search.setVisibility(View.GONE);
        enableDisableVerifyButton();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.MANAGE_DOCUMENTS},
                    Constants.PERMISSION_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_CAMERA: {

                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showSnackBar("Please give permissions to scan barcode");

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);

                    }
                }
                return;
            }
        }

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enableDisableVerifyButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    TextWatcher discountTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String discount = et_discount.getText().toString();
            String mrp = et_mrp.getText().toString();
            if (!Utility.isEmpty(discount) && !Utility.isEmpty(mrp)) {

                double mrpAmount = Double.parseDouble(mrp);
                double discountEntered = Double.parseDouble(discount);

                double finalAmount = mrpAmount - (mrpAmount * (discountEntered / 100));

                tv_item_amount_discounted.setText("= " + getString(R.string.rupee_sign) + Utility.showDoubleString(finalAmount));
                tv_item_amount_discounted.setVisibility(View.VISIBLE);
            } else {
                tv_item_amount_discounted.setVisibility(View.GONE);
            }

            enableDisableVerifyButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    TextWatcher barCodeTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enableDisableVerifyButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {

            String textEntered = editable.toString();

            if (mType != EDIT_OFFER)
                if (!Utility.isEmpty(textEntered) &&
                        suggestionSku != null && suggestionSku.getBarcodeMeasurement() != null
                        && suggestionSku.getBarcodeMeasurement().getSkuBarCode().equalsIgnoreCase(textEntered.trim())) {
                    /**
                     * do nothing
                     */
                    tv_search.setVisibility(View.GONE);
                } else {
                    barCodeStatus = BARCODE_STATE_UNVERIFIED;
                    suggestionSku = null;
                    tv_search.setVisibility(View.VISIBLE);
                    tv_item_name.setVisibility(View.GONE);

                    et_barcode.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
        }
    };

    private void setUpListener() {

        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchSKUFromBarCode(et_barcode.getText().toString());
            }
        });

        et_expiry_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddBarCodeOfferActivity.this, datePickerListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        et_barcode.addTextChangedListener(barCodeTextWatcher);
        et_mrp.addTextChangedListener(discountTextWatcher);
        et_discount.addTextChangedListener(discountTextWatcher);
        et_expiry_date.addTextChangedListener(textWatcher);

        iv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mScannerView.startCamera();
//                mScannerView.setAutoFocus(true);
                mScannerView.resumeCameraPreview(AddBarCodeOfferActivity.this);
                et_barcode.setText("");
                et_discount.setText("");
                et_mrp.setText("");
            }
        });

    }


    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {

        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        et_expiry_date.setText(sdf.format(mCalendar.getTime()));
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        if (mType == EDIT_OFFER)
            toolbarText.setText(getString(R.string.create_new_offer));
        else
            toolbarText.setText(getString(R.string.create_new_offer));
    }

    @Click(R.id.btn_submit)
    public void onSubmitClicked(View viewRegister) {
        Utility.hideKeyboard(this, btn_submit);
        if (isValid()) {

            tv_error_discount.setVisibility(View.GONE);
            tv_error_barcode.setVisibility(View.GONE);
            tv_error_offer_expiry.setVisibility(View.GONE);


            btn_submit.setVisibility(View.GONE);
            btn_submit_progress.setVisibility(View.VISIBLE);


            Utility.hideKeyboard(this, btn_submit_progress);
            makeUploadDataToServerCall();
        }
    }

    private boolean isValid() {

        /**
         * BARCODE
         */
        String barcode = et_barcode.getText().toString().trim();
        if (TextUtils.isEmpty(barcode)) {
            tv_error_barcode.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_barcode.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, tv_error_barcode.getBottom());
            return false;
        }

        if (mType != EDIT_OFFER && barCodeStatus == BARCODE_STATE_UNVERIFIED) {
            tv_error_barcode.setText(getString(R.string.incorrect_barcode));
            tv_error_barcode.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, tv_error_barcode.getBottom());
            return false;
        }

        /**
         * MRP
         */
        String mrp = et_mrp.getText().toString().trim();
        if (TextUtils.isEmpty(mrp)) {
            tv_error_mrp.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_mrp.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, tv_error_mrp.getBottom());
            return false;
        }

        /**
         * DISCOUNT
         */
        String discount = et_discount.getText().toString().trim();
        if (ll_discount_layout.getVisibility() == View.VISIBLE && TextUtils.isEmpty(discount)) {
            tv_error_discount.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_discount.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, tv_error_discount.getBottom());
            return false;
        }

        /**
         * EXPIRY
         */
        String officeExpiry = et_expiry_date.getText().toString().trim();
        if (TextUtils.isEmpty(officeExpiry)) {
            tv_error_offer_expiry.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_offer_expiry.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, tv_error_offer_expiry.getBottom());
            return false;
        }

        return true;
    }

    private void makeUploadDataToServerCall() {
        String skuId;
        String skuMeasurementId;

        if (mType != EDIT_OFFER) {
            skuId = suggestionSku.getId();
            skuMeasurementId = suggestionSku.getBarcodeMeasurement().getSkuId();
        } else {
            skuId = mOfferItem.getSkuId();
            skuMeasurementId = mOfferItem.getSkuMeasurementId();
        }

        String discount = et_discount.getText().toString();
        String expiry = et_expiry_date.getText().toString();
        String mrp = et_mrp.getText().toString();

        String brandOfferId = null;
        String offerType = null;
        if (getIntent().hasExtra(Constants.OFFER_TYPE)) {
            brandOfferId = mOfferItem.getOfferId();
            offerType = String.valueOf(getIntent().getIntExtra(Constants.OFFER_TYPE, 1));
        }


        new RetrofitHelper(this).addBarCodeOfferFromSeller(skuId, skuMeasurementId, mrp, discount, expiry, mOfferId, brandOfferId, offerType, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {
                        JSONObject sellerOffer = jsonObject.getJSONObject("seller_offer");

                        Type classType = new TypeToken<OfferItem>() {
                        }.getType();

                        OfferItem offerItem = new Gson().fromJson(sellerOffer.toString(), classType);

                        if (mType == EDIT_OFFER) {
                            finish();
                        } else {
                            showSnackBar(getString(R.string.offer_published_success_barcode));

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1500);
                        }
                    } else {
                        showSnackBar(getString(R.string.something_went_wrong));
                        btn_submit.setVisibility(View.VISIBLE);
                        btn_submit_progress.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    showSnackBar(getString(R.string.something_went_wrong));
                    btn_submit.setVisibility(View.VISIBLE);
                    btn_submit_progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));
                btn_submit.setVisibility(View.VISIBLE);
                btn_submit_progress.setVisibility(View.GONE);
            }
        });

    }

    private void enableDisableVerifyButton() {
        String barcode = et_barcode.getText().toString();
        String mrp = et_mrp.getText().toString();
        String discount = et_discount.getText().toString();
        String offerExpiry = et_expiry_date.getText().toString();

        boolean discountCheck = true;
        if(ll_discount_layout.getVisibility() == View.VISIBLE && Utility.isEmpty(discount.trim()))
            discountCheck = false;

        if (!Utility.isEmpty(barcode.trim()) && !Utility.isEmpty(mrp.trim()) && discountCheck && !Utility.isEmpty(offerExpiry.trim()))
            Utility.enableButton(this, btn_submit, true);
        else
            Utility.enableButton(this, btn_submit, false);
    }

    private void setBarcodeData(String displayValue) {
        et_barcode.setText(displayValue);
        fetchSKUFromBarCode(displayValue);
    }

    private void fetchSKUFromBarCode(final String barcode) {
        new RetrofitHelper(this).fetchSKUFromBarCode(barcode, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONObject("result") != null) {
                            JSONObject suggestionJson = jsonObject.getJSONObject("result");
                            Type classType = new TypeToken<SuggestionSku>() {
                            }.getType();

                            suggestionSku = new Gson().fromJson(suggestionJson.toString(), classType);
                            handleResponse(suggestionSku);
                        } else {
                            showSnackBar(getString(R.string.sku_not_found));
                            barCodeStatus = BARCODE_STATE_UNVERIFIED;
                            tv_search.setVisibility(View.VISIBLE);
                            tv_item_name.setVisibility(View.GONE);
                            et_barcode.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                        }
                    }
                } catch (JSONException e) {
                    barCodeStatus = BARCODE_STATE_UNVERIFIED;
                    tv_search.setVisibility(View.VISIBLE);
                    tv_item_name.setVisibility(View.GONE);
                    et_barcode.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                }
            }

            @Override
            public void onErrorResponse() {
                Log.d("SHRUTI", "Error ");
                barCodeStatus = BARCODE_STATE_UNVERIFIED;
                tv_search.setVisibility(View.VISIBLE);
                tv_item_name.setVisibility(View.GONE);
                et_barcode.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

            }
        });
    }

    private void handleResponse(SuggestionSku suggestionSku) {

        if (suggestionSku.getBarcodeMeasurement() != null) {
            OrderItem.OrderSKU sku = suggestionSku.getBarcodeMeasurement();
            et_mrp.setText(sku.getSkuMrp());

            barCodeStatus = BARCODE_STATE_VERIFIED;
            tv_search.setVisibility(View.GONE);

            tv_item_name.setText(suggestionSku.getTitle() + " - " + sku.getSkuMeasurementValue() + " " + sku.getSkuMeasurementAcronym());
            tv_item_name.setVisibility(View.VISIBLE);

            ic_sku_image.setVisibility(View.VISIBLE);
            iv_offer.setVisibility(View.GONE);

            String imageUrl = Constants.BASE_URL + "skus/" + sku.getParentSkuId() + "/measurements/" + sku.getSkuId() + "/images";
            Picasso.get()
                    .load(imageUrl)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_placeholder_sku))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(ic_sku_image);

            et_barcode.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(AddBarCodeOfferActivity.this, R.drawable.ic_barcode_success), null);
        } else {
            ic_sku_image.setVisibility(View.GONE);
            tv_item_name.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setAutoFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

        setBarcodeData(result.getText());
        fetchSKUFromBarCode(result.getText());
    }
}
