package com.binbill.seller.Offers;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.BottomSheetHelper;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

@EActivity(R.layout.activity_add_offer)
public class AddOfferActivity extends BaseActivity implements BottomSheetHelper.BottomSheetClickInterface {


    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    TextView tv_upload_image, tv_select_from_template, tv_error_offer_name, tv_error_offer_description, tv_error_offer_expiry;
    @ViewById
    EditText et_offer_name, et_offer_description, et_expiry_date;

    @ViewById
    AppButton btn_submit;

    @ViewById
    LinearLayout btn_submit_progress;

    @ViewById
    NestedScrollView scroll_view;

    @ViewById
    PhotoView iv_offer;

    Calendar mCalendar = Calendar.getInstance();
    private JSONArray fileDetailsJson = new JSONArray();;
    private String mOfferId;
    private ArrayList<String> bottomSheetItems;
    private Uri cameraFileUri;

    @AfterViews
    public void setUpView() {
        SpannableString content = new SpannableString(getString(R.string.upload_offer_image));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_upload_image.setText(content);

        SpannableString content2 = new SpannableString(getString(R.string.select_from_default));
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        tv_select_from_template.setText(content2);

        enableDisableVerifyButton();
        setUpToolbar();
        setUpListener();

        if (getIntent().hasExtra(Constants.OFFER_ITEM)) {
            setUpData((OfferItem) getIntent().getSerializableExtra(Constants.OFFER_ITEM));
        }
    }

    private void setUpData(OfferItem offer) {
        final String authToken = SharedPref.getString(this, SharedPref.AUTH_TOKEN);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        return response.request().newBuilder()
                                .header("Authorization", authToken)
                                .build();
                    }
                }).build();

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();

        picasso.load(Constants.BASE_URL + "offer/" + offer.getOfferId() + "/images/0")
                .config(Bitmap.Config.RGB_565)
                .fit().centerCrop()
                .into(iv_offer);

        et_offer_name.setText(offer.getOfferTitle());
        et_offer_description.setText(offer.getOfferDescription());
        et_expiry_date.setText(Utility.getFormattedDate(12, offer.getOfferEndDate(), 0));

        mOfferId = offer.getOfferId();

        fileDetailsJson = new JSONArray(offer.getOfferFiles());
    }

    private void setUpListener() {

        et_expiry_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddOfferActivity.this, datePickerListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        tv_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetItems = new ArrayList<>();
                bottomSheetItems.add(getString(R.string.take_picture));
                bottomSheetItems.add(getString(R.string.choose_from_gallery));

                ArrayList<Drawable> bottomSheetIcons = new ArrayList<>();
                bottomSheetIcons.add(ContextCompat.getDrawable(AddOfferActivity.this, R.drawable.ic_camera));
                bottomSheetIcons.add(ContextCompat.getDrawable(AddOfferActivity.this, R.drawable.ic_gallery));

                BottomSheetHelper.showBottomSheet(AddOfferActivity.this, bottomSheetItems, bottomSheetIcons, AddOfferActivity.this);
            }
        });

        et_offer_name.addTextChangedListener(textWatcher);
        et_offer_description.addTextChangedListener(textWatcher);
        et_expiry_date.addTextChangedListener(textWatcher);

        tv_select_from_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AddOfferActivity.this, DefaultBannerActivity_.class), Constants.SELECT_DEFAULT_IMAGE);
            }
        });

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enableDisableVerifyButton();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void uploadDefaultImage(final Uri fileUri) {
        tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(this, R.drawable.wait), null);

        new RetrofitHelper(this).uploadFile(this, AppSession.getInstance(this).getSellerId(), Constants.UPLOAD_TYPE_SELLER_OFFER, fileUri, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                showSnackBar("Upload Success");
                tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                String uriString = Utility.getPath(AddOfferActivity.this, fileUri);
                Picasso.get()
                        .load("file://" + uriString)
                        .config(Bitmap.Config.RGB_565)
                        .fit().centerCrop()
                        .into(iv_offer);

                processImageResponse(response);
            }

            @Override
            public void onErrorResponse() {
                showSnackBar("Upload Fail");
                tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.SELECT_DEFAULT_IMAGE) {
            if (resultCode == RESULT_OK) {

                File file = (File) data.getSerializableExtra(Constants.DEFAULT_BANNER);
                Uri returnValue = Uri.fromFile(file);
                uploadDefaultImage(returnValue);
            }
        }

        if (requestCode == Constants.ACTIVITY_RESULT_CAMERA) {
            if (resultCode == RESULT_OK) {
                Log.d("SHRUTI", "Image uri: " + cameraFileUri.toString());

                tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(this, R.drawable.wait), null);

                if (mOfferId == null) {

                    new RetrofitHelper(this).uploadFile(this, AppSession.getInstance(this).getSellerId(), Constants.UPLOAD_TYPE_SELLER_OFFER, cameraFileUri, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {

                            showSnackBar("Upload Success");
                            tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                            String uriString = Utility.getPath(AddOfferActivity.this, cameraFileUri);
                            Picasso.get()
                                    .load("file://" + uriString)
                                    .config(Bitmap.Config.RGB_565)
                                    .fit().centerCrop()
                                    .into(iv_offer);

                            processImageResponse(response);
                        }

                        @Override
                        public void onErrorResponse() {
                            showSnackBar("Upload Fail");
                            tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            cameraFileUri = null;
                        }
                    });
                } else {
                    new RetrofitHelper(this).updateOfferImage(this, mOfferId, cameraFileUri, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {

                            showSnackBar("Upload Success");
                            tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                            String uriString = Utility.getPath(AddOfferActivity.this, cameraFileUri);
                            Picasso.get()
                                    .load("file://" + uriString)
                                    .config(Bitmap.Config.RGB_565)
                                    .fit().centerCrop()
                                    .into(iv_offer);

                            processImageResponse(response);
                        }

                        @Override
                        public void onErrorResponse() {
                            showSnackBar("Upload Fail");
                            tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            cameraFileUri = null;
                        }
                    });
                }
            } else {
                cameraFileUri = null;
            }
        }

        if (requestCode == Constants.ACTIVITY_RESULT_GALLERY) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Log.d("SHRUTI", "Image uri: " + data.getData());

                final Uri fileUri = data.getData();
                tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(this, R.drawable.wait), null);

                if (mOfferId == null) {
                    new RetrofitHelper(this).uploadFile(this, AppSession.getInstance(this).getSellerId(), Constants.UPLOAD_TYPE_SELLER_OFFER, fileUri, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {

                            showSnackBar("Upload Success");
                            tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                            String uriString = Utility.getPath(AddOfferActivity.this, fileUri);
                            Picasso.get()
                                    .load("file://" + uriString)
                                    .config(Bitmap.Config.RGB_565)
                                    .fit().centerCrop()
                                    .into(iv_offer);

                            processImageResponse(response);
                        }

                        @Override
                        public void onErrorResponse() {
                            showSnackBar("Upload Fail");
                            tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                    });
                } else {
                    final Uri fileUri1 = data.getData();
                    new RetrofitHelper(this).updateOfferImage(this, mOfferId, fileUri1, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {

                            showSnackBar("Upload Success");
                            tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                            String uriString = Utility.getPath(AddOfferActivity.this, fileUri1);
                            Picasso.get()
                                    .load("file://" + uriString)
                                    .config(Bitmap.Config.RGB_565)
                                    .fit().centerCrop()
                                    .into(iv_offer);

                            processImageResponse(response);
                        }

                        @Override
                        public void onErrorResponse() {
                            showSnackBar("Upload Fail");
                            tv_upload_image.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                    });
                }
            }
        }
    }

    private void processImageResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject fileJson = jsonObject.optJSONObject("file_details");
            fileDetailsJson.put(fileJson);

        } catch (JSONException e) {

        }

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

        String title = et_offer_name.getText().toString();
        String description = et_offer_description.getText().toString();
        String expiry = et_expiry_date.getText().toString();

        new RetrofitHelper(this).addOfferFromSeller(title, description, expiry, fileDetailsJson.toString(), mOfferId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {
                        JSONObject sellerOffer = jsonObject.getJSONObject("seller_offer");

                        Type classType = new TypeToken<OfferItem>() {
                        }.getType();

                        OfferItem offerItem = new Gson().fromJson(sellerOffer.toString(), classType);

                        Intent addIntent = new Intent(AddOfferActivity.this, PublishOfferToUserActivity_.class);
                        addIntent.putExtra(Constants.OFFER_ITEM, offerItem);
                        addIntent.putExtra(Constants.FLOW_TYPE, Constants.ADD_USER_FOR_OFFER);
                        addIntent.putExtra(Constants.CREATE_OFFER, true);
                        startActivity(addIntent);
                        finish();
                    } else {
                        showSnackBar(getString(R.string.something_went_wrong));
                        btn_submit.setVisibility(View.VISIBLE);
                        btn_submit_progress.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    showSnackBar(getString(R.string.something_went_wrong));
                    btn_submit.setVisibility(View.VISIBLE);
                    btn_submit_progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));
                btn_submit.setVisibility(View.VISIBLE);
                btn_submit_progress.setVisibility(View.GONE);
            }
        });
    }

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        et_expiry_date.setText(sdf.format(mCalendar.getTime()));
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.create_new_offer));
    }

    private void enableDisableVerifyButton() {

        String offerName = et_offer_name.getText().toString();
        String offerDescription = et_offer_description.getText().toString();
        String offerExpiry = et_expiry_date.getText().toString();

        if (!Utility.isEmpty(offerName.trim()) && !Utility.isEmpty(offerDescription.trim()) && !Utility.isEmpty(offerExpiry.trim()))
            Utility.enableButton(this, btn_submit, true);
        else
            Utility.enableButton(this, btn_submit, false);
    }

    private boolean isValid() {

        /**
         * OFFER NAME
         */
        String offerName = et_offer_name.getText().toString().trim();
        if (TextUtils.isEmpty(offerName)) {
            tv_error_offer_name.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_offer_name.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, tv_error_offer_name.getBottom());
            return false;
        }

        /**
         * OFFER DESCRIPTION
         */
        String offerDescription = et_offer_description.getText().toString().trim();
        if (TextUtils.isEmpty(offerDescription)) {
            tv_error_offer_description.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_offer_description.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, tv_error_offer_description.getBottom());
            return false;
        }

        /**
         * OFFER EXPIRY
         */
        String officeExpiry = et_expiry_date.getText().toString().trim();
        if (TextUtils.isEmpty(officeExpiry)) {
            tv_error_offer_expiry.setText(getString(R.string.error_field_cannot_be_empty));
            tv_error_offer_expiry.setVisibility(View.VISIBLE);
            scroll_view.scrollTo(0, tv_error_offer_expiry.getBottom());
            return false;
        }

        return true;
    }

    @Override
    public void onRowClicked(int position) {

        String selectedRow = bottomSheetItems.get(position);
        switch (position) {
            case 0:
                checkCameraPermission();
                break;
            case 1:
                Utility.proceedToPickImageFromGallery(AddOfferActivity.this);
                break;

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
            cameraFileUri = Utility.proceedToTakePicture(this);
        }
    }
}
