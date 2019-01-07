package com.binbill.seller.Registration;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binbill.seller.AppSession;
import com.binbill.seller.AssistedService.AddAssistedServiceActivity;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends BaseActivity implements OptionListFragment.OnOptionListInteractionListener, SellerTypeFragment.SellerTypeInterface {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    NestedScrollView scroll_view;

    @ViewById
    EditText et_email, et_pan, et_gstin, et_main_category ,et_sku;

    /*@ViewById
    TextView tv_error_email, tv_error_pan, tv_error_gstin, btn_login_now, ;*/

    @ViewById
    TextView btn_login_now, tv_upload;

    @ViewById
    AppButton btn_register_now;

    @ViewById
    LinearLayout btn_register_progress;

    @ViewById
    FrameLayout container;

    UserRegistrationDetails userRegistrationDetails;

    public static String skuDetails;
    public static String mainCategoryDetails;
    public static int idSize;
    public static int[] mainCategoryIds;
    private static final int PICK_FROM_GALLERY = 1;
    private ProgressDialog dialog;

    @AfterViews
    public void initiateViews() {
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        setUpToolbar();
        dialog = new ProgressDialog(this);
        // setUpListeners();

        et_sku.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
                Intent i = new Intent(RegisterActivity.this, MainCategoryActivity_.class);
                startActivity(i);
            }
        });

        tv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.hideKeyboard(RegisterActivity.this, btn_register_now);
                browseDocuments();
            }
        });

        et_sku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.hideKeyboard(RegisterActivity.this, btn_register_now);
                Intent i = new Intent(RegisterActivity.this, InventoriesRegister_.class);
                startActivity(i);
            }
        });

        et_main_category.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        et_main_category.setMaxLines(3);

        enableDisableRegisterButton();

        showSellerTypeFragment(0);
    }

    private void browseDocuments(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
        }else {

            String[] mimeTypes =
                    {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                if (mimeTypes.length > 0) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
            } else {
                String mimeTypesStr = "";
                for (String mimeType : mimeTypes) {
                    mimeTypesStr += mimeType + "|";
                }
                intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
            }
            startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                // Get the Uri of the selected file
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }
                if(displayName!=null) {
                    String extension = displayName.substring(displayName.lastIndexOf("."));
                    if(extension.contains("doc") || extension.contains("docx")|| extension.contains(".xls")
                            || extension.contains("xlsx")){
                        uploadSkuDoc(displayName, myFile, uri, path);
                    }else{
                        Toast.makeText(this, "Please choose .doc or " +
                                ".xls file only", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Unable to get the file. Please copy file to" +
                            " internal Location", Toast.LENGTH_LONG).show();

                }

            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Unable to get the file. Please copy file to" +
                    " another Location", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadSkuDoc(String fileName, File file, Uri uri, String Path){
        dialog.setMessage("Uploading file please wait....");
        dialog.show();
        new RetrofitHelper(this).uploadSkuDocuments(this, file , uri, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
               // btn_register_progress.setVisibility(View.VISIBLE);
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                showSnackBar("Upload Success");
                //processImageResponse(response);
            }

            @Override
            public void onErrorResponse() {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                showSnackBar("Upload Fail");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(skuDetails!=null && !skuDetails.equalsIgnoreCase(""))
            et_sku.setText(skuDetails);
        if(mainCategoryDetails!=null && !mainCategoryDetails.equalsIgnoreCase(""))
            et_main_category.setText(mainCategoryDetails);
        //mainCategoryIds = new int[idSize];
        if(mainCategoryIds!=null) {
            Log.v("TAGINT", "" + mainCategoryIds.length);
        }



    }
/*private void setUpListeners() {

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
             Intent i = new Intent(RegisterActivity.this, MainCategoryActivity.class);
             startActivity(i);
            }
        });
    }*/

    @Click(R.id.btn_register_now)
    public void onRegisterClicked(View viewRegister) {
        Utility.hideKeyboard(this, btn_register_now);
        String skuDetails = et_sku.getText().toString();
        String panStr = et_pan.getText().toString();
        if (!Utility.isEmpty(skuDetails) && !Utility.isEmpty(panStr)) {
            btn_register_now.setVisibility(View.GONE);
            btn_register_progress.setVisibility(View.VISIBLE);
            makeRegisterCall();
        }else{
            Toast.makeText(this, "Please fill all mandatory fields", Toast.LENGTH_SHORT).show();
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

      //  city_id: [joi.number(), joi.allow(null)],
      //  state_id: [joi.number(), joi.allow(null)],
     //   locality_id: [joi.number(), joi.allow(null)],

        if (!Utility.isEmpty(userRegistrationDetails.getEmail()))
            map.put("email", userRegistrationDetails.getEmail());
        if (!Utility.isEmpty(userRegistrationDetails.getGstin()))
            map.put("gstin", userRegistrationDetails.getGstin());
        if (!Utility.isEmpty(userRegistrationDetails.getPan()))
            map.put("pan", userRegistrationDetails.getPan());
        if (!Utility.isEmpty(userRegistrationDetails.getShopName()))
            map.put("seller_name", userRegistrationDetails.getShopName());
        if (!Utility.isEmpty(userRegistrationDetails.getShop_address()))
            map.put("address", userRegistrationDetails.getShop_address());
        if (!Utility.isEmpty(userRegistrationDetails.getLongitude()))
            map.put("longitude", userRegistrationDetails.getLongitude());
        if (!Utility.isEmpty(userRegistrationDetails.getLongitude()))
            map.put("longitude", userRegistrationDetails.getLongitude());
        if (!Utility.isEmpty(userRegistrationDetails.getLatitude()))
            map.put("latitude", userRegistrationDetails.getLatitude());
        if (!Utility.isEmpty(userRegistrationDetails.getMainCategoryIds()))
            map.put("main_category_ids", String.valueOf(userRegistrationDetails.getMainCategoryIds()));
        if (!Utility.isEmpty(userRegistrationDetails.getStoreSize()))
            map.put("store_size", userRegistrationDetails.getStoreSize());
            map.put("staff_no", String.valueOf(userRegistrationDetails.getStaff_no()));
//        if (userRegistrationDetails.getMainCategory() != null && !Utility.isEmpty(userRegistrationDetails.getMainCategory().getId()))
        /**
         * Hardcoding catgeory id, since we are supporting only FMCG For now
         */
        map.put("category_id", "28");
        map.put("is_fmcg", String.valueOf(userRegistrationDetails.isFmcg()));
        map.put("is_assisted", String.valueOf(userRegistrationDetails.isAssisted()));
        map.put("has_pos", String.valueOf(userRegistrationDetails.isHasPos()));


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
                MainCategoryActivity.itemList.clear();
                MainCategoryActivity.isLaunchedFirstTime = false;
                JSONObject sellerDetails = jsonObject.optJSONObject("seller_detail");
                if (sellerDetails != null) {
                    String sellerId = sellerDetails.optString("id");
                    userRegistrationDetails.setId(sellerId);
                    AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);

                    SharedPref.putString(this, SharedPref.SELLER_ID, sellerId);

                    int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
                    Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
                    startActivity(intent);
                    finish();
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

        ArrayList<Integer> tempIds = new ArrayList<>();

        try {
            for (int k = 0; k < mainCategoryIds.length; k++) {
                tempIds.add(mainCategoryIds[k]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        userRegistrationDetails.setEmail(et_email.getText().toString().trim());
        userRegistrationDetails.setStoreSize(et_pan.getText().toString().trim());
        userRegistrationDetails.setStaff_no(Integer.parseInt(et_gstin.getText().toString().trim()));
        userRegistrationDetails.setSku(et_sku.getText().toString().trim());
        if(tempIds.size()!=0) {
            userRegistrationDetails.setMainCategoryIds(tempIds);
        }

        // userRegistrationDetails.setGstin(et_gstin.getText().toString().trim());
        // userRegistrationDetails.setPan(et_pan.getText().toString().trim());

        AppSession.getInstance(this).setUserRegistrationDetails(userRegistrationDetails);
    }

    private boolean isValidDetails() {

        /**
         * EMAIL
         *//*
        String emailStr = et_email.getText().toString();
        if (!Utility.isEmpty(emailStr) && !Utility.isValidEmail(emailStr.trim())) {
            *//*tv_error_email.setText(getString(R.string.incorrect_email_address));
            tv_error_email.setVisibility(View.VISIBLE);*//*
            scroll_view.scrollTo(0, et_email.getBottom());
            return false;
        }*/

        String skuDetails = et_sku.getText().toString();
        if(!Utility.isEmpty(skuDetails)){
            return  false;
        }

        /**
         * PAN
         */
        String panStr = et_pan.getText().toString();
        if (!Utility.isEmpty(panStr)) {
           /* Matcher matcher = Constants.PAN_PATTERN.matcher(panStr.trim());
            if (!matcher.matches()) {
                // tv_error_pan.setText(getString(R.string.incorrect_pan_number));
                //  tv_error_pan.setVisibility(View.VISIBLE);
                scroll_view.scrollTo(0, et_pan.getBottom());

            }*/
            return false;
        }

        /**
         * GSTIN
         */
       /* String gstin = et_gstin.getText().toString();
        if (!Utility.isEmpty(gstin)) {

            try {
                if (!Utility.validGSTIN(gstin)) {
                    // tv_error_gstin.setText(getString(R.string.incorrect_gstin_number));
                    //   tv_error_gstin.setVisibility(View.VISIBLE);
                    scroll_view.scrollTo(0, et_gstin.getBottom());
                    return false;
                }
            } catch (Exception e) {

            }
        }*/


        return true;
    }

    public boolean isValidMobileNumber(String mobileStr) {
        Long mobileNumber = Utility.isValidMobileNumber(mobileStr);
        return mobileNumber.compareTo(-1L) != 0;
    }

    public void enableDisableRegisterButton() {

        String mainCategory = et_main_category.getText().toString();
        String skuDetails = et_sku.getText().toString();

//        String mainCategory = et_main_category.getText().toString();

        if ((!Utility.isEmpty(mainCategory) || !Utility.isEmpty(skuDetails)))
            Utility.enableButton(this, btn_register_now, true);
        else
            Utility.enableButton(this, btn_register_now, false);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.register));
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();

            if (scroll_view.getVisibility() == View.VISIBLE)
                super.onBackPressed();
        } else
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

    @Override
    public void onNext(int stage, boolean isFMCG) {
        if (stage == 0 && isFMCG)
            showSellerTypeFragment(++stage);
        else {
            container.setVisibility(View.GONE);
            scroll_view.setVisibility(View.VISIBLE);

            toolbarText.setText(getString(R.string.register));
        }
    }

    private void showSellerTypeFragment(int flow) {
        if (flow == 0)
            toolbarText.setText(getString(R.string.select_service_type));
        else
            toolbarText.setText(getString(R.string.select_service_type));


        SellerTypeFragment sellerTypeFragment = SellerTypeFragment.newInstance(flow);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, sellerTypeFragment, "SellerTypeFragment");
        transaction.addToBackStack("SellerTypeFragment");
        transaction.commitAllowingStateLoss();
        container.setVisibility(View.VISIBLE);
        scroll_view.setVisibility(View.GONE);
    }
}
