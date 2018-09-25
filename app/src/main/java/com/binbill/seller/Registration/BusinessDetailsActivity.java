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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.BusinessDetailsModel;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Model.FMCGHeaderModel;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SplashActivity;
import com.binbill.seller.UpgradeHelper;
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

@EActivity(R.layout.activity_business_details)
public class BusinessDetailsActivity extends BaseActivity implements OptionListFragment.OnOptionListInteractionListener {


    private static final int SUCCESS = 1;
    private static final int FAILURE = 2;
    private static final int PENDING = 3;
    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    AppButton btn_submit;

    @ViewById
    EditText et_business_type;

    @ViewById
    LinearLayout btn_submit_progress, sole_proprietorship, partnership, public_private_company;

    @ViewById
    FrameLayout container;

    @ViewById
    TextView tv_sole_1, tv_sole_2, tv_partner_1, tv_partner_2, tv_partner_3, tv_partner_4, tv_pp_1, tv_pp_2, tv_pp_3;

    @ViewById
    TextView view_partner_image_1, view_partner_image_2, view_partner_image_3, view_partner_image_4, view_sole_image_1,
            view_sole_image_2, view_pp_image_1, view_pp_image_2, view_pp_image_3;

    @ViewById
    TextView upload_partner_1, upload_partner_2, upload_partner_3, upload_partner_4, upload_sole_1,
            upload_sole_2, upload_pp_1, upload_pp_2, upload_pp_3;

    @ViewById
    TextView image_partner_count_1, image_partner_count_2, image_partner_count_3, image_partner_count_4,
            image_sole_count_1, image_sole_count_2, image_pp_count_1, image_pp_count_2, image_pp_count_3;

    @ViewById
    NestedScrollView scroll_view;
    private BusinessDetailsModel selectedModel;
    private HashMap<String, ArrayList<Uri>> cameraFileUriMap = new HashMap<>();

    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        setUpListeners();

        enableDisableVerifyButton();
        Utility.hideKeyboard(this, btn_submit);
    }

    private void enableDisableVerifyButton() {

        if (et_business_type.getText().toString().length() > 0)
            Utility.enableButton(this, btn_submit, true);
        else
            Utility.enableButton(this, btn_submit, false);
    }


    private void setUpListeners() {
        upload_sole_1.setOnClickListener(uploadImageListener);
        upload_sole_2.setOnClickListener(uploadImageListener);

        upload_partner_1.setOnClickListener(uploadImageListener);
        upload_partner_2.setOnClickListener(uploadImageListener);
        upload_partner_3.setOnClickListener(uploadImageListener);
        upload_partner_4.setOnClickListener(uploadImageListener);

        upload_pp_1.setOnClickListener(uploadImageListener);
        upload_pp_2.setOnClickListener(uploadImageListener);
        upload_pp_3.setOnClickListener(uploadImageListener);

        view_sole_image_1.setOnClickListener(viewImageListener);
        view_sole_image_2.setOnClickListener(viewImageListener);

        view_partner_image_1.setOnClickListener(viewImageListener);
        view_partner_image_2.setOnClickListener(viewImageListener);
        view_partner_image_3.setOnClickListener(viewImageListener);
        view_partner_image_4.setOnClickListener(viewImageListener);

        view_pp_image_1.setOnClickListener(viewImageListener);
        view_pp_image_2.setOnClickListener(viewImageListener);
        view_pp_image_3.setOnClickListener(viewImageListener);

        et_business_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(BusinessDetailsActivity.this, btn_submit);

                ArrayList<BusinessDetailsModel> businessDetails = AppSession.getInstance(BusinessDetailsActivity.this).getBusinessDetails();
                ArrayList<String> businessTypes = new ArrayList<>();

                for (BusinessDetailsModel businessDetailsModel : businessDetails)
                    businessTypes.add(businessDetailsModel.getBusinessName());

                /**
                 * Reusing delivery distance here as it takes only a list of strings
                 */

                OptionListFragment optionListFragment = OptionListFragment.newInstance(businessTypes, Constants.DELIVERY_DISTANCE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, optionListFragment, "OptionListFragment");
                transaction.commitAllowingStateLoss();
                container.setVisibility(View.VISIBLE);
                scroll_view.setVisibility(View.GONE);
            }
        });
    }

    View.OnClickListener viewImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mSelectedSectionTag = (String) view.getTag();
            Intent intent = new Intent(BusinessDetailsActivity.this, ImagePreviewActivity_.class);
            intent.putExtra(Constants.FILE_URI, cameraFileUriMap.get(mSelectedSectionTag));
            intent.putExtra(Constants.IMAGE_TYPE, Constants.TYPE_URI);
            startActivity(intent);
        }
    };

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
            ArrayList<Uri> cameraFileUriList = cameraFileUriMap.get(mSelectedSectionTag);

            if (cameraFileUriList == null)
                cameraFileUriList = new ArrayList<>();
            cameraFileUriList.add(Utility.proceedToTakePicture(this));
            cameraFileUriMap.put(mSelectedSectionTag, cameraFileUriList);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_CAMERA: {

                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    ArrayList<Uri> cameraFileUriList = cameraFileUriMap.get(mSelectedSectionTag);
                    if (cameraFileUriList == null)
                        cameraFileUriList = new ArrayList<>();
                    cameraFileUriList.add(Utility.proceedToTakePicture(this));
                    cameraFileUriMap.put(mSelectedSectionTag, cameraFileUriList);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTIVITY_RESULT_CAMERA) {
            if (resultCode == RESULT_OK) {

                ArrayList<Uri> uriList = cameraFileUriMap.get(mSelectedSectionTag);
                Log.d("SHRUTI", "Image uri: " + uriList.get(uriList.size() - 1).toString());
                UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();

                changeUIState(PENDING);
                new RetrofitHelper(this).uploadFile(this, userRegistrationDetails.getId(), Constants.UPLOAD_TYPE_BUSINESS_TYPE, selectedModel.getBusinessId(), getImageIdFromTag(mSelectedSectionTag), uriList.get(uriList.size() - 1), new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                        changeUIState(SUCCESS);
                        et_business_type.setClickable(false);
                    }

                    @Override
                    public void onErrorResponse() {
                        changeUIState(FAILURE);
                    }
                });
            } else {
                ArrayList<Uri> uri = cameraFileUriMap.get(mSelectedSectionTag);
                uri.remove(uri.size() - 1);
                cameraFileUriMap.put(mSelectedSectionTag, uri);
            }
        }
    }

    private String getImageIdFromTag(String stringTag) {

        int tag = Integer.parseInt(stringTag);

        ArrayList<MainCategory> imageTypes = selectedModel.getBusinessTypes();

        if (selectedModel.getBusinessId().equalsIgnoreCase("1")) {

            for (MainCategory image : imageTypes) {
                if (tag == 1 && image.getName().equalsIgnoreCase(tv_sole_1.getText().toString()))
                    return image.getId();
                if (tag == 2 && image.getName().equalsIgnoreCase(tv_sole_2.getText().toString()))
                    return image.getId();
            }
        } else if (selectedModel.getBusinessId().equalsIgnoreCase("2")) {
            for (MainCategory image : imageTypes) {
                if (tag == 3 && image.getName().equalsIgnoreCase(tv_partner_1.getText().toString()))
                    return image.getId();
                if (tag == 4 && image.getName().equalsIgnoreCase(tv_partner_2.getText().toString()))
                    return image.getId();
                if (tag == 5 && image.getName().equalsIgnoreCase(tv_partner_3.getText().toString()))
                    return image.getId();
                if (tag == 6 && image.getName().equalsIgnoreCase(tv_partner_4.getText().toString()))
                    return image.getId();
            }
        } else if (selectedModel.getBusinessId().equalsIgnoreCase("3")) {
            for (MainCategory image : imageTypes) {
                if (tag == 7 && image.getName().equalsIgnoreCase(tv_pp_1.getText().toString()))
                    return image.getId();
                if (tag == 8 && image.getName().equalsIgnoreCase(tv_pp_2.getText().toString()))
                    return image.getId();
                if (tag == 9 && image.getName().equalsIgnoreCase(tv_pp_3.getText().toString()))
                    return image.getId();
            }
        }
        return null;
    }

    private void changeUIState(int status) {

        ArrayList<Uri> imageList = cameraFileUriMap.get(mSelectedSectionTag);

        int tag = Integer.parseInt(mSelectedSectionTag);
        switch (tag) {
            /**
             * Sole proprietorship
             */
            case 1:

                if (status == SUCCESS) {
                    upload_sole_1.setText("");
                    showSnackBar("Upload Success");
                    image_sole_count_1.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                    image_sole_count_1.setVisibility(View.VISIBLE);
                    view_sole_image_1.setVisibility(View.VISIBLE);
                    upload_sole_1.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == FAILURE) {
                    upload_sole_1.setText("");
                    imageList.remove(imageList.size() - 1);
                    cameraFileUriMap.put(mSelectedSectionTag, imageList);
                    showSnackBar("Upload Fail");

                    if (imageList.size() > 0) {
                        view_sole_image_1.setVisibility(View.VISIBLE);
                        image_sole_count_1.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                        image_sole_count_1.setVisibility(View.VISIBLE);
                    } else {
                        view_sole_image_1.setVisibility(View.GONE);
                        image_sole_count_1.setVisibility(View.GONE);
                    }
                    upload_sole_1.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == PENDING) {
                    upload_sole_1.setText("Uploading image...");
                    upload_sole_1.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.wait), null);
                }

                break;
            case 2:

                if (status == SUCCESS) {
                    upload_sole_2.setText("");
                    showSnackBar("Upload Success");
                    image_sole_count_2.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                    image_sole_count_2.setVisibility(View.VISIBLE);
                    view_sole_image_2.setVisibility(View.VISIBLE);
                    upload_sole_2.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == FAILURE) {
                    upload_sole_2.setText("");
                    imageList.remove(imageList.size() - 1);
                    cameraFileUriMap.put(mSelectedSectionTag, imageList);
                    showSnackBar("Upload Fail");

                    if (imageList.size() > 0) {
                        view_sole_image_2.setVisibility(View.VISIBLE);
                        image_sole_count_2.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                        image_sole_count_2.setVisibility(View.VISIBLE);
                    } else {
                        view_sole_image_2.setVisibility(View.GONE);
                        image_sole_count_2.setVisibility(View.GONE);
                    }
                    upload_sole_2.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == PENDING) {
                    upload_sole_2.setText("Uploading image...");
                    upload_sole_2.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.wait), null);
                }

                break;

            /**
             * Partnership
             */
            case 3:

                if (status == SUCCESS) {
                    upload_partner_1.setText("");
                    showSnackBar("Upload Success");
                    image_partner_count_1.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                    image_partner_count_1.setVisibility(View.VISIBLE);
                    view_partner_image_1.setVisibility(View.VISIBLE);
                    upload_partner_1.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == FAILURE) {
                    upload_partner_1.setText("");
                    imageList.remove(imageList.size() - 1);
                    cameraFileUriMap.put(mSelectedSectionTag, imageList);
                    showSnackBar("Upload Fail");

                    if (imageList.size() > 0) {
                        view_partner_image_1.setVisibility(View.VISIBLE);
                        image_partner_count_1.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                        image_partner_count_1.setVisibility(View.VISIBLE);
                    } else {
                        view_partner_image_1.setVisibility(View.GONE);
                        image_partner_count_1.setVisibility(View.GONE);
                    }
                    upload_partner_1.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == PENDING) {
                    upload_partner_1.setText("Uploading image...");
                    upload_partner_1.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.wait), null);
                }

                break;
            case 4:

                if (status == SUCCESS) {
                    upload_partner_2.setText("");
                    showSnackBar("Upload Success");
                    image_partner_count_2.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                    image_partner_count_2.setVisibility(View.VISIBLE);
                    view_partner_image_2.setVisibility(View.VISIBLE);
                    upload_partner_2.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == FAILURE) {
                    upload_partner_2.setText("");
                    imageList.remove(imageList.size() - 1);
                    cameraFileUriMap.put(mSelectedSectionTag, imageList);
                    showSnackBar("Upload Fail");

                    if (imageList.size() > 0) {
                        view_partner_image_2.setVisibility(View.VISIBLE);
                        image_partner_count_2.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                        image_partner_count_2.setVisibility(View.VISIBLE);
                    } else {
                        view_partner_image_2.setVisibility(View.GONE);
                        image_partner_count_2.setVisibility(View.GONE);
                    }
                    upload_partner_2.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == PENDING) {
                    upload_partner_2.setText("Uploading image...");
                    upload_partner_2.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.wait), null);
                }


                break;
            case 5:

                if (status == SUCCESS) {
                    upload_partner_3.setText("");
                    showSnackBar("Upload Success");
                    image_partner_count_3.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                    image_partner_count_3.setVisibility(View.VISIBLE);
                    view_partner_image_3.setVisibility(View.VISIBLE);
                    upload_partner_3.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == FAILURE) {
                    upload_partner_3.setText("");
                    imageList.remove(imageList.size() - 1);
                    cameraFileUriMap.put(mSelectedSectionTag, imageList);
                    showSnackBar("Upload Fail");

                    if (imageList.size() > 0) {
                        view_partner_image_3.setVisibility(View.VISIBLE);
                        image_partner_count_3.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                        image_partner_count_3.setVisibility(View.VISIBLE);
                    } else {
                        view_partner_image_3.setVisibility(View.GONE);
                        image_partner_count_3.setVisibility(View.GONE);
                    }
                    upload_partner_3.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == PENDING) {
                    upload_partner_3.setText("Uploading image...");
                    upload_partner_3.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.wait), null);
                }

                break;
            case 6:

                if (status == SUCCESS) {
                    upload_partner_4.setText("");
                    showSnackBar("Upload Success");
                    image_partner_count_4.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                    image_partner_count_4.setVisibility(View.VISIBLE);
                    view_partner_image_4.setVisibility(View.VISIBLE);
                    upload_partner_4.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == FAILURE) {
                    upload_partner_4.setText("");
                    imageList.remove(imageList.size() - 1);
                    cameraFileUriMap.put(mSelectedSectionTag, imageList);
                    showSnackBar("Upload Fail");

                    if (imageList.size() > 0) {
                        view_partner_image_4.setVisibility(View.VISIBLE);
                        image_partner_count_4.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                        image_partner_count_4.setVisibility(View.VISIBLE);
                    } else {
                        view_partner_image_4.setVisibility(View.GONE);
                        image_partner_count_4.setVisibility(View.GONE);
                    }
                    upload_partner_4.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == PENDING) {
                    upload_partner_4.setText("Uploading image...");
                    upload_partner_4.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.wait), null);
                }

                break;
            /**
             * public private
             */
            case 7:

                if (status == SUCCESS) {
                    upload_pp_1.setText("");
                    showSnackBar("Upload Success");
                    image_pp_count_1.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                    image_pp_count_1.setVisibility(View.VISIBLE);
                    view_pp_image_1.setVisibility(View.VISIBLE);
                    upload_pp_1.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == FAILURE) {
                    upload_pp_1.setText("");
                    imageList.remove(imageList.size() - 1);
                    cameraFileUriMap.put(mSelectedSectionTag, imageList);
                    showSnackBar("Upload Fail");

                    if (imageList.size() > 0) {
                        view_pp_image_1.setVisibility(View.VISIBLE);
                        image_pp_count_1.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                        image_pp_count_1.setVisibility(View.VISIBLE);
                    } else {
                        view_pp_image_1.setVisibility(View.GONE);
                        image_pp_count_1.setVisibility(View.GONE);
                    }
                    upload_pp_1.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == PENDING) {
                    upload_pp_1.setText("Uploading image...");
                    upload_pp_1.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.wait), null);
                }

                break;
            case 8:

                if (status == SUCCESS) {
                    upload_pp_2.setText("");
                    showSnackBar("Upload Success");
                    image_pp_count_2.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                    image_pp_count_2.setVisibility(View.VISIBLE);
                    view_pp_image_2.setVisibility(View.VISIBLE);
                    upload_pp_2.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == FAILURE) {
                    upload_pp_2.setText("");
                    imageList.remove(imageList.size() - 1);
                    cameraFileUriMap.put(mSelectedSectionTag, imageList);
                    showSnackBar("Upload Fail");

                    if (imageList.size() > 0) {
                        view_pp_image_2.setVisibility(View.VISIBLE);
                        image_pp_count_2.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                        image_pp_count_2.setVisibility(View.VISIBLE);
                    } else {
                        view_pp_image_2.setVisibility(View.GONE);
                        image_pp_count_2.setVisibility(View.GONE);
                    }
                    upload_pp_2.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == PENDING) {
                    upload_pp_2.setText("Uploading image...");
                    upload_pp_2.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.wait), null);
                }


                break;
            case 9:

                if (status == SUCCESS) {
                    upload_pp_3.setText("");
                    showSnackBar("Upload Success");
                    image_pp_count_3.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                    image_pp_count_3.setVisibility(View.VISIBLE);
                    view_pp_image_3.setVisibility(View.VISIBLE);
                    upload_pp_3.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == FAILURE) {
                    upload_pp_3.setText("");
                    imageList.remove(imageList.size() - 1);
                    cameraFileUriMap.put(mSelectedSectionTag, imageList);
                    showSnackBar("Upload Fail");

                    if (imageList.size() > 0) {
                        view_pp_image_3.setVisibility(View.VISIBLE);
                        image_pp_count_3.setText(getResources().getQuantityString(R.plurals.file_uploaded, imageList.size(), imageList.size()));
                        image_pp_count_3.setVisibility(View.VISIBLE);
                    } else {
                        view_pp_image_3.setVisibility(View.GONE);
                        image_pp_count_3.setVisibility(View.GONE);
                    }
                    upload_pp_3.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.attachment), null);
                } else if (status == PENDING) {
                    upload_pp_3.setText("Uploading image...");
                    upload_pp_3.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            ContextCompat.getDrawable(this, R.drawable.wait), null);
                }
                break;
        }
    }

    private String mSelectedSectionTag;
    View.OnClickListener uploadImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mSelectedSectionTag = (String) view.getTag();
            checkCameraPermission();
        }
    };

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.business_details));
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
        return true;
    }

    private void makeUploadDataToServerCall() {
        /**
         * This API is to fetch categories from server
         */

        btn_submit.setVisibility(View.VISIBLE);
        btn_submit_progress.setVisibility(View.GONE);


        ApiHelper.makeDashboardDataCall(this, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        Type classType = new TypeToken<DashboardModel>() {
                        }.getType();

                        DashboardModel dashboardModel = new Gson().fromJson(jsonObject.toString(), classType);
                        AppSession.getInstance(BusinessDetailsActivity.this).setDashboardData(dashboardModel);

                        int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
                        Intent intent = RegistrationResolver.getNextIntent(BusinessDetailsActivity.this, registrationIndex);

                        if(dashboardModel.getForceUpdate() != null){
                            if (dashboardModel.getForceUpdate().equalsIgnoreCase("TRUE"))
                                UpgradeHelper.invokeUpdateDialog(BusinessDetailsActivity.this, true);
                            else if (dashboardModel.getForceUpdate().equalsIgnoreCase("FALSE"))
                                UpgradeHelper.invokeUpdateDialog(BusinessDetailsActivity.this, false);
                        }else {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    finish();
                }
            }

            @Override
            public void onErrorResponse() {
                finish();
            }
        });
    }


    @Override
    public void onListItemSelected(Object item, int identifier) {
        String selectedItem = (String) item;
        et_business_type.setText(selectedItem);

        container.setVisibility(View.GONE);
        scroll_view.setVisibility(View.VISIBLE);
        scroll_view.smoothScrollTo(0, et_business_type.getBottom());

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("OptionListFragment");
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        enableDisableVerifyButton();

        resolveUIBasedOnSelection();
    }

    private BusinessDetailsModel getModelByName(String name) {
        ArrayList<BusinessDetailsModel> businessDetails = AppSession.getInstance(BusinessDetailsActivity.this).getBusinessDetails();

        for (BusinessDetailsModel model : businessDetails) {
            if (name.equalsIgnoreCase(model.getBusinessName()))
                return model;
        }

        return null;
    }

    private void resolveUIBasedOnSelection() {

        String selectedBusinessType = et_business_type.getText().toString();

        sole_proprietorship.setVisibility(View.GONE);
        partnership.setVisibility(View.GONE);
        public_private_company.setVisibility(View.GONE);

        selectedModel = getModelByName(selectedBusinessType);

        if (selectedModel != null) {
            if (selectedModel.getBusinessId().equalsIgnoreCase("1")) {
                sole_proprietorship.setVisibility(View.VISIBLE);
            } else if (selectedModel.getBusinessId().equalsIgnoreCase("2")) {
                partnership.setVisibility(View.VISIBLE);
            } else if (selectedModel.getBusinessId().equalsIgnoreCase("3"))
                public_private_company.setVisibility(View.VISIBLE);
        }

        setSectionNamesFromServerData();
    }

    private void setSectionNamesFromServerData() {

        if (selectedModel != null) {
            ArrayList<MainCategory> businessDocTypes = selectedModel.getBusinessTypes();

            if (selectedModel.getBusinessId().equalsIgnoreCase("1")) {
                tv_sole_1.setText(businessDocTypes.get(0).getName());
                tv_sole_2.setText(businessDocTypes.get(1).getName());
            } else if (selectedModel.getBusinessId().equalsIgnoreCase("2")) {
                tv_partner_1.setText(businessDocTypes.get(0).getName());
                tv_partner_2.setText(businessDocTypes.get(1).getName());
                tv_partner_3.setText(businessDocTypes.get(2).getName());
                tv_partner_4.setText(businessDocTypes.get(3).getName());
            } else if (selectedModel.getBusinessId().equalsIgnoreCase("3")) {
                tv_pp_1.setText(businessDocTypes.get(0).getName());
                tv_pp_2.setText(businessDocTypes.get(1).getName());
                tv_pp_3.setText(businessDocTypes.get(2).getName());
            }
        }
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
