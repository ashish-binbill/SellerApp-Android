package com.binbill.seller.Offers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.YesNoDialogFragment;
import com.binbill.seller.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_offer)
public class OfferActivity extends BaseActivity implements YesNoDialogFragment.YesNoClickInterface {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ImageView iv_notification;


    @ViewById
    TabLayout tab_strip;

    @ViewById
    ViewPager view_pager;
    private OfferFragmentPagerAdapter mPagerAdapter;

    @AfterViews
    public void setUpView() {
        setUpToolbar();
        setUpListeners();

        setUpViewPager();
    }

    private void setUpViewPager() {

        mPagerAdapter = new OfferFragmentPagerAdapter(getSupportFragmentManager());

        if (view_pager != null) {
            view_pager.setOffscreenPageLimit(0);
            view_pager.setAdapter(mPagerAdapter);
        }

        if (tab_strip != null) {
            tab_strip.setupWithViewPager(view_pager);
        }

        tab_strip.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void invokeAddOfferOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_offer_type, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final Dialog offerDialog = builder.create();
        offerDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        offerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout normalOffer = (RelativeLayout) dialogView.findViewById(R.id.rl_normal_offer);
        RelativeLayout barcodeOffer = (RelativeLayout) dialogView.findViewById(R.id.rl_barcode_offer);

        normalOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfferActivity.this, AddOfferActivity_.class);
                startActivity(intent);

                offerDialog.dismiss();
            }
        });

        barcodeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfferActivity.this, AddBarCodeOfferActivity_.class);
                startActivity(intent);

                offerDialog.dismiss();
            }
        });

        TextView headerTitle = (TextView) dialogView.findViewById(R.id.header);
        headerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offerDialog.dismiss();
            }
        });

        offerDialog.show();
    }

    private void setUpListeners() {
        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                invokeAddOfferOptions();
                Intent intent = new Intent(OfferActivity.this, AddBarCodeOfferActivity_.class);
                startActivity(intent);
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.my_offers));

        iv_notification.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_offer));
        iv_notification.setVisibility(View.VISIBLE);
    }

    public void showYesNoDialog() {
        YesNoDialogFragment fragment = YesNoDialogFragment.newInstance(getString(R.string.delete_offer), "Delete Offer");
        fragment.show(getSupportFragmentManager(), "YesNoDialogFragment");
    }

    @Override
    public void onOptionSelected(boolean isProceed) {
        Fragment page = (Fragment) mPagerAdapter.instantiateItem(view_pager, view_pager.getCurrentItem());
        if (view_pager.getCurrentItem() == 0) {
//            ((NormalOfferFragment) page).onOptionSelected(isProceed);
//        } else {
            ((BarCodeOfferFragment) page).onOptionSelected(isProceed);
        }
    }
}
