package com.binbill.seller.Customer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.binbill.seller.Dashboard.MyCustomerFragment;
import com.binbill.seller.Order.ActiveOrderFragment;
import com.binbill.seller.Order.NotRespondedOrderFragment;
import com.binbill.seller.Order.PastOrderFragment;

public class CustomerPagerAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Connected", "Invited"};
    public static int positionPager;

    public CustomerPagerAdapter(FragmentManager fm) {
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
                positionPager = 0;
                return MyCustomerFragment.newInstance();
            case 1:
                positionPager = 1;
                return InvitedCustomerFragment.newInstance();
        }
        return null;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

