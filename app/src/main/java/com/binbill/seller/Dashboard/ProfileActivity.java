package com.binbill.seller.Dashboard;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_profile)
public class ProfileActivity extends AppCompatActivity {
    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ImageView iv_shop_image;

    @ViewById
    TextView tv_shop_name, tv_shop_address,tv_main_category, tv_open_days, tv_timings, tv_delivery, tv_payment_modes, tv_view_attachment;
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
        tv_shop_name.setText(profileDetails.getName());
        tv_shop_address.setText(profileDetails.getAddress());
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.profile));
    }

    private void setUpListeners() {

    }

}
