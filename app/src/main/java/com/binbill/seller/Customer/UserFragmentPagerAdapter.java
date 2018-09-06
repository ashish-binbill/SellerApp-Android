package com.binbill.seller.Customer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.binbill.seller.Dashboard.VerificationFragment;
import com.binbill.seller.Model.UserModel;

/**
 * Created by shruti.vig on 9/3/18.
 */

public class UserFragmentPagerAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 3;
    private final UserModel userModel;
    private String tabTitles[] = new String[]{"Credit Details", "Loyalty Points", "Transactions"};

    public UserFragmentPagerAdapter(FragmentManager fm, UserModel model) {
        super(fm);
        this.userModel = model;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return CreditsFragment.newInstance(userModel);
            case 1:
                return LoyaltyFragment.newInstance(userModel);
            case 2:
                return TransactionFragment.newInstance(userModel);

        }
        return null;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
