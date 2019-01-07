package com.binbill.seller.Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;

@EActivity(R.layout.activity_business_register_details)
public class BusinessDetailsRegisterActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    EditText et_business_name,et_gstin,et_pan,et_adhar;

    @ViewById
    SquareAppButton btn_next;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById(R.id.iv_skip)
    TextView iv_skip;

    @ViewById
    NestedScrollView scroll_view;

    UserRegistrationDetails userRegistrationDetails;

    private String mMode;
    private ProgressDialog dialog;

    @AfterViews
    public void initiateViews() {

        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();

        setUpToolbar();
        dialog = new ProgressDialog(this);
        setUpListeners();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText("Business Details");
        iv_skip.setVisibility(View.VISIBLE);
    }


    private void setUpListeners() {

        et_business_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableRegisterButton();
            }
        });

        et_pan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableRegisterButton();
            }
        });

        et_gstin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableRegisterButton();
            }
        });

    }

    public void enableDisableRegisterButton() {

        String pan = et_pan.getText().toString();
        String gst = et_gstin.getText().toString();
        String adhaar = et_adhar.getText().toString();

//        String mainCategory = et_main_category.getText().toString();

        if ((!Utility.isEmpty(pan) || !Utility.isEmpty(gst) || !Utility.isEmpty(adhaar)))
            Utility.enableButton(this, btn_next, true);
        else
            Utility.enableButton(this, btn_next, false);
    }

    @Click(R.id.btn_next)
    public void submit(View view){

        Utility.hideKeyboard(BusinessDetailsRegisterActivity.this, btn_next);
        if(isValidDetails()){

            saveDataLocally();
            makeCallToServer();
        }
    }

    @Click(R.id.iv_skip)
    public void skipBtn(View v){


        saveDataLocally();
        makeCallToServer();

       /* Utility.hideKeyboard(BusinessDetailsRegisterActivity.this, iv_skip);
        int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
        Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();*/
    }

    private void saveDataLocally(){
        userRegistrationDetails.setPan(et_pan.getText().toString());
        userRegistrationDetails.setGstin(et_gstin.getText().toString());
        userRegistrationDetails.setAdhar(et_adhar.getText().toString());
        userRegistrationDetails.setBusinessName(et_business_name.getText().toString());
        AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);
    }

    private void makeCallToServer(){

        dialog.setMessage("Saving data please wait....");
        dialog.show();

        HashMap<String, String> map = new HashMap<>();
        UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();

        if (!Utility.isEmpty(userRegistrationDetails.getGstin()))
            map.put("gstin", userRegistrationDetails.getGstin());
        if (!Utility.isEmpty(userRegistrationDetails.getPan()))
            map.put("pan", userRegistrationDetails.getPan());
        if (!Utility.isEmpty(userRegistrationDetails.getAdhar()))
            map.put("aadhar", userRegistrationDetails.getAdhar());
        if (!Utility.isEmpty(userRegistrationDetails.getBusinessName()))
            map.put("business_name", userRegistrationDetails.getBusinessName());
       /* if (!Utility.isEmpty(userRegistrationDetails.getBusinessAddress()))
            map.put("address", userRegistrationDetails.getBusinessAddress());*/
       /* if (!Utility.isEmpty(userRegistrationDetails.getPincode()))
            map.put("pincode", userRegistrationDetails.getPincode());*/
        /*if (userRegistrationDetails.getState() != null)
            map.put("state_id", userRegistrationDetails.getState().getStateId());
        if (userRegistrationDetails.getCity() != null)
            map.put("city_id", userRegistrationDetails.getCity().getCityId());*/
        if (userRegistrationDetails.getLocality() != null)
            map.put("locality_id", userRegistrationDetails.getLocality().getLocalityId());
        if (userRegistrationDetails.getDaysOpen() != null && userRegistrationDetails.getDaysOpen().size() > 0)
            map.put("shop_open_day", TextUtils.join(",", userRegistrationDetails.getDaysOpen()));
        map.put("start_time", userRegistrationDetails.getShopOpen());
        map.put("close_time", userRegistrationDetails.getShopClose());
        map.put("home_delivery", String.valueOf(userRegistrationDetails.isHomeDelivery()));
        map.put("pay_online", String.valueOf(userRegistrationDetails.isOnlinePayment()));
        if (!Utility.isEmpty(userRegistrationDetails.getHomeDeliveryDistance()))
            map.put("home_delivery_remarks", userRegistrationDetails.getHomeDeliveryDistance());
        if (userRegistrationDetails.getPaymentOptions() != null && userRegistrationDetails.getPaymentOptions().size() > 0)
            map.put("payment_modes", TextUtils.join(",", userRegistrationDetails.getPaymentOptions()));


        new RetrofitHelper(this).updateBasicDetails(userRegistrationDetails.getId(), map, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response);
            }

            @Override
            public void onErrorResponse() {
                handleError();
            }
        });

    }

    private void handleError() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        showSnackBar(getString(R.string.something_went_wrong));
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            if(dialog.isShowing()){
                dialog.dismiss();
            }

            if (jsonObject.getBoolean("status")) {

                if (mMode == Constants.EDIT_MODE) {
                    finish();
                } else {

                    int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
                    Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

               /* btn_submit.setVisibility(View.VISIBLE);
                btn_submit_progress.setVisibility(View.GONE);*/

            } else
                handleError();
        } catch (JSONException e) {
            handleError();
        }
    }


    private boolean isValidDetails() {
       /* String panStr = et_pan.getText().toString();
        if (!Utility.isEmpty(panStr)) {
            Matcher matcher = Constants.PAN_PATTERN.matcher(panStr.trim());
            if (!matcher.matches()) {
                Toast.makeText(this, getString(R.string.incorrect_pan_number), Toast.LENGTH_SHORT).show();
                // tv_error_pan.setText(getString(R.string.incorrect_pan_number));
                //  tv_error_pan.setVisibility(View.VISIBLE);
                scroll_view.scrollTo(0, et_pan.getBottom());

            }
            return false;
        }*/

        /**
         * GSTIN
         */
        String gstin = et_gstin.getText().toString();
        if (!Utility.isEmpty(gstin)) {

            try {
                if (!Utility.validGSTIN(gstin)) {
                    Toast.makeText(this, getString(R.string.incorrect_gstin_number), Toast.LENGTH_SHORT).show();
                    // tv_error_gstin.setText(getString(R.string.incorrect_gstin_number));
                    //   tv_error_gstin.setVisibility(View.VISIBLE);
                    scroll_view.scrollTo(0, et_gstin.getBottom());
                    return false;
                }
            } catch (Exception e) {

            }
        }
        return true;

    }

}


