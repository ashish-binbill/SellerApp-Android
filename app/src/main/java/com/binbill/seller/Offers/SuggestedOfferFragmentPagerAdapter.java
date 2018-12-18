package com.binbill.seller.Offers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.binbill.seller.Constants;

public class SuggestedOfferFragmentPagerAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 5;
    private String tabTitles[] = new String[]{"New Product", "Discount Offers", "BOGO", "Extra Quantity", "General Offers"};

    public SuggestedOfferFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return NormalOfferFragment.newInstance(Constants.OFFER_TYPE_NEW_PRODUCT);
            case 1:
                return NormalOfferFragment.newInstance(Constants.OFFER_TYPE_DISCOUNTED);
            case 2:
                return NormalOfferFragment.newInstance(Constants.OFFER_TYPE_BOGO);
            case 3:
                return NormalOfferFragment.newInstance(Constants.OFFER_TYPE_EXTRA);
            case 4:
                return NormalOfferFragment.newInstance(Constants.OFFER_TYPE_GENERAL);

        }
        return null;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

