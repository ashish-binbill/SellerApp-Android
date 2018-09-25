package com.binbill.seller.Registration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Dashboard.ProfileModel;
import com.binbill.seller.Model.StateCityModel;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

@EActivity(R.layout.activity_basic_details1)
public class BasicDetails1Activity extends BaseActivity implements OptionListFragment.OnOptionListInteractionListener {


    @ViewById
    NestedScrollView scroll_view;

    @ViewById
    FrameLayout container;

    @ViewById
    EditText et_shop_name, et_business_name, et_business_address, et_pincode, et_city, et_locality, et_upload_shop_image, et_state;

    @ViewById
    TextView tv_error_shop_name, view_image, image_count, tv_error_business_name, tv_error_business_address, tv_error_pincode, tv_error_city, tv_error_locality, tv_error_state;

    @ViewById
    AppButton btn_submit;

    @ViewById
    LinearLayout btn_submit_progress;

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;
    UserRegistrationDetails userRegistrationDetails;
    private StateCityModel mStateSelected;
    private StateCityModel.CityModel mCitySelected;
    private StateCityModel.LocalityModel mLocalitySelected;
    private ArrayList<StateCityModel.CityModel> citiesList;
    private ArrayList<StateCityModel.LocalityModel> localityList;
    private ArrayList<Uri> cameraFileUri = new ArrayList<>();

    boolean isEditMode = false;
    ProfileModel mProfileModel;

    @AfterViews
    public void initiateViews() {

        if (getIntent().hasExtra(Constants.PROFILE_MODEL) && getIntent().getSerializableExtra(Constants.PROFILE_MODEL) != null) {
            isEditMode = true;
            mProfileModel = (ProfileModel) getIntent().getSerializableExtra(Constants.PROFILE_MODEL);
            setUpData();
        }

        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        setUpToolbar();
        setUpListeners();

        et_shop_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        et_business_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        et_business_address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        et_business_address.setMaxLines(3);

        enableDisableVerifyButton();
        Utility.hideKeyboard(this, btn_submit);
    }

    private void setUpData() {
        et_shop_name.setText(mProfileModel.getName());
        et_business_name.setText(mProfileModel.getSellerDetails().getBasicDetails().getBusinessName());
    }

    private void enableDisableVerifyButton() {

        String shopName = et_shop_name.getText().toString();
        String businessName = et_business_name.getText().toString();
        String businessAddress = et_business_address.getText().toString();

        String city = et_city.getText().toString();
        String pincode = et_pincode.getText().toString();
        String state = et_state.getText().toString();
        String locality = et_locality.getText().toString();

        if (!Utility.isEmpty(shopName.trim()) && !Utility.isEmpty(businessName.trim()) && !Utility.isEmpty(businessAddress.trim()) &&
                !Utility.isEmpty(city.trim()) && !Utility.isEmpty(locality) && !Utility.isEmpty(state.trim()) && !Utility.isEmpty(pincode.trim()))
            Utility.enableButton(this, btn_submit, true);
        else
            Utility.enableButton(this, btn_submit, false);
    }

    private void makeUploadCallInEditMode() {

        saveUserProfileInLocalObject();

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

                if (isEditMode) {
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

    private void setUpListeners() {

        et_upload_shop_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });


        et_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(BasicDetails1Activity.this, btn_submit);

                ArrayList<StateCityModel> list = AppSession.getInstance(BasicDetails1Activity.this).getStateList();
                if (list != null && list.size() > 0) {
                    OptionListFragment optionListFragment = OptionListFragment.newInstance(list, Constants.STATES);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, optionListFragment, "OptionListFragment");
                    transaction.commitAllowingStateLoss();
                    container.setVisibility(View.VISIBLE);
                    scroll_view.setVisibility(View.GONE);
                }
            }
        });

        et_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(BasicDetails1Activity.this, btn_submit);
                tv_error_state.setVisibility(View.GONE);
                if (et_state.getText().toString().isEmpty()) {
                    tv_error_state.setText(getString(R.string.error_field_cannot_be_empty));
                    tv_error_state.setVisibility(View.VISIBLE);
                    scroll_view.scrollTo(0, et_state.getBottom());
                } else if (mStateSelected != null) {

                    if (citiesList != null && citiesList.size() > 0) {
                        OptionListFragment optionListFragment = OptionListFragment.newInstance(citiesList, Constants.CITIES);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.container, optionListFragment, "OptionListFragment");
                        transaction.commitAllowingStateLoss();
                        container.setVisibility(View.VISIBLE);
                        scroll_view.setVisibility(View.GONE);
                    }
                }
            }
        });

        et_locality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(BasicDetails1Activity.this, btn_submit);
                tv_error_city.setVisibility(View.GONE);

                if (et_city.getText().toString().isEmpty()) {
                    tv_error_city.setText(getString(R.string.error_field_cannot_be_empty));
                    tv_error_city.setVisibility(View.VISIBLE);
                    scroll_view.scrollTo(0, et_city.getBottom());
                } else if (mCitySelected != null) {

                    if (localityList != null && localityList.size() > 0) {
                        OptionListFragment optionListFragment = OptionListFragment.newInstance(localityList, Constants.LOCALITY);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.container, optionListFragment, "OptionListFragment");
                        transaction.commitAllowingStateLoss();
                        container.setVisibility(View.VISIBLE);
                        scroll_view.setVisibility(View.GONE);
                    }
                }
            }
        });

        et_shop_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_error_shop_name.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableVerifyButton();
            }
        });

        et_business_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_error_business_name.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableVerifyButton();
            }
        });

        et_business_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_error_business_address.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableVerifyButton();
            }
        });

        et_pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_error_pincode.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableVerifyButton();
            }
        });

        view_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BasicDetails1Activity.this, ImagePreviewActivity_.class);
                intent.putExtra(Constants.FILE_URI, cameraFileUri);
                intent.putExtra(Constants.IMAGE_TYPE, Constants.TYPE_URI);
                startActivity(intent);
            }
        });
    }

    @Click(R.id.btn_submit)
    public void onSubmitClicked(View viewRegister) {
        Utility.hideKeyboard(this, btn_submit);
        if (isValid()) {
            btn_submit.setVisibility(View.GONE);
            btn_submit_progress.setVisibility(View.VISIBLE);

            if (isEditMode) {
                makeUploadCallInEditMode();
            } else {
                makeUploadDataToServerCall();
            }
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.MANAGE_DOCUMENTS},
                    Constants.PERMISSION_CAMERA);
        } else {
            cameraFileUri.add(Utility.proceedToTakePicture(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTIVITY_RESULT_CAMERA) {
            if (resultCode == RESULT_OK) {
                Log.d("SHRUTI", "Image uri: " + cameraFileUri.get(cameraFileUri.size() - 1).toString());

                et_upload_shop_image.setText("Uploading image...");
                et_upload_shop_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(this, R.drawable.wait), null);

                new RetrofitHelper(this).uploadFile(this, userRegistrationDetails.getId(), Constants.UPLOAD_TYPE_SELLER_SHOP, cameraFileUri.get(cameraFileUri.size() - 1), new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {

                        et_upload_shop_image.setText("");
                        showSnackBar("Upload Success");
                        image_count.setText(getResources().getQuantityString(R.plurals.file_uploaded, cameraFileUri.size(), cameraFileUri.size()));
                        image_count.setVisibility(View.VISIBLE);
                        view_image.setVisibility(View.VISIBLE);
                        et_upload_shop_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                ContextCompat.getDrawable(BasicDetails1Activity.this, R.drawable.ic_camera), null);
                    }

                    @Override
                    public void onErrorResponse() {
                        et_upload_shop_image.setText("");
                        cameraFileUri.remove(cameraFileUri.size() - 1);
                        showSnackBar("Upload Fail");
                        if (cameraFileUri.size() > 0) {
                            view_image.setVisibility(View.VISIBLE);
                            image_count.setText(getResources().getQuantityString(R.plurals.file_uploaded, cameraFileUri.size(), cameraFileUri.size()));
                            image_count.setVisibility(View.VISIBLE);
                        } else {
                            view_image.setVisibility(View.GONE);
                            image_count.setVisibility(View.GONE);
                        }
                        et_upload_shop_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                ContextCompat.getDrawable(BasicDetails1Activity.this, android.R.drawable.ic_menu_close_clear_cancel), null);
                    }
                });
            } else {
                cameraFileUri.remove(cameraFileUri.size() - 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_CAMERA: {

                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    cameraFileUri.add(Utility.proceedToTakePicture(this));

                } else {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showSnackBar("Please give permissions to take picture");
                    }

                }
                return;
            }
        }
    }

    private void makeUploadDataToServerCall() {

        saveUserProfileInLocalObject();
        AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);
        int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);

        Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
        startActivity(intent);

        btn_submit.setVisibility(View.VISIBLE);
        btn_submit_progress.setVisibility(View.GONE);
    }

    private void saveUserProfileInLocalObject() {

        userRegistrationDetails.setShopName(et_shop_name.getText().toString().trim());
        userRegistrationDetails.setBusinessName(et_business_name.getText().toString().trim());
        userRegistrationDetails.setBusinessAddress(et_business_address.getText().toString().trim());
        userRegistrationDetails.setPincode(et_pincode.getText().toString().trim());
        userRegistrationDetails.setCity(mCitySelected);
        userRegistrationDetails.setState(mStateSelected);
        userRegistrationDetails.setLocality(mLocalitySelected);
        /**
         * Main category is set in onItem Selected listener below
         */
    }

    private boolean isValid() {

        /**
         * SHOP NAME
         */
        String shopName = et_shop_name.getText().toString().trim();
        if (TextUtils.isEmpty(shopName)) {
            tv_error_shop_name.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_shop_name.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, et_shop_name.getBottom());
            return false;
        }

        /**
         * BUSINESS NAME
         */
        String businessName = et_business_name.getText().toString().trim();
        if (TextUtils.isEmpty(businessName)) {
            tv_error_business_name.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_business_name.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, et_business_name.getBottom());
            return false;
        }

        /**
         * BUSINESS ADDRESS
         */
        String businessAddress = et_business_address.getText().toString().trim();
        if (TextUtils.isEmpty(businessAddress)) {
            tv_error_business_address.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_business_address.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, et_business_address.getBottom());
            return false;
        }

        /**
         * STATE
         */
        String state = et_state.getText().toString().trim();
        if (TextUtils.isEmpty(state)) {
            tv_error_state.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_state.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, et_state.getBottom());
            return false;
        }

        /**
         * CITY
         */
        String city = et_city.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            tv_error_city.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_city.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, et_city.getBottom());
            return false;
        }

        /**
         * LOCALITY
         */
        String locality = et_locality.getText().toString().trim();
        if (TextUtils.isEmpty(locality)) {
            tv_error_locality.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_locality.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, et_locality.getBottom());
            return false;
        }

        /**
         * PINCODE
         */
        String pincode = et_pincode.getText().toString().trim();
        if (TextUtils.isEmpty(pincode)) {
            tv_error_pincode.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_pincode.setVisibility(View.VISIBLE);
            return false;
        } else {
            Matcher matcher = Constants.ZIP_CODE_PATTERN.matcher(pincode.trim());
            if (!matcher.matches()) {
                tv_error_pincode.setText(R.string.incorrect_pincode);
                tv_error_pincode.setVisibility(View.VISIBLE);
                scroll_view.scrollTo(0, et_pincode.getBottom());
                et_pincode.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.basic_details_1));
    }

    @Override
    public void onListItemSelected(Object item, int identifier) {

        switch (identifier) {
            case Constants.STATES:
                StateCityModel selectedObject = (StateCityModel) item;
                mStateSelected = selectedObject;
                et_state.setText(selectedObject.getStateName());
                et_city.setText("");

                container.setVisibility(View.GONE);
                scroll_view.setVisibility(View.VISIBLE);
                scroll_view.smoothScrollTo(0, et_state.getBottom());

                et_city.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(this, R.drawable.wait), null);
                new RetrofitHelper(this).fetchCityByState(mStateSelected.getStateId(),
                        new RetrofitHelper.RetrofitCallback() {
                            @Override
                            public void onResponse(String response) {
                                et_city.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                        ContextCompat.getDrawable(BasicDetails1Activity.this, R.drawable.ic_dropdown), null);

                                try {
                                    JSONObject payload = new JSONObject(response);
                                    JSONArray cityArray = payload.getJSONArray("cities");
                                    Type classType = new TypeToken<ArrayList<StateCityModel.CityModel>>() {
                                    }.getType();

                                    citiesList = new Gson().fromJson(cityArray.toString(), classType);

                                } catch (JSONException e) {
                                }


                            }

                            @Override
                            public void onErrorResponse() {

                            }
                        });


                break;
            case Constants.DELIVERY_DISTANCE:
                String selectedString = (String) item;
                et_city.setText(selectedString);

                container.setVisibility(View.GONE);
                scroll_view.setVisibility(View.VISIBLE);
                scroll_view.smoothScrollTo(0, et_city.getBottom());
                break;
            case Constants.CITIES:
                StateCityModel.CityModel selectedCity = (StateCityModel.CityModel) item;
                mCitySelected = selectedCity;
                et_city.setText(selectedCity.getCityName());
                et_locality.setText("");

                container.setVisibility(View.GONE);
                scroll_view.setVisibility(View.VISIBLE);
                scroll_view.smoothScrollTo(0, et_city.getBottom());

                et_locality.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(this, R.drawable.wait), null);
                new RetrofitHelper(this).fetchLocalityByCityAndState(mCitySelected.getStateId(), mCitySelected.getCityId(),
                        new RetrofitHelper.RetrofitCallback() {
                            @Override
                            public void onResponse(String response) {
                                et_locality.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                        ContextCompat.getDrawable(BasicDetails1Activity.this, R.drawable.ic_dropdown), null);

                                try {
                                    JSONObject payload = new JSONObject(response);
                                    JSONArray localityArray = payload.getJSONArray("localities");
                                    Type classType = new TypeToken<ArrayList<StateCityModel.LocalityModel>>() {
                                    }.getType();

                                    localityList = new Gson().fromJson(localityArray.toString(), classType);

                                } catch (JSONException e) {
                                }
                            }

                            @Override
                            public void onErrorResponse() {

                            }
                        });


                break;

            case Constants.LOCALITY:
                StateCityModel.LocalityModel selectedLocality = (StateCityModel.LocalityModel) item;
                mLocalitySelected = selectedLocality;
                et_locality.setText(selectedLocality.getLocalityName());
                et_pincode.setText(selectedLocality.getPinCode());

                container.setVisibility(View.GONE);
                scroll_view.setVisibility(View.VISIBLE);
                scroll_view.smoothScrollTo(0, et_city.getBottom());
        }


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
}
