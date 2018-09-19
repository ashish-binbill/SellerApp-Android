package com.binbill.seller.Dashboard;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_wallet)
public class WalletActivity extends BaseActivity {
    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    TextView tv_wallet;

    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        setUpData();
    }

    private void setUpData() {

        ProfileModel profileModel = AppSession.getInstance(this).getSellerProfile();
        if (!Utility.isEmpty(profileModel.getCashBack()))
            tv_wallet.setText(profileModel.getCashBack());
        else
            tv_wallet.setText("0");

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.my_wallet));
    }

}
