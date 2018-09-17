package com.binbill.seller.Dashboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.Constants;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Registration.RegistrationResolver;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;

@EActivity(R.layout.activity_profile)
public class ProfileActivity extends AppCompatActivity {
    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ImageView iv_shop_image;

    @ViewById
    TextView tv_shop_name, tv_shop_address, tv_main_category, tv_open_days, tv_timings, tv_delivery, tv_payment_modes, tv_view_attachment;

    @ViewById
    TextView tv_other_details;
    private ProfileModel profileDetails;

    @AfterViews
    public void initiateViews() {

        profileDetails = AppSession.getInstance(this).getSellerProfile();
        if (profileDetails == null)
            onBackPressed();
        setUpToolbar();
        setUpListeners();
        setUpData();
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

        ArrayList<MainCategory> paymentModesModel = AppSession.getInstance(this).getPaymentModes();
        String paymentModes = basicDetails.getPaymentModes();
        StringBuilder displayPaymentModes = new StringBuilder();
        if (!Utility.isEmpty(paymentModes)) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(paymentModes.split(",")));
            for (String pay : list) {

                for (MainCategory paymentMode : paymentModesModel) {
                    if (pay.equalsIgnoreCase(paymentMode.getId())){
                        displayPaymentModes = displayPaymentModes.append(pay);
                        displayPaymentModes = displayPaymentModes.append(" | ");
                    }
                }
            }

//            displayPaymentModes.deleteCharAt(displayOpenDays.lastIndexOf("|"));
        }

        tv_payment_modes.setText(displayPaymentModes);
        tv_timings.setText(basicDetails.getStartTime() + " - " + basicDetails.getCloseTime());

        StringBuilder homeDelivery = new StringBuilder();

        if (basicDetails.getHomeDelivery().equalsIgnoreCase("true"))
            homeDelivery.append("Yes");
        else
            homeDelivery.append("No");

        if (!Utility.isEmpty(basicDetails.getHomeDeliveryRemarks()))
            homeDelivery.append("\nWithin " + basicDetails.getHomeDeliveryRemarks());
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.profile));
    }

    private void setUpListeners() {
        tv_other_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = RegistrationResolver.getNextIntent(ProfileActivity.this, 1);
                intent.putExtra(Constants.PROFILE_MODEL, profileDetails);
                startActivity(intent);

            }
        });
    }

}
