package com.binbill.seller.HomeDelivery;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.Model.StateCityModel;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Registration.BasicDetails1Activity;
import com.binbill.seller.Registration.RegisterActivity;
import com.binbill.seller.Retrofit.RetrofitApiInterface;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_home_delivery_charges)
public class SetHomeDeliveryActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Switch simpleSwitch;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    SquareAppButton btn_save;

    @ViewById
    TextView tv_amt_less_100, tv_amt_101_300, tv_amt_301_499, tv_amt_500;

    @ViewById
    EditText et_amt_less_100, et_amt_101_300, et_amt_301_499, et_amt_500;

    boolean amount_100, amount_101, amount_301, amount_500;

    Boolean switchState;

    UserRegistrationDetails userRegistrationDetails;

    public static boolean isFirstTimeLaunched;

    @AfterViews
    public void setUpView() {
        setUpToolbar();
        setUpListeners();
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText("Set Home Delivery Charges");

    }

    private void setUpListeners(){

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    et_amt_101_300.setFocusable(false);
                    et_amt_less_100.setFocusable(false);
                    et_amt_301_499.setFocusable(false);
                    et_amt_500.setFocusable(false);
                    btn_save.setVisibility(View.GONE);
                    if(isChecked && !isFirstTimeLaunched){
                        switchState = simpleSwitch.isChecked();
                        userRegistrationDetails.setHomeDelivery(switchState);
                        makeCallToServer();
                        //isFirstTimeLaunched = true;
                    }
                }else{
                    btn_save.setVisibility(View.VISIBLE);
                    et_amt_101_300.setFocusableInTouchMode(true);
                    et_amt_less_100.setFocusableInTouchMode(true);
                    et_amt_301_499.setFocusableInTouchMode(true);
                    et_amt_500.setFocusableInTouchMode(true);
                }
            }
        });

        et_amt_less_100.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==1 && !s.toString().startsWith("₹")) {
                    et_amt_less_100.setText("₹"+s.toString());
                    Selection.setSelection(et_amt_less_100.getText(), et_amt_less_100.getText().length());
                }
                try {
                    String[] splitAmount = s.toString().trim().split("₹");
                    Integer amount = Integer.parseInt(splitAmount[splitAmount.length - 1]);
                    if (amount > 100.00) {
                        amount_100 = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_amt_101_300.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==1 && !s.toString().startsWith("₹")) {
                    et_amt_101_300.setText("₹"+s.toString());
                    Selection.setSelection(et_amt_101_300.getText(), et_amt_101_300.getText().length());
                }
                try {
                    String[] splitAmount = s.toString().trim().split("₹");
                    Integer amount = Integer.parseInt(splitAmount[splitAmount.length - 1]);
                    if (amount > 300.00) {
                        amount_101 = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_amt_301_499.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==1 && !s.toString().startsWith("₹")) {
                    et_amt_301_499.setText("₹"+s.toString());
                    Selection.setSelection(et_amt_301_499.getText(), et_amt_301_499.getText().length());
                }

                try {
                    String[] splitAmount = s.toString().trim().split("₹");
                    Integer amount = Integer.parseInt(splitAmount[splitAmount.length - 1]);
                    if (amount > 499.00) {
                        amount_301 = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_amt_500.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==1 && !s.toString().startsWith("₹")) {
                    et_amt_500.setText("₹"+s.toString());
                    Selection.setSelection(et_amt_500.getText(), et_amt_500.getText().length());
                }

                try {
                    String[] splitAmount = s.toString().trim().split("₹");
                    Integer amount = Integer.parseInt(splitAmount[splitAmount.length - 1]);
                    if (amount > 500000.00) {
                        amount_500 = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


        new RetrofitHelper(this).fetchDeliveryRules(userRegistrationDetails.getId(),
                new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject payload = new JSONObject(response);
                            JSONArray result = payload.getJSONArray("delivery_rules");
                            HashMap<String, String> hmap;
                            ArrayList<HashMap<String, String>> array = new ArrayList<>();

                            for(int i = 0; i < result.length(); i++){
                                JSONObject obj = result.getJSONObject(i);
                                hmap = new HashMap<>();
                                hmap.put("title", obj.getString("title"));
                                hmap.put("delivery_charges", obj.getString("delivery_charges"));
                                hmap.put("maximum_order_value", obj.getString("maximum_order_value"));
                                hmap.put("minimum_order_value", obj.getString("minimum_order_value"));
                                array.add(hmap);
                            }

                            try {
                                tv_amt_less_100.setText(array.get(0).get("title"));
                                tv_amt_101_300.setText(array.get(1).get("title"));
                                tv_amt_301_499.setText(array.get(2).get("title"));
                                tv_amt_500.setText(array.get(3).get("title"));

                                userRegistrationDetails.setIsAmtLess100(Double.parseDouble(array.get(0)
                                        .get("delivery_charges")));
                                userRegistrationDetails.setIsAmt101_300(Double.parseDouble(array.get(1)
                                        .get("delivery_charges")));
                                userRegistrationDetails.setIsAmt301_499(Double.parseDouble(array.get(2)
                                        .get("delivery_charges")));
                                userRegistrationDetails.setIsAmt_500(Double.parseDouble(array.get(3)
                                        .get("delivery_charges")));

                                et_amt_less_100.setText("₹" + String.valueOf(userRegistrationDetails.getIsAmtLess100()));
                                et_amt_101_300.setText("₹" + String.valueOf(userRegistrationDetails.getIsAmt101_300()));
                                et_amt_301_499.setText("₹" + String.valueOf(userRegistrationDetails.getIsAmt301_499()));
                                et_amt_500.setText("₹" + String.valueOf(userRegistrationDetails.getIsAmt_500()));

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                        }


                    }

                    @Override
                    public void onErrorResponse() {

                    }
                });

        if(!userRegistrationDetails.isHomeDelivery()) {
        }else{
            simpleSwitch.setChecked(true);
            btn_save.setVisibility(View.GONE);
        }
        if (userRegistrationDetails.getIsAmtLess100() != 0)
            et_amt_less_100.setText("₹" + String.valueOf(userRegistrationDetails.getIsAmtLess100()));
        if (userRegistrationDetails.getIsAmt101_300() != 0)
            et_amt_101_300.setText("₹" + String.valueOf(userRegistrationDetails.getIsAmt101_300()));
        if (userRegistrationDetails.getIsAmt301_499() != 0)
            et_amt_301_499.setText("₹" + String.valueOf(userRegistrationDetails.getIsAmt301_499()));
        if (userRegistrationDetails.getIsAmt_500() != 0)
            et_amt_500.setText("₹" + String.valueOf(userRegistrationDetails.getIsAmt_500()));
    }


    @Click(R.id.btn_save)
    public void saveBtn(View v){
        Utility.hideKeyboard(SetHomeDeliveryActivity.this, btn_save);
        switchState = simpleSwitch.isChecked();
        userRegistrationDetails.setHomeDelivery(switchState);
        if(switchState){
            makeCallToServer();
        }else {
            try {

                String[] splitAmount = et_amt_less_100.getText().toString().trim().split("₹");
                Double amount = Double.parseDouble(splitAmount[splitAmount.length - 1]);
                userRegistrationDetails.setIsAmtLess100(amount);
            } catch (Exception e) {
                e.printStackTrace();
                userRegistrationDetails.setIsAmtLess100(0);
            }

            try {
                String[] splitAmount1 = et_amt_101_300.getText().toString().trim().split("₹");
                Double amount1 = Double.parseDouble(splitAmount1[splitAmount1.length - 1]);
                userRegistrationDetails.setIsAmt101_300(amount1);
            } catch (Exception e) {
                e.printStackTrace();
                userRegistrationDetails.setIsAmt101_300(0);
            }

            try {
                String[] splitAmount2 = et_amt_301_499.getText().toString().trim().split("₹");
                Double amount2 = Double.parseDouble(splitAmount2[splitAmount2.length - 1]);
                userRegistrationDetails.setIsAmt301_499(amount2);
            } catch (Exception e) {
                e.printStackTrace();
                userRegistrationDetails.setIsAmt301_499(0);
            }
            try {
                String[] splitAmount3 = et_amt_500.getText().toString().trim().split("₹");
                Double amount3 = Double.parseDouble(splitAmount3[splitAmount3.length - 1]);
                userRegistrationDetails.setIsAmt_500(amount3);
            } catch (Exception e) {
                e.printStackTrace();
                userRegistrationDetails.setIsAmt_500(0);
            }

            AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);

            if (amount_100 || amount_101 || amount_301 || amount_500) {
                Toast.makeText(this, "Please enter amount within valid range", Toast.LENGTH_SHORT).show();
            } else {
               makeCallToServer();
            }
        }


    }

    private void makeCallToServer(){

       // UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        HashMap<String, String> map = new HashMap<>();
        JSONArray array = new JSONArray();

        if(switchState){
            map.put("is_free_delivery", String.valueOf(switchState));
          /*  userRegistrationDetails.setIsAmtLess100(0);
            userRegistrationDetails.setIsAmt101_300(0);
            userRegistrationDetails.setIsAmt301_499(0);
            userRegistrationDetails.setIsAmt_500(0);*/

        }else{
            map.put("is_free_delivery", String.valueOf(switchState));

            try {
                if (!Utility.isEmpty(et_amt_less_100.getText().toString().trim())) {

                    JSONObject typeObject = new JSONObject();
                    typeObject.put("minimum_order_value", 0);
                    typeObject.put("maximum_order_value", 100);
                    String[] splitAmount = et_amt_less_100.getText().toString().trim().split("₹");
                    Double amount = Double.parseDouble(splitAmount[splitAmount.length - 1]);
                    typeObject.put("delivery_charges", amount);
                    array.put(typeObject);
                    //list.add(map_internal);

                }
                if (!Utility.isEmpty(et_amt_101_300.getText().toString().trim())) {

                    JSONObject typeObject = new JSONObject();
                    typeObject.put("minimum_order_value", 100);
                    typeObject.put("maximum_order_value", 300);
                    String[] splitAmount = et_amt_101_300.getText().toString().trim().split("₹");
                    Double amount = Double.parseDouble(splitAmount[splitAmount.length - 1]);
                    typeObject.put("delivery_charges", amount);
                    array.put(typeObject);
                    //list.add(map_internal1);
                }
                if (!Utility.isEmpty(et_amt_301_499.getText().toString().trim())) {
                    JSONObject typeObject = new JSONObject();
                    typeObject.put("minimum_order_value", 300);
                    typeObject.put("maximum_order_value", 500);
                    String[] splitAmount = et_amt_301_499.getText().toString().trim().split("₹");
                    Double amount = Double.parseDouble(splitAmount[splitAmount.length - 1]);
                    typeObject.put("delivery_charges", amount);
                    array.put(typeObject);
                    //list.add(map_internal2);
                }
                if (!Utility.isEmpty(et_amt_500.getText().toString().trim())) {
                    JSONObject typeObject = new JSONObject();
                    typeObject.put("minimum_order_value", 500);
                    typeObject.put("maximum_order_value", 500000);
                    String[] splitAmount = et_amt_500.getText().toString().trim().split("₹");
                    Double amount = Double.parseDouble(splitAmount[splitAmount.length - 1]);
                    typeObject.put("delivery_charges", amount);
                    array.put(typeObject);
                    // list.add(map_internal3);
                }

                try {
                    String priceFormatted = new JSONTokener(array.toString()).nextValue().toString();
                    //  object.put("delivery_rules", priceFormatted);
              /*  String[] dsf = new String[list.size()];
                list.toArray(dsf);*/
                    map.put("delivery_rules", priceFormatted);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }

        String check = String.valueOf(map);

        new RetrofitHelper(this).updateSellerDeliverRules(userRegistrationDetails.getId(), map, new RetrofitHelper.RetrofitCallback() {
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

        showSnackBar(getString(R.string.something_went_wrong));
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("status")) {
                Toast.makeText(this, "Your Home Delivery Charges have been saved successfully", Toast.LENGTH_SHORT).show();
                if(switchState)
                  isFirstTimeLaunched = true;
                else
                    isFirstTimeLaunched = false;
                //showSnackBar(getString(R.string.success));
              //  onResume();
              //  finish();
            } else
                handleError();
        } catch (JSONException e) {
            handleError();
        }
    }

}
