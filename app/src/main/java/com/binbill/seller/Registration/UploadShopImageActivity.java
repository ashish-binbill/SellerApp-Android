package com.binbill.seller.Registration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.Dashboard.ProfileActivity;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_upload_image)
public class UploadShopImageActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById(R.id. textViewUpload)
    TextView  textViewUpload;

    @ViewById(R.id.iv_uploadImage)
    ImageView iv_uploadImage;

    @ViewById(R.id.camBtn)
    ImageButton camBtn;

    @ViewById(R.id.btn_next)
    SquareAppButton btn_next;

    public static boolean isFromProfile;

    UserRegistrationDetails userRegistrationDetails;
    private ArrayList<Uri> cameraFileUri = new ArrayList<>();

    @AfterViews
    public void initiateViews() {
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        setUpToolbar();
        setUpListeners();
    }

    private void setUpListeners() {

        iv_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText("Upload Shop Image");
    }

    @Click(R.id.camBtn)
    public void OpenCamera(View CameraView){
        checkCameraPermission();
    }

    @Click(R.id.btn_next)
    public void SubmitBtn(View view){
        if(!isFromProfile) {
            int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
            Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
           // isFromProfile = false;
            finish();
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
            cameraFileUri.clear();
            cameraFileUri.add(Utility.proceedToTakePicture(this));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTIVITY_RESULT_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    ProfileActivity.cameraURI = cameraFileUri.get(0);
                 //  iv_uploadImage.setImageURI(cameraFileUri.get(0));
                    textViewUpload.setVisibility(View.GONE);
                  //  Bitmap bitmap = BitmapFactory.decodeFile(cameraFileUri.get(0));
                  //  Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    //iv_uploadImage.setImageBitmap(bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }
              //  Bitmap photo = (Bitmap) data.getExtras().get("data");
             //   iv_uploadImage.setImageBitmap(photo);

              //  Log.d("SHRUTI", "Image uri: " + cameraFileUri.get(cameraFileUri.size() - 1).toString());

                //  et_upload_shop_image.setText("Uploading image...");
                camBtn.setVisibility(View.VISIBLE);
                textViewUpload.setVisibility(View.GONE);
                camBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.wait));
              /*  et_upload_shop_image.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(this, R.drawable.wait), null);*/

                new RetrofitHelper(this).uploadFile(this, userRegistrationDetails.getId(), Constants.UPLOAD_TYPE_SELLER_SHOP, cameraFileUri.get(cameraFileUri.size() - 1), new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {

                        // et_upload_shop_image.setText("");
                        if(isFromProfile) {
                            isFromProfile = false;
                            finish();
                        }else{
                            showSnackBar("Upload Success");
                            camBtn.setVisibility(View.VISIBLE);
                            camBtn.setImageDrawable(ContextCompat.getDrawable(UploadShopImageActivity.this,
                                    R.drawable.ic_retake));
                        }

                    }

                    @Override
                    public void onErrorResponse() {
                        //   et_upload_shop_image.setText("");
                        //  cameraFileUri.remove(cameraFileUri.size() - 1);
                        showSnackBar("Upload Fail");
                        if (cameraFileUri.size() > 0) {
                            camBtn.setVisibility(View.VISIBLE);
                        } else {
                            camBtn.setVisibility(View.VISIBLE);
                            //image_count.setVisibility(View.GONE);
                        }

                    }
                });
            } else {
               // cameraFileUri.remove(cameraFileUri.size() - 1);
            }
        }
    }

}
