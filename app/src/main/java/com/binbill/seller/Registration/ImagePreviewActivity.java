package com.binbill.seller.Registration;

import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.binbill.seller.Adapter.ImagePreviewAdapter;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.Model.FileItem;
import com.binbill.seller.Model.JobCopy;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.ViewPagerExceptionHandled;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@EActivity(R.layout.activity_image_preview)
public class ImagePreviewActivity extends BaseActivity {

    @ViewById
    ViewPagerExceptionHandled viewPager;

    @ViewById
    TextView upload_date, seller_id;

    @ViewById
    ImageButton close;

    @AfterViews
    public void setUpViews() {

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = df.format(c);

        upload_date.setText(formattedDate);

        UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        seller_id.setText("Seller Id: " + userRegistrationDetails.getId());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int type = getIntent().getIntExtra(Constants.IMAGE_TYPE, Constants.TYPE_URI);
        if (type == Constants.TYPE_URI) {
            ArrayList<Uri> files = (ArrayList<Uri>) getIntent().getSerializableExtra(Constants.FILE_URI);
            populatePager(files, type);
        } else if (type == Constants.TYPE_URL) {
            ArrayList<JobCopy> files = (ArrayList<JobCopy>) getIntent().getSerializableExtra(Constants.FILE_URI);
            populatePager(files, type);
        } else if (type == Constants.TYPE_URL_FILE) {
            ArrayList<FileItem> files = (ArrayList<FileItem>) getIntent().getSerializableExtra(Constants.FILE_URI);
            populatePager(files, type);
        }
    }

    public void populatePager(ArrayList<?> images, int type) {
        ImagePreviewAdapter imagePreviewAdapter = new ImagePreviewAdapter(this, images, type);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(20);
        viewPager.setAdapter(imagePreviewAdapter);
    }

}
