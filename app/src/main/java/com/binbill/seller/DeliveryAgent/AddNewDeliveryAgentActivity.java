package com.binbill.seller.DeliveryAgent;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Order.DeliveryModel;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_add_new_delivery_agent)
public class AddNewDeliveryAgentActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    EditText et_name, et_mobile;

    @ViewById
    TextView tv_error_mobile;

    @ViewById
    AppButton btn_save;

    @ViewById
    ProgressBar btn_save_progress;
    private String mType = "CREATE";

    @AfterViews
    public void setUpView() {
        setUpToolbar();
        setUpListeners();

        if (getIntent() != null && getIntent().hasExtra(Constants.EDIT_DELIVERY_BOY)) {
            DeliveryModel userModel = (DeliveryModel) getIntent().getSerializableExtra(Constants.EDIT_DELIVERY_BOY);
            setUpData(userModel);
        }

        enableDisableSaveButton();
    }

    private void setUpData(DeliveryModel userModel) {
        mType = Constants.EDIT_DELIVERY_BOY;

    }

    private void setUpListeners() {

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.add_delivery_boy));
    }

    private void enableDisableSaveButton() {

        String name = et_name.getText().toString();
        String mobile = et_mobile.getText().toString();

        if (!Utility.isEmpty(name) && !Utility.isEmpty(mobile)) {
            Utility.enableButton(this, btn_save, true);
        } else
            Utility.enableButton(this, btn_save, false);
    }


}
