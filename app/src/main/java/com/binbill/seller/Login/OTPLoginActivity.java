package com.binbill.seller.Login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.OtpListener;
import com.binbill.seller.CustomViews.OtpView;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.R;
import com.binbill.seller.Registration.RegistrationResolver;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SMS.SMSListener;
import com.binbill.seller.SMS.SMSReceiver;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

@EActivity(R.layout.activity_otp_login)
public class OTPLoginActivity extends BaseActivity {


    @ViewById
    OtpView otp_view;

    @ViewById
    TextView resend_otp, tv_auto_read;

    @ViewById
    AppButton btn_submit;

    @ViewById
    LinearLayout btn_submit_progress, auto_read;

    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView toolbar_text;
    private boolean isSmsReceiverOnReceiveCalled;
    private boolean isAutoReading = false;

    @AfterViews
    public void initiateViews() {

        setupToolbar();
        setupViews();
        setUpListeners();
        Utility.enableButton(this, btn_submit, false);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbar_text.setText(getString(R.string.enter_otp));
    }

    private void setUpListeners() {

        otp_view.setListener(new OtpListener() {
            @Override
            public void onOtpEntered(String otp) {
                Utility.enableButton(OTPLoginActivity.this, btn_submit, true);
            }

            @Override
            public void onOTPIncomplete() {
                Utility.enableButton(OTPLoginActivity.this, btn_submit, false);
            }
        });
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED) {

            auto_read.setVisibility(View.VISIBLE);
            isAutoReading = true;
            SMSReceiver.bindListener(new SMSListener() {
                @Override
                public void messageReceived(String messageText) {

                    if (otp_view != null && btn_submit != null && !isSmsReceiverOnReceiveCalled) {
                        isSmsReceiverOnReceiveCalled = true;
                        Log.d("Text", messageText);
                        auto_read.setVisibility(View.GONE);
                        otp_view.setOTP(messageText.substring(messageText.indexOf("\"") + 1, messageText.lastIndexOf("\"")));
//                        s.callOnClick();
                        isSmsReceiverOnReceiveCalled = false;
                    }
                }
            });

        }
    }

    @Click(R.id.btn_submit)
    public void onNextClicked(View nextBtn) {
        btn_submit.setVisibility(View.GONE);
        btn_submit_progress.setVisibility(View.VISIBLE);

        makeVerifyOTPCall();
    }

    @Click(R.id.resend_otp)
    public void onResendOtp(View resend) {
        makeSendOTPRequest();
    }

    private void makeSendOTPRequest() {

        HashMap<String, String> map = new HashMap<>();
        map.put("mobile_no", getIntent().getStringExtra(Constants.MOBILE));

        tv_auto_read.setText("Sending OTP to " + getIntent().getStringExtra(Constants.MOBILE));
        auto_read.setVisibility(View.VISIBLE);

        new RetrofitHelper(this).getOTPToLoginUser(map, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response.toString());
            }

            @Override
            public void onErrorResponse() {
                tv_auto_read.setText(getString(R.string.auto_reading_your_otp));
                if (isAutoReading)
                    tv_auto_read.setVisibility(View.VISIBLE);
                else
                    tv_auto_read.setVisibility(View.GONE);
                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void handleResponse(String value) {

        tv_auto_read.setText(getString(R.string.auto_reading_your_otp));
        if (isAutoReading)
            tv_auto_read.setVisibility(View.VISIBLE);
        else
            tv_auto_read.setVisibility(View.GONE);

        try {
            JSONObject jsonObject = new JSONObject(value);
            if (jsonObject.getBoolean("status")) {

            } else {
                showSnackBar(getString(R.string.something_went_wrong));
            }
        } catch (JSONException e) {
            showSnackBar(getString(R.string.something_went_wrong));
        }
    }

    private void makeVerifyOTPCall() {

        HashMap<String, String> map = new HashMap<>();
        map.put("token", otp_view.getOTP());
        map.put("mobile_no", AppSession.getInstance(this).getMobile());
        map.put("fcm_id", SharedPref.getString(this, SharedPref.FIREBASE_TOKEN));
        map.put("platform", "1");

        new RetrofitHelper(this).validateOTPToLoginUser(map, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response, Constants.OTP_LOGIN);
            }

            @Override
            public void onErrorResponse() {
                handleError();
            }
        });
    }

    private void handleError() {
        btn_submit.setVisibility(View.VISIBLE);
        btn_submit_progress.setVisibility(View.GONE);

        showSnackBar(getString(R.string.something_went_wrong));
    }

    private void handleResponse(String value, int identifier) {

        try {
            JSONObject jsonObject = new JSONObject(value);
            if (jsonObject.getBoolean("status")) {

                switch (identifier) {
                    case Constants.OTP_LOGIN:
                        if (jsonObject.has("authorization") && !jsonObject.isNull("authorization")) {
                            SharedPref.putString(this, SharedPref.AUTH_TOKEN, jsonObject.getString("authorization"));
                        }

                        makeUserStateApiCall();
                        break;
                    case Constants.GET_USER_STATE:

//                        if (jsonObject.optBoolean("is_onboarded", true)) {
//                            startActivity(new Intent(this, DashboardActivity_.class));
//                        } else {
                        RegistrationResolver.parseAndSaveData(this, jsonObject.toString());
                        String nextStep = jsonObject.optString("next_step");
                        int currentIndex = RegistrationResolver.getResolvedIndexForNextScreen(nextStep);

                        final Intent intent = RegistrationResolver.getNextIntent(this, currentIndex);
                        if (intent.getComponent().getClassName().contains("Dashboard")) {
                            ApiHelper.fetchAllCustomer(this);
                            ApiHelper.fetchAllAssistedServices(this);
                            ApiHelper.fetchJobsForVerification(this);
                            ApiHelper.fetchOrders(this);
                            ApiHelper.makeDashboardDataCall(this, new RetrofitHelper.RetrofitCallback() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getBoolean("status")) {

                                            Type classType = new TypeToken<DashboardModel>() {
                                            }.getType();

                                            DashboardModel dashboardModel = new Gson().fromJson(jsonObject.toString(), classType);
                                            AppSession.getInstance(OTPLoginActivity.this).setDashboardData(dashboardModel);

                                            startActivity(intent);
                                        }
                                    } catch (JSONException e) {
                                        finish();
                                    }
                                }

                                @Override
                                public void onErrorResponse() {
                                    finish();
                                }
                            });
                        } else {
                            startActivity(intent);
                        }

                        btn_submit.setVisibility(View.VISIBLE);
                        btn_submit_progress.setVisibility(View.GONE);

                        break;
                }
            } else {
                handleError();
            }
        } catch (JSONException e) {
            handleError();
        }

    }

    private void makeUserStateApiCall() {

        new RetrofitHelper(this).getUserState(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response, Constants.GET_USER_STATE);
            }

            @Override
            public void onErrorResponse() {
                handleError();
            }
        });

    }

    private void setupViews() {
        SpannableString content = new SpannableString(getString(R.string.resend_otp));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        resend_otp.setText(content);
    }


}
