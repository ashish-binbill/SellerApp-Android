package com.binbill.seller.DeliveryAgent;

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
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.AssistedService.AddAssistedServiceActivity;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Order.DeliveryModel;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

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
    RelativeLayout profile_pic;

    @ViewById
    ProgressBar btn_save_progress;
    private String mType = "CREATE";
    private Uri fileUri;

    @ViewById
    ImageView iv_user_image, iv_sub_image;
    private JSONObject fileDetailsJsonProfile;
    private String assistedServiceId;

    @AfterViews
    public void setUpView() {
        setUpToolbar();
        setUpListeners();

        if (getIntent() != null && getIntent().hasExtra(Constants.EDIT_DELIVERY_BOY)) {
            DeliveryModel userModel = (DeliveryModel) getIntent().getSerializableExtra(Constants.EDIT_DELIVERY_BOY);
            assistedServiceId = userModel.getDeliveryBoyId();
            setUpData(userModel);
        }

        enableDisableSaveButton();
    }

    private void setUpData(DeliveryModel userModel) {
        mType = Constants.EDIT_DELIVERY_BOY;

        et_name.setText(userModel.getName());
        et_mobile.setText(userModel.getMobile());

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
            picasso.load(Constants.BASE_URL + "assisted/" + userModel.getDeliveryBoyId() + "/profile")
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

                            int margins = Utility.convertDPtoPx(AddNewDeliveryAgentActivity.this, 15);
                            params.setMargins(margins, margins, margins, margins);
                            iv_user_image.setLayoutParams(params);

                            iv_user_image.setImageDrawable(ContextCompat.getDrawable(AddNewDeliveryAgentActivity.this, R.drawable.ic_user));
                        }
                    });
        }
    }

    private void setUpListeners() {
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });

        et_name.addTextChangedListener(textWatcher);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidDetails(true)) {
                    btn_save.setVisibility(View.GONE);
                    btn_save_progress.setVisibility(View.VISIBLE);

                    makeCreateAssistedServiceApiCall();

                }
            }
        });

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

                enableDisableSaveButton();
            }
        });
    }

    private void makeCreateAssistedServiceApiCall() {

        String mobile = et_mobile.getText().toString();
        String name = et_name.getText().toString();

        if (mType.equalsIgnoreCase(Constants.EDIT_DELIVERY_BOY)) {
            /**
             * Make profileImageDetails null in case of edit
             */
            fileDetailsJsonProfile = null;
            new RetrofitHelper(this).createAssistedServiceDeliveryBoy(name, mobile, assistedServiceId, fileDetailsJsonProfile == null ? "" : fileDetailsJsonProfile.toString(),
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
                            btn_save.setVisibility(View.VISIBLE);
                            btn_save_progress.setVisibility(View.GONE);

                        }

                        @Override
                        public void onErrorResponse() {
                            showSnackBar(getString(R.string.something_went_wrong));
                            btn_save.setVisibility(View.VISIBLE);
                            btn_save_progress.setVisibility(View.GONE);
                        }
                    });
        } else {
            new RetrofitHelper(this).createAssistedServiceDeliveryBoy(name, mobile, Constants.UPLOAD_TYPE_ASSISTED_DELIVERY_BOY, fileDetailsJsonProfile == null ? "" : fileDetailsJsonProfile.toString(), new RetrofitHelper.RetrofitCallback() {
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
                    btn_save.setVisibility(View.VISIBLE);
                    btn_save_progress.setVisibility(View.GONE);
                }

                @Override
                public void onErrorResponse() {
                    showSnackBar(getString(R.string.something_went_wrong));
                    btn_save.setVisibility(View.VISIBLE);
                    btn_save_progress.setVisibility(View.GONE);
                }
            });
        }
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


        TextView titleText = (TextView) dialogView.findViewById(R.id.title);
        titleText.setText(getString(R.string.delivery_agent_added));
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

        if (!mType.equalsIgnoreCase(Constants.EDIT_DELIVERY_BOY)) {

            if (fileDetailsJsonProfile == null) {
                if (showErrorOnUI) {
                    showSnackBar(getString(R.string.upload_profile_image));
                }
                return false;
            }
        }
        return true;
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
            enableDisableSaveButton();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_CAMERA: {

                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    fileUri = Utility.proceedToTakePicture(this);

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

                Log.d("SHRUTI", "Image uri: " + fileUri.toString());
                uploadProfileImage();
            } else {
                fileUri = null;
            }
        }
    }

    private void uploadProfileImage() {

        if (mType == Constants.EDIT_DELIVERY_BOY) {
            iv_sub_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.wait));
            new RetrofitHelper(this).updateAssistedProfileImage(this, assistedServiceId, fileUri, new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {

                    showSnackBar("Upload Success");

                    String uriString = Utility.getPath(AddNewDeliveryAgentActivity.this, fileUri);

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
                    iv_sub_image.setImageDrawable(ContextCompat.getDrawable(AddNewDeliveryAgentActivity.this, R.drawable.ic_camera));
                }
            });
        } else {
            iv_sub_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.wait));
            new RetrofitHelper(this).uploadFile(this, AppSession.getInstance(this).getSellerId(), Constants.UPLOAD_TYPE_ASSISTED_SERVICE, fileUri, new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {

                    showSnackBar("Upload Success");

                    String uriString = Utility.getPath(AddNewDeliveryAgentActivity.this, fileUri);
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
                                    Log.d("SHRUTI", "lOCHAAA");
                                }
                            });

                    processImageResponse(response);
                    iv_sub_image.setImageDrawable(ContextCompat.getDrawable(AddNewDeliveryAgentActivity.this, R.drawable.ic_camera));

                }

                @Override
                public void onErrorResponse() {
                    showSnackBar("Upload Fail");
                    iv_sub_image.setImageDrawable(ContextCompat.getDrawable(AddNewDeliveryAgentActivity.this, R.drawable.ic_camera));
                }
            });
        }
    }

    private void processImageResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            fileDetailsJsonProfile = jsonObject.optJSONObject("file_details");

        } catch (JSONException e) {

        }

    }

    public boolean isValidMobileNumber(String mobileStr) {
        Long mobileNumber = Utility.isValidMobileNumber(mobileStr);
        return mobileNumber.compareTo(-1L) != 0;
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
            fileUri = Utility.proceedToTakePicture(this);
        }
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
