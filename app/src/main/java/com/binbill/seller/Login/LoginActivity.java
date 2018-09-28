package com.binbill.seller.Login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.PrefixEditText;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewById(R.id.et_mobile)
    PrefixEditText mobileNumber;

    @ViewById(R.id.btn_submit)
    AppButton submit;

    @ViewById(R.id.btn_submit_progress)
    LinearLayout submitProgress;

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    TextView tv_error_mobile, terms_privacy_tv;

    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        setUpListeners();
        String mobile = Utility.fetchMobile(this);
        mobileNumber.setText(mobile);
        mobileNumber.setSelection(mobileNumber.getText().toString().length());

        Utility.enableButton(LoginActivity.this, submit, false);
    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    private void setUpListeners() {

        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Terms of services Clicked", Toast.LENGTH_SHORT).show();
                String url = "https://www.binbill.com/termSeller";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        };

        makeLinks(terms_privacy_tv, new String[]{getString(R.string.terms_of_use)}, new ClickableSpan[]{
                termsOfServicesClick
        });

        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (tv_error_mobile != null && tv_error_mobile.getVisibility() == View.VISIBLE) {
                        tv_error_mobile.setVisibility(View.INVISIBLE);
                    }

                    if (s.length() >= 10) {
                        Utility.hideKeyboard(LoginActivity.this, tv_error_mobile);
                        Utility.enableButton(LoginActivity.this, submit, true);
                    } else
                        Utility.enableButton(LoginActivity.this, submit, false);
                } else {
                    Utility.enableButton(LoginActivity.this, submit, false);
                }

                if (isValidDetails(false)) {
                    tv_error_mobile.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Click(R.id.btn_submit)
    public void onNextClicked(View viewNextBtn) {
        Utility.hideKeyboard(this, submit);
        if (isValidDetails(true)) {

            submit.setVisibility(View.GONE);
            submitProgress.setVisibility(View.VISIBLE);

            checkPermission();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    Constants.PERMISSION_READ_SMS);
        } else {
            makeSendOTPRequest();
        }
    }

    private void makeSendOTPRequest() {

        HashMap<String, String> map = new HashMap<>();
        map.put("mobile_no", mobileNumber.getText().toString().trim());

        new RetrofitHelper(this).getOTPToLoginUser(map, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response.toString());
            }

            @Override
            public void onErrorResponse() {
                handleError();
            }
        });
    }

    private void handleError() {
        submit.setVisibility(View.VISIBLE);
        submitProgress.setVisibility(View.GONE);

        showSnackBar(getString(R.string.something_went_wrong));
    }

    private void handleResponse(String value) {

        try {
            JSONObject jsonObject = new JSONObject(value);
            if (jsonObject.getBoolean("status")) {
                getFirebaseToken();
                SharedPref.putString(this, SharedPref.MOBILE, mobileNumber.getText().toString().trim());
                AppSession.getInstance(this).setMobile(mobileNumber.getText().toString().trim());
                Intent intent = new Intent(this, OTPLoginActivity_.class);
                intent.putExtra(Constants.MOBILE, mobileNumber.getText().toString().trim());
                startActivity(intent);

                submit.setVisibility(View.VISIBLE);
                submitProgress.setVisibility(View.GONE);
            } else {
                handleError();
            }
        } catch (JSONException e) {
            handleError();
        }
    }

    private void getFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("LoginActivity", "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        SharedPref.putString(LoginActivity.this, SharedPref.FIREBASE_TOKEN, token);
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_READ_SMS: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        makeSendOTPRequest();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                       /* if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                            //Show permission explanation dialog...
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.RECEIVE_SMS},
                                    Constants.PERMISSION_READ_SMS);
                        } else {
                            //Never ask again selected, or device policy prohibits the app from having that permission.
                            //So, disable that feature, or fall back to another situation...*/
                        makeSendOTPRequest();


                    }
                }
            }
        }
    }

    private boolean isValidDetails(boolean showErrorOnUI) {
        String mobileStr = mobileNumber.getText().toString().trim();
        if (TextUtils.isEmpty(mobileStr)) {
            if (showErrorOnUI) {
                tv_error_mobile.setText(getString(R.string.error_field_cannot_be_empty));
                tv_error_mobile.setVisibility(View.VISIBLE);
            }
            return false;
        } else if (!isValidMobileNumber(mobileStr)) {
            if (showErrorOnUI) {
                tv_error_mobile.setText(getString(R.string.error_incorrect_value));
                tv_error_mobile.setVisibility(View.VISIBLE);
            }
            return false;
        }
        return true;
    }

    public boolean isValidMobileNumber(String mobileStr) {
        Long mobileNumber = Utility.isValidMobileNumber(mobileStr);
        return mobileNumber.compareTo(-1L) != 0;
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.sign_in));
    }

    @Override
    public void onBackPressed() {
        Utility.hideKeyboard(this, tv_error_mobile);
        super.onBackPressed();
    }
}
