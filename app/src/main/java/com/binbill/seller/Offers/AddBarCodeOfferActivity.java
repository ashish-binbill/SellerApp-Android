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
    ImageView iv_offer;

    @ViewById
    RelativeLayout rl_bar_code_scanner;

    @ViewById
    TextView tv_error_offer_expiry, tv_error_mrp, tv_error_barcode, tv_error_discount, tv_search, tv_item_name;

    @ViewById
    ImageView iv_reset;

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

        if (getIntent().hasExtra(Constants.OFFER_ITEM)) {
            mType = EDIT_OFFER;
            mOfferItem = (OfferItem) getIntent().getSerializableExtra(Constants.OFFER_ITEM);
            setUpData(mOfferItem);
        }

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        mScannerView.setAutoFocus(true);

        enableDisableVerifyButton();
        setUpToolbar();
        setUpListener();
        checkCameraPermission();
    }

    private void setUpData(OfferItem offerItem) {
        et_barcode.setText("6876");
        et_barcode.setFocusableInTouchMode(false);
        et_barcode.setFocusable(false);

        et_mrp.setText(offerItem.getSku().getMrp());
        et_discount.setText(offerItem.getSku().getOfferDiscount());

        et_expiry_date.setText(Utility.getFormattedDate(12, offerItem.getOfferEndDate(), 0));
        mOfferId = offerItem.getOfferId();

        btn_submit.setText(getString(R.string.update));

        rl_bar_code_scanner.setVisibility(View.GONE);
        iv_offer.setVisibility(View.VISIBLE);

        String imageUrl = Constants.BASE_URL + "skus/" + mOfferItem.getSku().getSkuId() + "/measurements/" + mOfferItem.getSku().getSkuMeasurementId() + "/images";
        Picasso.get()
                .load(imageUrl)
                .config(Bitmap.Config.RGB_565)
                .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_placeholder_sku))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(iv_offer);
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
        et_mrp.addTextChangedListener(textWatcher);
        et_discount.addTextChangedListener(textWatcher);
        et_expiry_date.addTextChangedListener(textWatcher);

        iv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mScannerView.startCamera();
//                mScannerView.setAutoFocus(true);
                mScannerView.resumeCameraPreview(AddBarCodeOfferActivity.this);
                et_barcode.setText("");
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
            tv_error_mrp.setVisibility(View.GONE);
            tv_error_barcode.setVisibility(View.GONE);
            tv_error_offer_expiry.setVisibility(View.GONE);


            btn_submit.setVisibility(View.GONE);
            btn_submit_progress.setVisibility(View.VISIBLE);

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
        if (TextUtils.isEmpty(discount)) {
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
            skuId = mOfferItem.getSku().getSkuId();
            skuMeasurementId = mOfferItem.getSku().getSkuMeasurementId();
        }

        String discount = et_discount.getText().toString();
        String expiry = et_expiry_date.getText().toString();

        new RetrofitHelper(this).addBarCodeOfferFromSeller(skuId, skuMeasurementId, discount, expiry, mOfferId, new RetrofitHelper.RetrofitCallback() {
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

        if (!Utility.isEmpty(barcode.trim()) && !Utility.isEmpty(mrp.trim()) && !Utility.isEmpty(discount.trim()) && !Utility.isEmpty(offerExpiry.trim()))
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

            et_barcode.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(AddBarCodeOfferActivity.this, R.drawable.ic_barcode_success), null);
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
