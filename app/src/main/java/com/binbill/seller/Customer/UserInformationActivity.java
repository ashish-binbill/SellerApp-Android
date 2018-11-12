package com.binbill.seller.Customer;

import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_user_information)
public class UserInformationActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ViewPager view_pager;

    @ViewById
    TabLayout tab_strip;
    private UserFragmentPagerAdapter mPagerAdapter;
    private UserModel userModel;

    @AfterViews
    public void setUpView() {
        setUpViewPager();
        setUpToolbar();
    }

    private void setUpViewPager() {
        userModel = (UserModel) getIntent().getSerializableExtra(Constants.USER_MODEL);
        mPagerAdapter = new UserFragmentPagerAdapter(getSupportFragmentManager(), userModel);

        if (view_pager != null) {
            view_pager.setOffscreenPageLimit(0);
            view_pager.setAdapter(mPagerAdapter);
        }

        if (tab_strip != null) {
            tab_strip.setupWithViewPager(view_pager);
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        if(!Utility.isEmpty(userModel.getUserName()))
        toolbarText.setText(userModel.getUserName());
        else
            toolbarText.setText(getString(R.string.customer_details));
    }


}
