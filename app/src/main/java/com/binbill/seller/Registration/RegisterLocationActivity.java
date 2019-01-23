package com.binbill.seller.Registration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.regex.Matcher;

@EActivity(R.layout.activity_registration_location)
public class RegisterLocationActivity extends BaseActivity {

    public static String address;

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    EditText et_share_location, et_shop_name, et_shop_no, et_shop_pin;

    @ViewById
    SquareAppButton btn_next;

    @ViewById
    RelativeLayout mainLay;

    int PLACE_PICKER_REQUEST = 1;
    AlertDialog.Builder builder;
    UserRegistrationDetails userRegistrationDetails;
    String Lat,Lng;

    public static boolean isFromProfile;

    @AfterViews
    public void initiateViews() {
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        setUpToolbar();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.share_location_header));
    }

    @Click(R.id.btn_next)
    public void onNext(View viewProceed){
        Utility.hideKeyboard(this, btn_next);
        String share_locationStr = et_share_location.getText().toString();
        String shop_nameStr = et_shop_name.getText().toString();
        String shop_pinStr = et_shop_pin.getText().toString();

        saveUserProfileLocally();

        if (!Utility.isEmpty(share_locationStr) && !Utility.isEmpty(shop_nameStr)
                && !Utility.isEmpty(shop_pinStr)) {
            if(isFromProfile){

                HashMap<String, String> map = new HashMap<>();
                UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();

                if (!Utility.isEmpty(userRegistrationDetails.getShopName()))
                    map.put("seller_name", userRegistrationDetails.getShopName());
                if (!Utility.isEmpty(userRegistrationDetails.getBusinessName()))
                    map.put("business_name", userRegistrationDetails.getBusinessName());
                if (!Utility.isEmpty(userRegistrationDetails.getShop_address()))
                    map.put("address", userRegistrationDetails.getShop_address());
                if (!Utility.isEmpty(userRegistrationDetails.getPincode()))
                    map.put("pincode", userRegistrationDetails.getPincode());
                /*if (userRegistrationDetails.getState() != null)
                    map.put("state_id", userRegistrationDetails.getState().getStateId());
                if (userRegistrationDetails.getCity() != null)
                    map.put("city_id", userRegistrationDetails.getCity().getCityId());
                if (userRegistrationDetails.getLocality() != null)
                    map.put("locality_id", userRegistrationDetails.getLocality().getLocalityId());*/
                if (userRegistrationDetails.getDaysOpen() != null && userRegistrationDetails.getDaysOpen().size() > 0)
                    map.put("shop_open_day", TextUtils.join(",", userRegistrationDetails.getDaysOpen()));
                if (!Utility.isEmpty(userRegistrationDetails.getHomeDeliveryDistance()))
                    map.put("home_delivery_remarks", userRegistrationDetails.getHomeDeliveryDistance());
                if (userRegistrationDetails.getPaymentOptions() != null && userRegistrationDetails.getPaymentOptions().size() > 0)
                    map.put("payment_modes", TextUtils.join(",", userRegistrationDetails.getPaymentOptions()));


                new RetrofitHelper(this).updateBasicDetails(userRegistrationDetails.getId(), map, new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                       // handleResponse(response);
                        finish();
                        isFromProfile = false;
                    }

                    @Override
                    public void onErrorResponse() {
                      //  handleError();
                        finish();
                        isFromProfile = false;
                    }
                });





            }else{
                int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
                Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        }else{
            Toast.makeText(this, "Please fill all mandatory fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveUserProfileLocally()
    {
        userRegistrationDetails.setShop_address(et_share_location.getText().toString().trim());
        userRegistrationDetails.setLatitude(Lat);
        userRegistrationDetails.setLongitude(Lng);
        userRegistrationDetails.setShopName(et_shop_name.getText().toString().trim());
        userRegistrationDetails.setShop_no(et_shop_no.getText().toString());
        userRegistrationDetails.setShop_pin(et_shop_pin.getText().toString().trim());

        AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(address!=null && !address.equalsIgnoreCase("")){
            et_share_location.setText(address);
        }
    }

    @Click(R.id.et_share_location)
    public void onLocationClick(View view){
       /* Intent intent = new Intent(RegisterLocationActivity.this,
                CurrentLocationActivity.class);
        startActivity(intent);*/
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }catch (Exception  e){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Alert")
                    .setMessage("Either your Google Play Services are not updated or GoogleMaps not" +
                            " available")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                //String toastMsg = String.format("Place: %s", place.getAddress());
                String address = (String) place.getAddress();
                LatLng latLng = place.getLatLng();
                Double l1=latLng.latitude;
                Double l2=latLng.longitude;
                Lat = l1.toString();
                Lng = l2.toString();
                //String Latitude = (String) ;
                et_share_location.setText(address.trim());
                String[] splitAddress = address.split(",");
                String state_pin = splitAddress[splitAddress.length-2];
                String[] splitPin = state_pin.split("\\s");
                String pin = splitPin[splitPin.length-1];
                et_shop_pin.setText(pin.trim());
               //  Toast.makeText(this,"", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Click(R.id.mainLay)
    public void onOutsideClick(View view){
        Utility.hideKeyboard(RegisterLocationActivity.this, mainLay);
    }


    private boolean isValidDetails() {

        /**
         * Location
         */
        String share_locationStr = et_share_location.getText().toString();
        if (!Utility.isEmpty(share_locationStr)) {
            return true;
        }

        /**
         * Shop Name
         */
        String shop_nameStr = et_shop_name.getText().toString();
        if (!Utility.isEmpty(shop_nameStr)) {
            return true;
        }

       /* *//**
         * Shop No.
         *//*
        String shop_no = et_shop_no.getText().toString();
        if (!Utility.isEmpty(shop_no)) {

            return true;
        }*/

        /**
         * Shop No.
         */
        String shop_pin = et_shop_pin.getText().toString();
        if (!Utility.isEmpty(shop_pin) && shop_pin.length()==6 ) {

            return  true;
        }

        return false;
    }

}
