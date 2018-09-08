package com.binbill.seller.Dashboard;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private ProfileModel profileDetails;

    @AfterViews
    public void initiateViews() {

        profileDetails = AppSession.getInstance(this).getSellerProfile();
        if (profileDetails == null)
            onBackPressed();
        setUpToolbar();
        setUpListeners();
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
