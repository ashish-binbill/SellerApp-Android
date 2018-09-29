package com.binbill.seller.Offers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

@EActivity(R.layout.activity_default_banner)
public class DefaultBannerActivity extends BaseActivity implements DefaultBannerAdapter.BannerInteractionListener {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    RecyclerView rv_banner;

    @ViewById
    FrameLayout ll_select_banner_text;

    @ViewById
    ImageView iv_banner;

    @ViewById
    EditText et_offer_title;

    @ViewById
    TextView tv_banner_text;

    @ViewById
    FrameLayout fl_image_text;

    @ViewById
    AppButton btn_upload;

    ArrayList<Drawable> bannerList = new ArrayList<>();


    @AfterViews
    public void setUpView() {
        setUpToolbar();
        setUpDefaultBanners();
    }

    private void setUpDefaultBanners() {
        bannerList.add(ContextCompat.getDrawable(this, R.drawable.offer_banner_1));
        bannerList.add(ContextCompat.getDrawable(this, R.drawable.offer_banner_2));
        bannerList.add(ContextCompat.getDrawable(this, R.drawable.offer_banner_3));

        rv_banner.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_banner.setLayoutManager(llm);
        DefaultBannerAdapter mAdapter = new DefaultBannerAdapter(this, bannerList);
        rv_banner.setAdapter(mAdapter);
        rv_banner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBannerSelected(int position) {
        Drawable drawable = bannerList.get(position);
        setUpBannerText(position, drawable);
    }

    private void setUpBannerText(int position, Drawable drawable) {
        ll_select_banner_text.setVisibility(View.VISIBLE);
        rv_banner.setVisibility(View.GONE);

        iv_banner.setImageDrawable(drawable);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switch (position) {
            case 0:
                params.setMargins(Utility.convertDPtoPx(this, 40), Utility.convertDPtoPx(this, 30), Utility.convertDPtoPx(this, 200), 0);
                fl_image_text.setLayoutParams(params);
                tv_banner_text.setTextColor(ContextCompat.getColor(this, R.color.color_black));
                break;
            case 1:
                params.setMargins(Utility.convertDPtoPx(this, 40), Utility.convertDPtoPx(this, 30), Utility.convertDPtoPx(this, 200), 0);
                fl_image_text.setLayoutParams(params);
                tv_banner_text.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                break;
            case 2:
                params.setMargins(Utility.convertDPtoPx(this, 200), Utility.convertDPtoPx(this, 30), Utility.convertDPtoPx(this, 40), 0);
                fl_image_text.setLayoutParams(params);
                tv_banner_text.setTextColor(ContextCompat.getColor(this, R.color.color_black));
                break;
        }

        setUpListener();
    }

    private void setUpListener() {

        et_offer_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_banner_text.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_offer_title.setVisibility(View.GONE);
                btn_upload.setVisibility(View.GONE);

                createBitmapFolayout();
            }
        });
    }

    private void createBitmapFolayout() {

        ll_select_banner_text.setDrawingCacheEnabled(true);
        ll_select_banner_text.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        ll_select_banner_text.layout(0, 0, ll_select_banner_text.getMeasuredWidth(), ll_select_banner_text.getMeasuredHeight());

        ll_select_banner_text.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(ll_select_banner_text.getDrawingCache());
        ll_select_banner_text.setDrawingCacheEnabled(false);
        saveBitmap(b);
    }

    private void saveBitmap(Bitmap pictureBitmap) {
        File file = null;
        try {
            OutputStream fOut = null;
            file = Utility.getOutputMediaFile();
            fOut = new FileOutputStream(file);
            pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (Exception e) {

        }

        if(file != null){
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constants.DEFAULT_BANNER, file);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (ll_select_banner_text.getVisibility() == View.VISIBLE) {
            ll_select_banner_text.setVisibility(View.GONE);
            setUpDefaultBanners();
        } else
            super.onBackPressed();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.select_banner));
    }
}
