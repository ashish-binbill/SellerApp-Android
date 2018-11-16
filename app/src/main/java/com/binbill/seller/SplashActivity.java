package com.binbill.seller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.Login.LandingActivity_;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.Registration.RegistrationResolver;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (getIntent() != null && getIntent().hasExtra(Constants.NOTIFICATION_DEEPLINK))
            AppSession.getInstance(this).setNotificationIntent(getIntent().getStringExtra(Constants.NOTIFICATION_DEEPLINK));

        if (getIntent() != null && getIntent().hasExtra(Constants.ORDER_ID))
            AppSession.getInstance(this).setNotificationOrderId(getIntent().getStringExtra(Constants.ORDER_ID));

        setUpFlow();

        PowerManager.WakeLock screenOn = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
        if (screenOn.isHeld())
            screenOn.release();
    }

    public void setUpFlow() {

        String appToken = SharedPref.getString(this, SharedPref.AUTH_TOKEN);
        if (Utility.isEmpty(appToken)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LandingActivity_.class));
                    finish();
                }
            }, 3000);
        } else {
            makeUserStateApiCall();
        }
    }

    private void makeUserStateApiCall() {

        new RetrofitHelper(this).getUserState(new RetrofitHelper.RetrofitCallback() {
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

        Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
    }

    private void handleResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("status")) {

                UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(SplashActivity.this).getUserRegistrationDetails();
                userRegistrationDetails.setAssisted(jsonObject.optBoolean("is_assisted"));
                userRegistrationDetails.setFmcg(jsonObject.optBoolean("is_fmcg"));

                AppSession.getInstance(SplashActivity.this).setUserRegistrationDetails(userRegistrationDetails);

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
                                    AppSession.getInstance(SplashActivity.this).setDashboardData(dashboardModel);
                                    startActivity(intent);
                                    finish();
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);
                }
            } else
                handleError();
        } catch (JSONException e) {
            handleError();
        }
    }
}