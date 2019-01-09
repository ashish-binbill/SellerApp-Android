package com.binbill.seller.Dashboard;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_manage_fruits_veg)
public class ManageFruitsVegActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById(R.id.btn_no_data)
    SquareAppButton btnNoData;

    @ViewById(R.id.btn_add_data)
    SquareAppButton btnSave;

    UserRegistrationDetails userRegistrationDetails;
    String title= "";

    @AfterViews
    public void initiateViews() {
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        btnNoData.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        btnSave.setText("Save");
        getIntentData();
        setUpToolbar();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(title);
    }

    private void getIntentData(){
        title = getIntent().getStringExtra("Title");
    }
}
