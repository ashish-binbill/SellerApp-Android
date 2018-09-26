package com.binbill.seller.Registration;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Dashboard.ProfileModel;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

@EActivity(R.layout.activity_basic_details2)
public class BasicDetails2Activity extends BaseActivity implements OptionListFragment.OnOptionListInteractionListener {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    AppButton btn_submit;

    @ViewById
    LinearLayout btn_submit_progress;

    @ViewById
    FrameLayout container;

    @ViewById
    NestedScrollView scroll_view;

    @ViewById
    RadioGroup radio_group_home_delivery;

    @ViewById
    TextView tv_monday, tv_tuesday, tv_wednesday, tv_thursday, tv_friday, tv_saturday, tv_sunday,
            start_time, end_time;

    @ViewById
    TextView tv_delivery_header, tv_delivery_remark, tv_payment_modes_header;

    @ViewById
    LinearLayout ll_payment_2, ll_payment_1;

    @ViewById
    CheckBox cb_paytm, cb_cod, cb_credit_debit, cb_other_wallets;

    @ViewById
    EditText et_delivery_distance;

    UserRegistrationDetails userRegistrationDetails;
    String shopOpenTime = "9:00 AM";
    String shopCloseTime = "9:00 PM";
    private String mMode;
    private String sellerId;
    private ProfileModel.BasicDetails basicDetails;

    @AfterViews
    public void initiateViews() {
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();

        setUpViews();
        setUpToolbar();
        setUpListeners();

        enableDisableVerifyButton();
        Utility.hideKeyboard(this, btn_submit);

        if (getIntent().hasExtra(Constants.PROFILE_MODEL)) {
            mMode = Constants.EDIT_MODE;
            ProfileModel profileModel = (ProfileModel) getIntent().getSerializableExtra(Constants.PROFILE_MODEL);
            sellerId = profileModel.getId();

            if (profileModel.getSellerDetails() != null)
                basicDetails = profileModel.getSellerDetails().getBasicDetails();

            setUpData();
        }

        if (userRegistrationDetails != null && userRegistrationDetails.isAssisted() && !userRegistrationDetails.isFmcg()) {
            tv_delivery_header.setVisibility(View.GONE);
            radio_group_home_delivery.setVisibility(View.GONE);
            tv_delivery_remark.setVisibility(View.GONE);
            et_delivery_distance.setVisibility(View.GONE);

            tv_payment_modes_header.setVisibility(View.GONE);
            ll_payment_1.setVisibility(View.GONE);
            ll_payment_2.setVisibility(View.GONE);
        }
    }

    private void setUpData() {
        start_time.setText(basicDetails.getStartTime());
        end_time.setText(basicDetails.getCloseTime());

        String shopOpenDays = basicDetails.getShopOpenDays();
        ArrayList<String> list = new ArrayList<>(Arrays.asList(shopOpenDays.split(",")));

        for (String shopOpen : list)
            switch (Integer.parseInt(shopOpen)) {
                case 0:
                    tv_sunday.callOnClick();
                    break;
                case 1:
                    tv_monday.callOnClick();
                    break;
                case 2:
                    tv_tuesday.callOnClick();
                    break;
                case 3:
                    tv_wednesday.callOnClick();
                    break;
                case 4:
                    tv_thursday.callOnClick();
                    break;
                case 5:
                    tv_friday.callOnClick();
                    break;
                case 6:
                    tv_saturday.callOnClick();
                    break;

            }

        String paymentMode = basicDetails.getPaymentModes();

        ArrayList<String> paymentModes = new ArrayList<>(Arrays.asList(paymentMode.split(",")));

        for (String mode : paymentModes) {

            if (cb_paytm.getTag() != null && cb_paytm.getTag().toString().equalsIgnoreCase(mode))
                cb_paytm.setChecked(true);
            if (cb_cod.getTag() != null && cb_cod.getTag().toString().equalsIgnoreCase(mode))
                cb_cod.setChecked(true);
            if (cb_credit_debit.getTag() != null && cb_credit_debit.getTag().toString().equalsIgnoreCase(mode))
                cb_credit_debit.setChecked(true);
            if (cb_other_wallets.getTag() != null && cb_other_wallets.getTag().toString().equalsIgnoreCase(mode))
                cb_other_wallets.setChecked(true);
        }

        if (!Utility.isEmpty(basicDetails.getHomeDelivery()) && basicDetails.getHomeDelivery().equalsIgnoreCase("true"))
            radio_group_home_delivery.check(R.id.yes);
        else
            radio_group_home_delivery.check(R.id.no);

        if (!Utility.isEmpty(basicDetails.getHomeDeliveryRemarks()))
            et_delivery_distance.setText(basicDetails.getHomeDeliveryRemarks());

        enableDisableVerifyButton();
    }

    private void setUpViews() {

        ArrayList<MainCategory> payments = AppSession.getInstance(this).getPaymentModes();
        if (payments != null && payments.size() > 0) {

            if (payments.get(0) != null) {
                cb_paytm.setText(payments.get(0).getName());
                cb_paytm.setTag(payments.get(0).getId());
            } else
                cb_paytm.setVisibility(View.GONE);

            if (payments.get(1) != null) {
                cb_cod.setText(payments.get(1).getName());
                cb_cod.setTag(payments.get(1).getId());
            } else
                cb_cod.setVisibility(View.GONE);

            if (payments.get(2) != null) {
                cb_credit_debit.setText(payments.get(2).getName());
                cb_credit_debit.setTag(payments.get(2).getId());
            } else
                cb_credit_debit.setVisibility(View.GONE);

            if (payments.get(3) != null) {
                cb_other_wallets.setText(payments.get(3).getName());
                cb_other_wallets.setTag(payments.get(3).getId());
            } else
                cb_other_wallets.setVisibility(View.GONE);
        }

    }

    private void enableDisableVerifyButton() {
        int radioButtonID = radio_group_home_delivery.getCheckedRadioButtonId();
        AppCompatRadioButton radioButton = (AppCompatRadioButton) radio_group_home_delivery.findViewById(radioButtonID);
        final String isHomeDelivery = radioButton.getText().toString();

        boolean isdeliveryValidated = false;

        if (isHomeDelivery.equalsIgnoreCase(getString(R.string.yes))) {
            if (!Utility.isEmpty(et_delivery_distance.getText().toString()))
                isdeliveryValidated = true;
        } else
            isdeliveryValidated = true;


        if (isAnyDaySelected() && isdeliveryValidated
                && isAnyModeOfPaymentSelected())
            Utility.enableButton(this, btn_submit, true);
        else
            Utility.enableButton(this, btn_submit, false);
    }

    private boolean isAnyDaySelected() {

        if (((String) tv_monday.getTag()).equalsIgnoreCase("1") || ((String) tv_tuesday.getTag()).equalsIgnoreCase("1") || ((String) tv_wednesday.getTag()).equalsIgnoreCase("1") ||
                ((String) tv_thursday.getTag()).equalsIgnoreCase("1") || ((String) tv_friday.getTag()).equalsIgnoreCase("1") || ((String) tv_saturday.getTag()).equalsIgnoreCase("1") ||
                ((String) tv_sunday.getTag()).equalsIgnoreCase("1"))
            return true;

        return false;
    }

    private ArrayList<String> getDaysSelected() {
        ArrayList<String> daysList = new ArrayList<>();
        if (((String) tv_monday.getTag()).equalsIgnoreCase("1"))
            daysList.add("1");

        if (((String) tv_tuesday.getTag()).equalsIgnoreCase("1"))
            daysList.add("2");

        if (((String) tv_wednesday.getTag()).equalsIgnoreCase("1"))
            daysList.add("3");

        if (((String) tv_thursday.getTag()).equalsIgnoreCase("1"))
            daysList.add("4");

        if (((String) tv_friday.getTag()).equalsIgnoreCase("1"))
            daysList.add("5");

        if (((String) tv_saturday.getTag()).equalsIgnoreCase("1"))
            daysList.add("6");

        if (((String) tv_sunday.getTag()).equalsIgnoreCase("1"))
            daysList.add("0");


        return daysList;
    }

    private boolean isAnyModeOfPaymentSelected() {

        if (cb_paytm.isChecked() || cb_cod.isChecked() || cb_credit_debit.isChecked() || cb_other_wallets.isChecked())
            return true;
        return false;
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

    private void makeUploadDataToServerCall() {

        saveDataInLocalObject();

        HashMap<String, String> map = new HashMap<>();
        UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();

        if (!Utility.isEmpty(userRegistrationDetails.getShopName()))
            map.put("seller_name", userRegistrationDetails.getShopName());
        if (!Utility.isEmpty(userRegistrationDetails.getBusinessName()))
            map.put("business_name", userRegistrationDetails.getBusinessName());
        if (!Utility.isEmpty(userRegistrationDetails.getBusinessAddress()))
            map.put("address", userRegistrationDetails.getBusinessAddress());
        if (!Utility.isEmpty(userRegistrationDetails.getPincode()))
            map.put("pincode", userRegistrationDetails.getPincode());
        if (userRegistrationDetails.getState() != null)
            map.put("state_id", userRegistrationDetails.getState().getStateId());
        if (userRegistrationDetails.getCity() != null)
            map.put("city_id", userRegistrationDetails.getCity().getCityId());
        if (userRegistrationDetails.getLocality() != null)
            map.put("locality_id", userRegistrationDetails.getLocality().getLocalityId());
        if (userRegistrationDetails.getDaysOpen() != null && userRegistrationDetails.getDaysOpen().size() > 0)
            map.put("shop_open_day", TextUtils.join(",", userRegistrationDetails.getDaysOpen()));
        if (!Utility.isEmpty(userRegistrationDetails.getShopOpen()))
            map.put("start_time", userRegistrationDetails.getShopOpen());
        if (!Utility.isEmpty(userRegistrationDetails.getShopClose()))
            map.put("close_time", userRegistrationDetails.getShopClose());
        map.put("home_delivery", String.valueOf(userRegistrationDetails.isHomeDelivery()));
        if (!Utility.isEmpty(userRegistrationDetails.getHomeDeliveryDistance()))
            map.put("home_delivery_remarks", userRegistrationDetails.getHomeDeliveryDistance());
        if (userRegistrationDetails.getPaymentOptions() != null && userRegistrationDetails.getPaymentOptions().size() > 0)
            map.put("payment_modes", TextUtils.join(",", userRegistrationDetails.getPaymentOptions()));


        new RetrofitHelper(this).updateBasicDetails(userRegistrationDetails.getId(), map, new RetrofitHelper.RetrofitCallback() {
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
        btn_submit.setVisibility(View.VISIBLE);
        btn_submit_progress.setVisibility(View.GONE);

        showSnackBar(getString(R.string.something_went_wrong));
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("status")) {

                if (mMode == Constants.EDIT_MODE) {
                    finish();
                } else {

                    int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
                    Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                btn_submit.setVisibility(View.VISIBLE);
                btn_submit_progress.setVisibility(View.GONE);

            } else
                handleError();
        } catch (JSONException e) {
            handleError();
        }
    }

    private void saveDataInLocalObject() {
        userRegistrationDetails.setDaysOpen(getDaysSelected());
        userRegistrationDetails.setShopOpen(shopOpenTime);
        userRegistrationDetails.setShopClose(shopCloseTime);

        int radioButtonID = radio_group_home_delivery.getCheckedRadioButtonId();
        AppCompatRadioButton radioButton = (AppCompatRadioButton) radio_group_home_delivery.findViewById(radioButtonID);
        final String isHomeDelivery = radioButton.getText().toString();

        if (isHomeDelivery.equalsIgnoreCase(getString(R.string.yes)))
            userRegistrationDetails.setHomeDelivery(true);
        else
            userRegistrationDetails.setHomeDelivery(false);

        if (userRegistrationDetails.isHomeDelivery())
            userRegistrationDetails.setHomeDeliveryDistance(et_delivery_distance.getText().toString());

        ArrayList<String> paymentModes = new ArrayList<>();
        if (cb_paytm.isChecked())
            paymentModes.add(cb_paytm.getTag().toString());

        if (cb_cod.isChecked())
            paymentModes.add(cb_cod.getTag().toString());

        if (cb_other_wallets.isChecked())
            paymentModes.add(cb_other_wallets.getTag().toString());

        if (cb_credit_debit.isChecked())
            paymentModes.add(cb_credit_debit.getTag().toString());

        userRegistrationDetails.setPaymentOptions(paymentModes);
        AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);
    }

    CheckBox.OnCheckedChangeListener checkBoxCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            enableDisableVerifyButton();
        }
    };

    private void setUpListeners() {
        tv_monday.setOnClickListener(daySelectionListener);
        tv_tuesday.setOnClickListener(daySelectionListener);
        tv_wednesday.setOnClickListener(daySelectionListener);
        tv_thursday.setOnClickListener(daySelectionListener);
        tv_friday.setOnClickListener(daySelectionListener);
        tv_saturday.setOnClickListener(daySelectionListener);
        tv_sunday.setOnClickListener(daySelectionListener);

        start_time.setOnClickListener(timeSelectionListener);
        end_time.setOnClickListener(timeSelectionListener);

        cb_paytm.setOnCheckedChangeListener(checkBoxCheckedChangeListener);
        cb_cod.setOnCheckedChangeListener(checkBoxCheckedChangeListener);
        cb_credit_debit.setOnCheckedChangeListener(checkBoxCheckedChangeListener);
        cb_other_wallets.setOnCheckedChangeListener(checkBoxCheckedChangeListener);

        radio_group_home_delivery.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                enableDisableVerifyButton();
            }
        });

        et_delivery_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(BasicDetails2Activity.this, btn_submit);

                ArrayList<String> deliveryDistanceList = new ArrayList<>();
                deliveryDistanceList.add("1 km");
                deliveryDistanceList.add("2 km");
                deliveryDistanceList.add("3 km");
                deliveryDistanceList.add("4 km");
                deliveryDistanceList.add("5 km");
                deliveryDistanceList.add(">5 km");

                OptionListFragment optionListFragment = OptionListFragment.newInstance(deliveryDistanceList, Constants.DELIVERY_DISTANCE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, optionListFragment, "OptionListFragment");
                transaction.commitAllowingStateLoss();
                container.setVisibility(View.VISIBLE);
                scroll_view.setVisibility(View.GONE);
            }
        });
    }

    View.OnClickListener timeSelectionListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            final TextView viewSelected = (TextView) view;
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(BasicDetails2Activity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    String AM_PM;
                    if (selectedHour < 12) {
                        AM_PM = "AM";
                    } else {
                        AM_PM = "PM";
                        selectedHour = selectedHour - 12;
                    }

                    Calendar datetime = Calendar.getInstance();
                    datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    datetime.set(Calendar.MINUTE, selectedMinute);

                    String curTime = String.format("%02d:%02d", datetime.get(Calendar.HOUR_OF_DAY), datetime.get(Calendar.MINUTE));
                    viewSelected.setText(curTime + " " + AM_PM);

                    if (view.getId() == R.id.start_time)
                        shopOpenTime = curTime + " " + AM_PM;
                    else
                        shopCloseTime = curTime + " " + AM_PM;

                }
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    };

    View.OnClickListener daySelectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String isSelected = (String) view.getTag();

            TextView textView = (TextView) view;
            if (isSelected.equalsIgnoreCase("0")) {
                textView.setBackground(ContextCompat.getDrawable(BasicDetails2Activity.this, R.drawable.day_selected));
                textView.setTag("1");
            } else {
                textView.setBackground(ContextCompat.getDrawable(BasicDetails2Activity.this, R.drawable.day_unselected));
                textView.setTag("0");
            }

            enableDisableVerifyButton();
        }
    };

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.basic_details_2));
    }

    @Override
    public void onListItemSelected(Object item, int identifier) {

        String selectedItem = (String) item;
        et_delivery_distance.setText(selectedItem);

        container.setVisibility(View.GONE);
        scroll_view.setVisibility(View.VISIBLE);
        scroll_view.smoothScrollTo(0, et_delivery_distance.getBottom());

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("OptionListFragment");
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        enableDisableVerifyButton();
    }

    @Override
    public void onCancel() {
        container.setVisibility(View.GONE);
        scroll_view.setVisibility(View.VISIBLE);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("OptionListFragment");
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        enableDisableVerifyButton();
    }

    public boolean isValid() {
        int radioButtonID = radio_group_home_delivery.getCheckedRadioButtonId();
        AppCompatRadioButton radioButton = (AppCompatRadioButton) radio_group_home_delivery.findViewById(radioButtonID);
        final String isHomeDelivery = radioButton.getText().toString();

        boolean isdeliveryValidated = false;

        if (isHomeDelivery.equalsIgnoreCase(getString(R.string.yes))) {
            if (!Utility.isEmpty(et_delivery_distance.getText().toString())) {
                isdeliveryValidated = true;
            }
        } else
            isdeliveryValidated = true;


        if (isAnyDaySelected() && isdeliveryValidated
                && isAnyModeOfPaymentSelected())
            return true;
        else
            return false;
    }
}
