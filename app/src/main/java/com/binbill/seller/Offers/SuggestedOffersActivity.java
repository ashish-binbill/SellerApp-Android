package com.binbill.seller.Offers;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.YesNoDialogFragment;
import com.binbill.seller.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_suggested_offers)
public class SuggestedOffersActivity extends BaseActivity implements YesNoDialogFragment.YesNoClickInterface {

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
    private SuggestedOfferFragmentPagerAdapter mPagerAdapter;

    @AfterViews
    public void setUpView() {
        setUpToolbar();

        setUpViewPager();
    }

    private void setUpViewPager() {

        mPagerAdapter = new SuggestedOfferFragmentPagerAdapter(getSupportFragmentManager());

        if (view_pager != null) {
            view_pager.setOffscreenPageLimit(0);
            view_pager.setAdapter(mPagerAdapter);
        }

        if (tab_strip != null) {
            tab_strip.setupWithViewPager(view_pager);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.suggested_offers));

        iv_notification.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_offer));
        iv_notification.setVisibility(View.GONE);
    }

    public void showYesNoDialog() {
        YesNoDialogFragment fragment = YesNoDialogFragment.newInstance(getString(R.string.delete_offer), "Delete Offer");
        fragment.show(getSupportFragmentManager(), "YesNoDialogFragment");
    }

    @Override
    public void onOptionSelected(boolean isProceed) {
        Fragment page = (Fragment) mPagerAdapter.instantiateItem(view_pager, view_pager.getCurrentItem());
        ((NormalOfferFragment) page).onOptionSelected(isProceed);
    }
}
