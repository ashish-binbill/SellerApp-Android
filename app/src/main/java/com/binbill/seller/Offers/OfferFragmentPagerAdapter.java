package com.binbill.seller.Offers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.binbill.seller.Order.ActiveOrderFragment;
import com.binbill.seller.Order.NotRespondedOrderFragment;
import com.binbill.seller.Order.PastOrderFragment;

public class OfferFragmentPagerAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 1;
    private String tabTitles[] = new String[]{"BarCode"};

    public OfferFragmentPagerAdapter(FragmentManager fm) {
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
//                return NormalOfferFragment.newInstance();
//            case 1:
                return BarCodeOfferFragment.newInstance();

        }
        return null;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}


