package com.binbill.seller.Offers;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
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
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Order.Order;
import com.binbill.seller.Order.OrderItem;
import com.binbill.seller.Order.SuggestionSku;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

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
    TextView tv_error_offer_expiry;

    @ViewById
    ImageView iv_reset;

    Calendar mCalendar = Calendar.getInstance();
    private int EDIT_OFFER = 1;
    private int mType = 0;
    private ZXingScannerView mScannerView;


    @AfterViews
    public void setUpView() {

        if (getIntent().hasExtra(Constants.OFFER_ITEM)) {
            mType = EDIT_OFFER;
//            setUpData((OfferItem) getIntent().getSerializableExtra(Constants.OFFER_ITEM));
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

    private void setUpListener() {

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

        et_barcode.addTextChangedListener(textWatcher);
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
            btn_submit.setVisibility(View.GONE);
            btn_submit_progress.setVisibility(View.VISIBLE);

            makeUploadDataToServerCall();
        }
    }

    private boolean isValid() {

        /**
         * OFFER DESCRIPTION
         */
        String offerMrp = et_mrp.getText().toString().trim();
        if (TextUtils.isEmpty(offerMrp)) {
            tv_error_offer_description.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_offer_description.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, tv_error_offer_description.getBottom());
            return false;
        }

        /**
         * OFFER EXPIRY
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
    }

    private void makeUploadDataToServerCall() {
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

    private void fetchSKUFromBarCode(String barcode) {
        new RetrofitHelper(this).fetchSKUFromBarCode(barcode, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONObject("result") != null) {
                            JSONObject suggestionJson = jsonObject.getJSONObject("result");
                            Type classType = new TypeToken<Order>() {
                            }.getType();

                            SuggestionSku suggestionSku = new Gson().fromJson(suggestionJson.toString(), classType);
                            handleResponse(suggestionSku);
                        }
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {
                Log.d("SHRUTI", "Error ");
            }
        });
    }

    private void handleResponse(SuggestionSku suggestionSku) {

        if (suggestionSku.getMeasurement() != null && suggestionSku.getMeasurement().size() > 0) {
            OrderItem.OrderSKU sku = suggestionSku.getMeasurement().get(0);
            et_mrp.setText(sku.getSkuMrp());
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
