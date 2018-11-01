package com.binbill.seller.Order;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.binbill.seller.Customer.CreditsFragment;
import com.binbill.seller.Customer.LoyaltyFragment;
import com.binbill.seller.Customer.TransactionFragment;
import com.binbill.seller.Model.UserModel;

/**
 * Created by shruti.vig on 9/6/18.
 */

public class OrderFragmentPagerAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Active", "Past","Not Responded"};

    public OrderFragmentPagerAdapter(FragmentManager fm) {
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
                return ActiveOrderFragment.newInstance();
            case 1:
                return PastOrderFragment.newInstance();
            case 2:
                return NotRespondedOrderFragment.newInstance();

        }
        return null;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

