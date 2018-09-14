package com.binbill.seller.Registration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends BaseActivity  implements OptionListFragment.OnOptionListInteractionListener{

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    NestedScrollView scroll_view;

    @ViewById
    EditText et_email, et_pan, et_gstin;

    @ViewById
    TextView tv_error_email, tv_error_pan, tv_error_gstin, btn_login_now, et_main_category;

    @ViewById
    AppButton btn_register_now;

    @ViewById
    LinearLayout btn_register_progress;

    @ViewById
    FrameLayout container;

    UserRegistrationDetails userRegistrationDetails;

    @AfterViews
    public void initiateViews() {
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        setUpToolbar();
        setUpListeners();

        et_main_category.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        et_main_category.setMaxLines(3);

        enableDisableRegisterButton();
    }

    private void setUpListeners() {

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_error_email.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableRegisterButton();
            }
        });

        et_pan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_error_pan.setVisibility(View.GONE);
                tv_error_gstin.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableRegisterButton();
            }
        });

        et_gstin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_error_gstin.setVisibility(View.GONE);
                tv_error_pan.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableDisableRegisterButton();
            }
        });

        et_main_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.hideKeyboard(RegisterActivity.this, btn_register_now);

                ArrayList<MainCategory> list = AppSession.getInstance(RegisterActivity.this).getMainCategoryList();
                if (list != null && list.size() > 0) {
                    OptionListFragment optionListFragment = OptionListFragment.newInstance(list, Constants.MAIN_CATEGORY);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, optionListFragment, "OptionListFragment");
                    transaction.commitAllowingStateLoss();
                    container.setVisibility(View.VISIBLE);
                    scroll_view.setVisibility(View.GONE);
                }
            }
        });


    }

    @Click(R.id.btn_register_now)
    public void onRegisterClicked(View viewRegister) {
        Utility.hideKeyboard(this, btn_register_now);
        if (isValidDetails()) {
            btn_register_now.setVisibility(View.GONE);
            btn_register_progress.setVisibility(View.VISIBLE);

            makeRegisterCall();
        }
    }

    @Click(R.id.btn_login_now)
    public void onLoginClicked(View viewRegister) {
        Utility.hideKeyboard(this, btn_login_now);
        finish();
    }

    private void makeRegisterCall() {

        saveUserProfileLocally();

        HashMap<String, String> map = new HashMap<>();

        if (!Utility.isEmpty(userRegistrationDetails.getEmail()))
            map.put("email", userRegistrationDetails.getEmail());
        if (!Utility.isEmpty(userRegistrationDetails.getGstin()))
            map.put("gstin", userRegistrationDetails.getGstin());
        if (!Utility.isEmpty(userRegistrationDetails.getPan()))
            map.put("pan", userRegistrationDetails.getPan());
        if (userRegistrationDetails.getMainCategory() != null && !Utility.isEmpty(userRegistrationDetails.getMainCategory().getId()))
            map.put("category_id", userRegistrationDetails.getMainCategory().getId());

        new RetrofitHelper(this).updatePanGstinInfo(map, new RetrofitHelper.RetrofitCallback() {
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
        btn_register_now.setVisibility(View.VISIBLE);
        btn_register_progress.setVisibility(View.GONE);

        showSnackBar(getString(R.string.something_went_wrong));
    }

    private void handleResponse(String value) {

        try {
            JSONObject jsonObject = new JSONObject(value);
            if (jsonObject.getBoolean("status")) {

                JSONObject sellerDetails = jsonObject.optJSONObject("seller_detail");
                if (sellerDetails != null) {
                    String sellerId = sellerDetails.optString("id");
                    userRegistrationDetails.setId(sellerId);
                    AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);

                    SharedPref.putString(this, SharedPref.SELLER_ID, sellerId);

                    int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
                    Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
                    startActivity(intent);
                } else {
                    /**
                     * Check if seller already exists
                     */
                    JSONArray sellerArray = jsonObject.optJSONArray("existing_sellers");
                    if (sellerArray != null) {
                        ArrayList<Seller> sellerList = new ArrayList<>();
                        for (int i = 0; i < sellerArray.length(); i++) {
                            JSONObject object = sellerArray.getJSONObject(i);
                            Seller seller = new Seller();
                            seller.setId(object.optString("id"));
                            seller.setName(object.optString("seller_name"));
                            seller.setAddress(object.optString("address"));

                            sellerList.add(seller);
                        }

                        Intent duplicateIntent = new Intent(RegisterActivity.this, DuplicateSellerActivity_.class);
                        duplicateIntent.putExtra(Constants.SELLER_LIST, sellerList);

                        if (!Utility.isEmpty(userRegistrationDetails.getEmail()))
                            duplicateIntent.putExtra("email", userRegistrationDetails.getEmail());
                        if (!Utility.isEmpty(userRegistrationDetails.getGstin()))
                            duplicateIntent.putExtra("gstin", userRegistrationDetails.getGstin());
                        if (!Utility.isEmpty(userRegistrationDetails.getPan()))
                            duplicateIntent.putExtra("pan", userRegistrationDetails.getPan());
                        if (userRegistrationDetails.getMainCategory() != null && !Utility.isEmpty(userRegistrationDetails.getMainCategory().getId()))
                            duplicateIntent.putExtra("category_id", userRegistrationDetails.getMainCategory().getId());

                        if (!Utility.isEmpty(et_gstin.getText().toString()))
                            duplicateIntent.putExtra(Constants.DUPLICATE_ITEM, Constants.GSTIN);
                        else if (!Utility.isEmpty(et_pan.getText().toString()))
                            duplicateIntent.putExtra(Constants.DUPLICATE_ITEM, Constants.PAN);
                        else
                            duplicateIntent.putExtra(Constants.DUPLICATE_ITEM, Constants.NONE);

                        startActivity(duplicateIntent);

                    } else
                        handleError();
                }

                btn_register_now.setVisibility(View.VISIBLE);
                btn_register_progress.setVisibility(View.GONE);
            } else {
                handleError();
            }
        } catch (JSONException e) {
            handleError();
        }
    }


    private void saveUserProfileLocally() {

        userRegistrationDetails.setEmail(et_email.getText().toString().trim());
        userRegistrationDetails.setGstin(et_gstin.getText().toString().trim());
        userRegistrationDetails.setPan(et_pan.getText().toString().trim());

        AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);
    }

    private boolean isValidDetails() {

        /**
         * EMAIL
         */
        String emailStr = et_email.getText().toString();
        if (!Utility.isEmpty(emailStr) && !Utility.isValidEmail(emailStr.trim())) {
            tv_error_email.setText(getString(R.string.incorrect_email_address));
            tv_error_email.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, et_email.getBottom());
            return false;
        }

        /**
         * PAN
         */
        String panStr = et_pan.getText().toString();
        if (!Utility.isEmpty(panStr)) {
            Matcher matcher = Constants.PAN_PATTERN.matcher(panStr.trim());
            if (!matcher.matches()) {
                tv_error_pan.setText(getString(R.string.incorrect_pan_number));
                tv_error_pan.setVisibility(View.VISIBLE);
                scroll_view.scrollTo(0, et_pan.getBottom());
                return false;
            }
        }

        /**
         * GSTIN
         */
        String gstin = et_gstin.getText().toString();
        if (!Utility.isEmpty(gstin)) {

            try {
                if (!Utility.validGSTIN(gstin)) {
                    tv_error_gstin.setText(getString(R.string.incorrect_gstin_number));
                    tv_error_gstin.setVisibility(View.VISIBLE);
                    scroll_view.scrollTo(0, et_gstin.getBottom());
                    return false;
                }
            } catch (Exception e) {

            }
        }


        return true;
    }

    public boolean isValidMobileNumber(String mobileStr) {
        Long mobileNumber = Utility.isValidMobileNumber(mobileStr);
        return mobileNumber.compareTo(-1L) != 0;
    }

    public void enableDisableRegisterButton() {

        String pan = et_pan.getText().toString();
        String gstin = et_gstin.getText().toString();

        String mainCategory = et_main_category.getText().toString();

        if ((!Utility.isEmpty(pan) || !Utility.isEmpty(gstin)) &&  !Utility.isEmpty(mainCategory.trim()))
            Utility.enableButton(this, btn_register_now, true);
        else
            Utility.enableButton(this, btn_register_now, false);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.register));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onListItemSelected(Object item, int identifier) {
        switch (identifier) {
            case Constants.MAIN_CATEGORY:
                MainCategory selectedItem = (MainCategory) item;
                et_main_category.setText(selectedItem.getName());
                userRegistrationDetails.setMainCategory(selectedItem);

                container.setVisibility(View.GONE);
                scroll_view.setVisibility(View.VISIBLE);
                scroll_view.smoothScrollTo(0, et_main_category.getBottom());

                break;
        }
    }

    @Override
    public void onCancel() {
        container.setVisibility(View.GONE);
        scroll_view.setVisibility(View.VISIBLE);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("OptionListFragment");
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        enableDisableRegisterButton();
    }
}
