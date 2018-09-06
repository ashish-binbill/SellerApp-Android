package com.binbill.seller.Registration;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_auto_eeservices)
public class AutoEEServicesActivity extends BaseActivity {
    @ViewById
    AppButton btn_submit;

    @ViewById
    LinearLayout btn_submit_progress;

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    CheckBox cb_electronics, cb_accessories, cb_insurance, cb_amc, cb_repair, cb_asc;

    @ViewById
    LinearLayout ll_electronics, ll_accessories, ll_insurance, ll_amc, ll_repair, ll_asc;

    UserRegistrationDetails userRegistrationDetails;

    @AfterViews
    public void initialiseViews() {
        setUpToolbar();
        setUpListeners();
        enableDisableVerifyButton();

        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
    }

    private void setUpListeners() {

        cb_electronics.setOnCheckedChangeListener(checkedChangeListener);
        cb_accessories.setOnCheckedChangeListener(checkedChangeListener);
        cb_amc.setOnCheckedChangeListener(checkedChangeListener);
        cb_insurance.setOnCheckedChangeListener(checkedChangeListener);
        cb_repair.setOnCheckedChangeListener(checkedChangeListener);
        cb_asc.setOnCheckedChangeListener(checkedChangeListener);

        ll_electronics.setOnClickListener(itemClickListener);
        ll_accessories.setOnClickListener(itemClickListener);
        ll_amc.setOnClickListener(itemClickListener);
        ll_asc.setOnClickListener(itemClickListener);
        ll_insurance.setOnClickListener(itemClickListener);
        ll_repair.setOnClickListener(itemClickListener);
    }

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_electronics:
                    setCheckedState(cb_electronics);
                    break;
                case R.id.ll_accessories:
                    setCheckedState(cb_accessories);
                    break;
                case R.id.ll_insurance:
                    setCheckedState(cb_insurance);
                    break;
                case R.id.ll_amc:
                    setCheckedState(cb_amc);
                    break;
                case R.id.ll_repair:
                    setCheckedState(cb_repair);
                    break;
                case R.id.ll_asc:
                    setCheckedState(cb_asc);
                    break;

            }
        }
    };

    private void setCheckedState(CheckBox checkBox) {

        if (checkBox.isChecked())
            checkBox.setChecked(false);
        else
            checkBox.setChecked(true);
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

            if (checked) {
                if (compoundButton.getId() != R.id.cb_asc)
                    cb_asc.setChecked(false);
                else {
                    cb_electronics.setChecked(false);
                    cb_accessories.setChecked(false);
                    cb_insurance.setChecked(false);
                    cb_amc.setChecked(false);
                    cb_repair.setChecked(false);
                }
            }
            enableDisableVerifyButton();
        }
    };

    private void enableDisableVerifyButton() {

        if (cb_accessories.isChecked() || cb_amc.isChecked() || cb_electronics.isChecked() || cb_insurance.isChecked() ||
                cb_repair.isChecked() || cb_asc.isChecked())
            Utility.enableButton(this, btn_submit, true);
        else
            Utility.enableButton(this, btn_submit, false);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(R.string.select_services);
    }

    @Click(R.id.btn_submit)
    public void onSubmitClicked(View viewRegister) {
        Utility.hideKeyboard(this, btn_submit);
        if (isValid()) {
            btn_submit.setVisibility(View.GONE);
            btn_submit_progress.setVisibility(View.VISIBLE);

            makeUploadDataToServerCall();

        }
    }

    private boolean isValid() {
        if (cb_accessories.isChecked() || cb_amc.isChecked() || cb_electronics.isChecked() || cb_insurance.isChecked() ||
                cb_repair.isChecked() || cb_asc.isChecked())
            return true;
        return false;
    }

    private void makeUploadDataToServerCall() {

        saveDataInModel();
        int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
        Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
        startActivity(intent);

        btn_submit.setVisibility(View.VISIBLE);
        btn_submit_progress.setVisibility(View.GONE);
    }

    private void saveDataInModel() {
        ArrayList<String> services = new ArrayList<>();

        if (cb_electronics.isChecked())
            services.add(Constants.ELECTRONICS);
        if (cb_accessories.isChecked())
            services.add(Constants.ACCESSORIES);
        if (cb_insurance.isChecked())
            services.add(Constants.INSURANCE);
        if (cb_amc.isChecked())
            services.add(Constants.AMC);
        if (cb_repair.isChecked())
            services.add(Constants.REPAIR);
        if (cb_asc.isChecked())
            services.add(Constants.ASC);

        userRegistrationDetails.setAutoEEServices(services);
        AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);
    }
}
