package com.binbill.seller.Dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.Model.BusinessDetailsModel;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Registration.ImagePreviewActivity_;
import com.binbill.seller.Registration.RegistrationResolver;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

@EActivity(R.layout.activity_profile)
public class ProfileActivity extends BaseActivity {
    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ImageView iv_shop_image;

    @ViewById
    TextView tv_shop_name, tv_business_type, tv_shop_address, tv_main_category, tv_open_days, tv_timings, tv_delivery, tv_payment_modes, tv_view_attachment;

    @ViewById
    TextView tv_other_details, tv_edit_business, tv_basic_details;
    private ProfileModel profileDetails;

    @ViewById
    LinearLayout ll_payment_mode, ll_delivery;

    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        setUpListeners();
    }

    private void setUpData() {
        UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();

        tv_shop_name.setText(profileDetails.getName());
        tv_shop_address.setText(profileDetails.getAddress());
        tv_main_category.setText(userRegistrationDetails.getMainCategory().getName());

        ProfileModel.BasicDetails basicDetails = profileDetails.getSellerDetails().getBasicDetails();
        String shopOpenDays = basicDetails.getShopOpenDays();
        StringBuilder displayOpenDays = new StringBuilder();
        if (!Utility.isEmpty(shopOpenDays)) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(shopOpenDays.split(",")));
            for (String openDay : list) {
                switch (openDay) {
                    case "0":
                        displayOpenDays.append(" S");
                        break;
                    case "1":
                        displayOpenDays.append(" M");
                        break;
                    case "2":
                        displayOpenDays.append(" T");
                        break;
                    case "3":
                        displayOpenDays.append(" W");
                        break;
                    case "4":
                        displayOpenDays.append(" T");
                        break;
                    case "5":
                        displayOpenDays.append(" F");
                        break;
                    case "6":
                        displayOpenDays.append(" S");
                        break;
                }
            }
        }

        tv_open_days.setText(displayOpenDays);


        if (userRegistrationDetails.isFmcg()) {
            ArrayList<MainCategory> paymentModesModel = AppSession.getInstance(this).getPaymentModes();
            String paymentModes = basicDetails.getPaymentModes();
            StringBuilder displayPaymentModes = new StringBuilder();
            if (!Utility.isEmpty(paymentModes)) {
                ArrayList<String> list = new ArrayList<>(Arrays.asList(paymentModes.split(",")));
                for (String pay : list) {

                    for (MainCategory paymentMode : paymentModesModel) {
                        if (pay.equalsIgnoreCase(paymentMode.getId())) {
                            displayPaymentModes = displayPaymentModes.append(paymentMode.getName());
                            displayPaymentModes = displayPaymentModes.append(" | ");
                        }
                    }
                }

            }

            tv_payment_modes.setText(displayPaymentModes);

            StringBuilder homeDelivery = new StringBuilder();

            if (basicDetails != null && basicDetails.getHomeDelivery() != null && basicDetails.getHomeDelivery().equalsIgnoreCase("true"))
                homeDelivery.append("Yes");
            else
                homeDelivery.append("No");

            if (!Utility.isEmpty(basicDetails.getHomeDeliveryRemarks()))
                homeDelivery.append("\nWithin " + basicDetails.getHomeDeliveryRemarks());

            tv_delivery.setText(homeDelivery);

            ll_payment_mode.setVisibility(View.VISIBLE);
            ll_payment_mode.setVisibility(View.VISIBLE);
        } else {
            ll_payment_mode.setVisibility(View.GONE);
            ll_payment_mode.setVisibility(View.GONE);
        }

        tv_timings.setText(basicDetails.getStartTime() + " - " + basicDetails.getCloseTime());

        String sellerId = AppSession.getInstance(this).getSellerId();

        final String authToken = SharedPref.getString(this, SharedPref.AUTH_TOKEN);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        return response.request().newBuilder()
                                .header("Authorization", authToken)
                                .build();
                    }
                }).build();

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();

        picasso.load(Constants.BASE_URL + "sellers/" + sellerId + "/upload/1/images/" + 0)
                .config(Bitmap.Config.RGB_565)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(iv_shop_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        iv_shop_image.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.default_profile));

                    }
                });


        ProfileModel.BusinessDetails businessDetails = profileDetails.getSellerDetails().getBusinessDetails();
        ArrayList<BusinessDetailsModel> business = AppSession.getInstance(ProfileActivity.this).getBusinessDetails();

        if (business != null && business.size() > 0 && businessDetails != null)
            for (BusinessDetailsModel model : business) {
                if (model != null && !Utility.isEmpty(model.getBusinessId()) && !Utility.isEmpty(businessDetails.getBusinessType())
                        && model.getBusinessId().equalsIgnoreCase(businessDetails.getBusinessType())) {
                    tv_business_type.setText(model.getBusinessName());
                }
            }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.profile));
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeSellerProfileApiCall();
    }

    private void makeSellerProfileApiCall() {
        new RetrofitHelper(this).getSellerDetails(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        JSONObject profileJson = jsonObject.getJSONObject("result");
                        Type classType = new TypeToken<ProfileModel>() {
                        }.getType();

                        ProfileModel profileModel = new Gson().fromJson(profileJson.toString(), classType);
                        AppSession.getInstance(ProfileActivity.this).setSellerProfile(profileModel);

                        JSONArray paymentModesArray = jsonObject.getJSONArray("payment_modes");
                        classType = new TypeToken<ArrayList<MainCategory>>() {
                        }.getType();

                        ArrayList<MainCategory> paymentModes = new Gson().fromJson(paymentModesArray.toString(), classType);
                        AppSession.getInstance(ProfileActivity.this).setPaymentModes(paymentModes);

                        profileDetails = AppSession.getInstance(ProfileActivity.this).getSellerProfile();
                        setUpData();
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {

            }
        });
    }

    private void setUpListeners() {

        tv_basic_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = RegistrationResolver.getNextIntent(ProfileActivity.this, 0);
                intent.putExtra(Constants.PROFILE_MODEL, profileDetails);
                startActivity(intent);

            }
        });

        tv_other_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = RegistrationResolver.getNextIntent(ProfileActivity.this, 1);
                intent.putExtra(Constants.PROFILE_MODEL, profileDetails);
                startActivity(intent);

            }
        });

        tv_view_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileModel.BusinessDetails businessDetails = profileDetails.getSellerDetails().getBusinessDetails();
                if (businessDetails != null && businessDetails.getDocuments() != null && businessDetails.getDocuments().size() > 0) {
                    Intent intent = new Intent(ProfileActivity.this, ImagePreviewActivity_.class);
                    intent.putExtra(Constants.FILE_URI, businessDetails.getDocuments());
                    intent.putExtra(Constants.IMAGE_TYPE, Constants.TYPE_URL_FILE);
                    startActivity(intent);
                } else
                    showSnackBar(getString(R.string.no_copies));
            }
        });

        tv_edit_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (profileDetails.getSellerDetails().getBusinessDetails() != null) {
                    Intent intent = RegistrationResolver.getNextIntent(ProfileActivity.this, 2);
                    intent.putExtra(Constants.BUSINESS_MODEL, profileDetails.getSellerDetails().getBusinessDetails().getBusinessType());
                    startActivity(intent);
                }
            }
        });
    }

}
