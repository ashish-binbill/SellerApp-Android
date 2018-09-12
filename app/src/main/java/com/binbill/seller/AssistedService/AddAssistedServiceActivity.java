package com.binbill.seller.AssistedService;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.R;
import com.binbill.seller.Registration.ImagePreviewActivity_;
import com.binbill.seller.Registration.OptionListFragment;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.nex3z.flowlayout.FlowLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

@EActivity(R.layout.activity_add_assisted_service)
public class AddAssistedServiceActivity extends BaseActivity implements OptionListFragment.OnOptionListInteractionListener {
    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    EditText et_service_guide, et_mobile, et_adhar_image, et_type_of_service, et_price;

    @ViewById
    AppButton btn_submit;

    @ViewById
    LinearLayout btn_submit_progress, upload_image_layout;

    @ViewById
    FrameLayout container, frame_cross;

    @ViewById
    RelativeLayout profile_pic;

    @ViewById
    NestedScrollView scroll_view;

    @ViewById
    FlowLayout fl_tag_layout;

    @ViewById
    ImageView iv_user_image, iv_sub_image;

    @ViewById
    TextView view_image, image_count, tv_error_mobile, tv_error_upload_adhar;

    private ArrayList<Uri> cameraFileUri = new ArrayList<>();
    private ArrayList<AssistedUserModel.ServiceType> mServiceTypes;
    private String mServiceTypeId;
    private JSONArray fileDetailsJson = new JSONArray();
    private JSONObject fileDetailsJsonProfile;
    private String mAssistedServiceId;
    private String mType = "CREATE_ASSISTED_SERVICE";
    private boolean mProfileImage = false;
    private Uri fileUri;

    @AfterViews
    public void setUpView() {
        setUpToolbar();
        setUpListener();

        if (getIntent() != null && getIntent().hasExtra(Constants.EDIT_ASSISTED_SERVICES)) {
            AssistedUserModel userModel = (AssistedUserModel) getIntent().getSerializableExtra(Constants.EDIT_ASSISTED_SERVICES);
            setUpData(userModel);
        }

        enableDisableVerifyButton();
    }

    private void setUpData(AssistedUserModel userModel) {

        mType = Constants.EDIT_ASSISTED_SERVICES;

        mAssistedServiceId = userModel.getId();
        ArrayList<AssistedUserModel.ServiceType> serviceTypes = userModel.getServiceTypes();
        et_mobile.setText(userModel.getMobile());
        et_service_guide.setText(userModel.getName());

        et_price.setVisibility(View.GONE);
        et_type_of_service.setVisibility(View.GONE);
        upload_image_layout.setVisibility(View.GONE);

        if (userModel.getProfileImage() != null) {

            final String authToken = SharedPref.getString(iv_user_image.getContext(), SharedPref.AUTH_TOKEN);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .authenticator(new Authenticator() {
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            return response.request().newBuilder()
                                    .header("Authorization", authToken)
                                    .build();
                        }
                    }).build();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 0);
            iv_user_image.setLayoutParams(params);

            iv_user_image.setScaleType(ImageView.ScaleType.FIT_XY);

            Picasso picasso = new Picasso.Builder(this)
                    .downloader(new OkHttp3Downloader(okHttpClient))
                    .build();
            picasso.load(Constants.BASE_URL + "assisted/" + userModel.getId() + "/profile")
                    .config(Bitmap.Config.RGB_565)
                    .into(iv_user_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) iv_user_image.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            iv_user_image.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError(Exception e) {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT
                            );

                            int margins = Utility.convertDPtoPx(AddAssistedServiceActivity.this, 15);
                            params.setMargins(margins, margins, margins, margins);
                            iv_user_image.setLayoutParams(params);

                            iv_user_image.setImageDrawable(ContextCompat.getDrawable(AddAssistedServiceActivity.this, R.drawable.ic_user));
                        }
                    });
        }
        createEditServiceLayout(serviceTypes);
    }

    private void createEditServiceLayout(ArrayList<AssistedUserModel.ServiceType> serviceTypes) {
        fl_tag_layout.setVisibility(View.VISIBLE);
        int noOfTags = fl_tag_layout.getChildCount();
        if (noOfTags > 0)
            fl_tag_layout.removeAllViews();

        if (serviceTypes != null) {
            int noOfServices = serviceTypes.size();
            for (AssistedUserModel.ServiceType serviceType : serviceTypes) {
                LayoutInflater inflater = LayoutInflater.from(this);
                FrameLayout inflatedLayout = (FrameLayout) inflater.inflate(R.layout.item_tag_view_edit, null, false);
                TextView textView = (TextView) inflatedLayout.findViewById(R.id.tv_text);
                FrameLayout frameCross = (FrameLayout) inflatedLayout.findViewById(R.id.frame_cross);
                if (serviceType.getPrice() != null)
                    textView.setText(serviceType.getServiceType() + " ( Rs " + serviceType.getPrice().getValue() + " )");
                textView.setTag(serviceType);
                textView.setOnClickListener(mTagClickListener);
                fl_tag_layout.addView(inflatedLayout);
                if (noOfServices == 1) {
                    frameCross.setVisibility(View.GONE);
                }
            }
        }

    }

    private void setUpListener() {

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileImage = true;
                checkCameraPermission();
            }
        });
        et_type_of_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(AddAssistedServiceActivity.this, btn_submit);

                mServiceTypes = AppSession.getInstance(AddAssistedServiceActivity.this).getAssistedServiceTypes();

                if (mServiceTypes != null && mServiceTypes.size() > 0) {
                    ArrayList<String> serviceList = new ArrayList<>();
                    for (AssistedUserModel.ServiceType serviceType : mServiceTypes)
                        serviceList.add(serviceType.getName());

                    OptionListFragment optionListFragment = OptionListFragment.newInstance(serviceList, Constants.DELIVERY_DISTANCE);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, optionListFragment, "OptionListFragment");
                    transaction.commitAllowingStateLoss();
                    container.setVisibility(View.VISIBLE);
                    scroll_view.setVisibility(View.GONE);
                }
            }
        });

        et_adhar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileImage = false;
                tv_error_upload_adhar.setVisibility(View.GONE);
                checkCameraPermission();
            }
        });
        et_price.addTextChangedListener(textWatcher);
        et_service_guide.addTextChangedListener(textWatcher);

        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (tv_error_mobile != null && tv_error_mobile.getVisibility() == View.VISIBLE) {
                        tv_error_mobile.setVisibility(View.GONE);
                    }
                }

                if (isValidDetails(false)) {
                    tv_error_mobile.setVisibility(View.GONE);
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidDetails(true)) {
                    btn_submit.setVisibility(View.GONE);
                    btn_submit_progress.setVisibility(View.VISIBLE);

                    makeCreateAssistedServiceApiCall();

                }
            }
        });

        view_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddAssistedServiceActivity.this, ImagePreviewActivity_.class);
                intent.putExtra(Constants.FILE_URI, cameraFileUri);
                intent.putExtra(Constants.IMAGE_TYPE, Constants.TYPE_URI);
                startActivity(intent);
            }
        });

    }

    public void invokeSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_assisted_service, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        AppButton yesButton = (AppButton) dialogView.findViewById(R.id.btn_yes);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
    }

    private void makeCreateAssistedServiceApiCall() {

        String name = et_service_guide.getText().toString();
        String mobile = et_mobile.getText().toString();
        String price = et_price.getText().toString();

        if (mType.equalsIgnoreCase(Constants.EDIT_ASSISTED_SERVICES)) {
            /**
             * Make profileImageDetails null in case of edit
             */
            fileDetailsJsonProfile = null;
            new RetrofitHelper(this).createAssistedService(name, mobile, mAssistedServiceId, fileDetailsJson == null ? "" : fileDetailsJson.toString(), fileDetailsJsonProfile == null ? "" : fileDetailsJsonProfile.toString(),
                    new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.optBoolean("status")) {
                                    invokeSuccessDialog();
                                } else
                                    showSnackBar(getString(R.string.something_went_wrong));
                            } catch (JSONException e) {
                                showSnackBar(getString(R.string.something_went_wrong));
                            }
                            btn_submit.setVisibility(View.VISIBLE);
                            btn_submit_progress.setVisibility(View.GONE);

                        }

                        @Override
                        public void onErrorResponse() {
                            showSnackBar(getString(R.string.something_went_wrong));
                            btn_submit.setVisibility(View.VISIBLE);
                            btn_submit_progress.setVisibility(View.GONE);
                        }
                    });
        } else {
            new RetrofitHelper(this).createAssistedService(name, mobile, mServiceTypeId, price, fileDetailsJson == null ? "" : fileDetailsJson.toString(), fileDetailsJsonProfile == null ? "" : fileDetailsJsonProfile.toString(), new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optBoolean("status")) {
                            invokeSuccessDialog();
                        } else
                            showSnackBar(getString(R.string.something_went_wrong));
                    } catch (JSONException e) {
                        showSnackBar(getString(R.string.something_went_wrong));
                    }
                    btn_submit.setVisibility(View.VISIBLE);
                    btn_submit_progress.setVisibility(View.GONE);
                }

                @Override
                public void onErrorResponse() {
                    showSnackBar(getString(R.string.something_went_wrong));
                    btn_submit.setVisibility(View.VISIBLE);
                    btn_submit_progress.setVisibility(View.GONE);
                }
            });
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
            if (mProfileImage)
                fileUri = Utility.proceedToTakePicture(this);
            else
                cameraFileUri.add(Utility.proceedToTakePicture(this));
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.assisted_services));
    }

    private boolean isValidDetails(boolean showErrorOnUI) {
        String mobileStr = et_mobile.getText().toString().trim();
        if (TextUtils.isEmpty(mobileStr)) {
            if (showErrorOnUI) {
                tv_error_mobile.setText(getString(R.string.error_field_cannot_be_empty));
                tv_error_mobile.setVisibility(View.VISIBLE);
            }
            return false;
        } else if (!isValidMobileNumber(mobileStr)) {
            if (showErrorOnUI) {
                tv_error_mobile.setText(getString(R.string.error_incorrect_value));
                tv_error_mobile.setVisibility(View.VISIBLE);
            }
            return false;
        }

        if (!mType.equalsIgnoreCase(Constants.EDIT_ASSISTED_SERVICES)) {
            if (fileDetailsJson == null) {
                if (showErrorOnUI) {
                    tv_error_upload_adhar.setText(getString(R.string.error_field_cannot_be_empty));
                    tv_error_upload_adhar.setVisibility(View.VISIBLE);
                }
                return false;
            }

            if (fileDetailsJsonProfile == null) {
                if (showErrorOnUI) {
                    showSnackBar(getString(R.string.upload_profile_image));
                }
                return false;
            }
        }
        return true;
    }

    public boolean isValidMobileNumber(String mobileStr) {
        Long mobileNumber = Utility.isValidMobileNumber(mobileStr);
        return mobileNumber.compareTo(-1L) != 0;
    }

    private void enableDisableVerifyButton() {

        String name = et_service_guide.getText().toString();
        String mobile = et_mobile.getText().toString();

        if (!Utility.isEmpty(name) && !Utility.isEmpty(mobile)) {
            if (mType.equalsIgnoreCase(Constants.EDIT_ASSISTED_SERVICES)) {
                Utility.enableButton(this, btn_submit, true);

                if (tv_error_mobile != null && tv_error_mobile.getVisibility() == View.VISIBLE) {
                    tv_error_mobile.setVisibility(View.GONE);
                }
            } else if (!Utility.isEmpty(et_price.getText().toString()) && !Utility.isEmpty(et_type_of_service.getText().toString())) {
                Utility.enableButton(this, btn_submit, true);

                if (tv_error_mobile != null && tv_error_mobile.getVisibility() == View.VISIBLE) {
                    tv_error_mobile.setVisibility(View.GONE);
                }
            } else {
                Utility.enableButton(this, btn_submit, false);
            }
        } else
            Utility.enableButton(this, btn_submit, false);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            enableDisableVerifyButton();
        }
    };

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTIVITY_RESULT_CAMERA) {
            if (resultCode == RESULT_OK) {

                if (mProfileImage) {
                    Log.d("SHRUTI", "Image uri: " + fileUri.toString());
                    uploadProfileImage();
                } else {
                    Log.d("SHRUTI", "Image uri: " + cameraFileUri.get(cameraFileUri.size() - 1).toString());
                    uploadAdharImage();
                }
            } else {
                if (mProfileImage)
                    fileUri = null;
                else
                    cameraFileUri.remove(cameraFileUri.size() - 1);
            }
        }
    }

    private void uploadProfileImage() {

        if (mType == Constants.EDIT_ASSISTED_SERVICES) {
            iv_sub_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.wait));
            new RetrofitHelper(this).updateAssistedProfileImage(this, mAssistedServiceId, fileUri, new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {

                    showSnackBar("Upload Success");

                    String uriString = Utility.getPath(AddAssistedServiceActivity.this, fileUri);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                    );
                    params.setMargins(0, 0, 0, 0);
                    iv_user_image.setLayoutParams(params);

                    iv_user_image.setScaleType(ImageView.ScaleType.FIT_XY);

                    Picasso.get().
                            load("file://" + uriString)
                            .config(Bitmap.Config.RGB_565)
                            .into(iv_user_image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap imageBitmap = ((BitmapDrawable) iv_user_image.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
//                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                    iv_user_image.setImageDrawable(imageDrawable);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });

//                    processImageResponse(response);
                }

                @Override
                public void onErrorResponse() {
                    showSnackBar("Upload Fail");
                    iv_sub_image.setImageDrawable(ContextCompat.getDrawable(AddAssistedServiceActivity.this, R.drawable.ic_camera));
                }
            });
        } else {
            iv_sub_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.wait));
            new RetrofitHelper(this).uploadFile(this, AppSession.getInstance(this).getSellerId(), Constants.UPLOAD_TYPE_ASSISTED_SERVICE_PROFILE, fileUri, new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {

                    showSnackBar("Upload Success");

                    String uriString = Utility.getPath(AddAssistedServiceActivity.this, fileUri);
                    /**
                     * /assisted/{id}/profile
                     */

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                    );
                    params.setMargins(0, 0, 0, 0);
                    iv_user_image.setLayoutParams(params);

                    iv_user_image.setScaleType(ImageView.ScaleType.FIT_XY);

                    Picasso.get().
                            load("file://" + uriString)
                            .config(Bitmap.Config.RGB_565)
                            .into(iv_user_image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap imageBitmap = ((BitmapDrawable) iv_user_image.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
//                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                    iv_user_image.setImageDrawable(imageDrawable);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });

                    processImageResponse(response);
                }

                @Override
                public void onErrorResponse() {
                    showSnackBar("Upload Fail");
                    iv_sub_image.setImageDrawable(ContextCompat.getDrawable(AddAssistedServiceActivity.this, R.drawable.ic_camera));
                }
            });
        }
    }

    private void uploadAdharImage() {
        et_adhar_image.setText("Uploading image...");
        et_adhar_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(this, R.drawable.wait), null);
        new RetrofitHelper(this).uploadFile(this, AppSession.getInstance(this).getSellerId(), Constants.UPLOAD_TYPE_ASSISTED_SERVICE, cameraFileUri.get(cameraFileUri.size() - 1), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                et_adhar_image.setText("");
                showSnackBar("Upload Success");
                image_count.setText(getResources().getQuantityString(R.plurals.file_uploaded, cameraFileUri.size(), cameraFileUri.size()));
                image_count.setVisibility(View.VISIBLE);
                view_image.setVisibility(View.VISIBLE);
                et_adhar_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(AddAssistedServiceActivity.this, R.drawable.ic_camera), null);

                processImageResponse(response);
            }

            @Override
            public void onErrorResponse() {
                et_adhar_image.setText("");
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
                et_adhar_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(AddAssistedServiceActivity.this, android.R.drawable.ic_menu_close_clear_cancel), null);
            }
        });

    }

    private void processImageResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (mProfileImage) {
                fileDetailsJsonProfile = jsonObject.optJSONObject("file_details");
            } else {
                JSONObject fileJson = jsonObject.optJSONObject("file_details");
                fileDetailsJson.put(fileJson);
            }

        } catch (JSONException e) {

        }

    }

    @Override
    public void onListItemSelected(Object item, int identifier) {
        String selectedItem = (String) item;
        et_type_of_service.setText(selectedItem);

        for (AssistedUserModel.ServiceType type : mServiceTypes)
            if (type.getName().equalsIgnoreCase(selectedItem))
                mServiceTypeId = type.getId();

        container.setVisibility(View.GONE);
        scroll_view.setVisibility(View.VISIBLE);
        scroll_view.smoothScrollTo(0, et_type_of_service.getBottom());

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

    View.OnClickListener mTagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final AssistedUserModel userModel = (AssistedUserModel) getIntent().getSerializableExtra(Constants.EDIT_ASSISTED_SERVICES);
            final AssistedUserModel.ServiceType serviceType = (AssistedUserModel.ServiceType) view.getTag();
            final String serviceTypeId = serviceType.getId();
            String userId = userModel.getId();
            new RetrofitHelper(AddAssistedServiceActivity.this).deleteAssistedServiceTag(userId, serviceTypeId, new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {
                    /*fetchAssistedService();*/
                    ArrayList<AssistedUserModel.ServiceType> serviceTypes = userModel.getServiceTypes();
                    serviceTypes.remove(serviceType);
                    createEditServiceLayout(serviceTypes);
                }

                @Override
                public void onErrorResponse() {
                    AddAssistedServiceActivity.this.showSnackBar(getString(R.string.something_went_wrong));
                }
            });

        }
    };
}
