package com.binbill.seller.Dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binbill.seller.Customer.CustomerPagerAdapter;
import com.binbill.seller.Customer.InvitedCustomerFragment;
import com.binbill.seller.Order.OrderFragmentPagerAdapter;
import com.binbill.seller.R;

public class CustomerFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabStrip;
    private int pageToDisplay = 0;

    public CustomerFragment() {
    }

    public static CustomerFragment newInstance(int page) {
        CustomerFragment fragment = new CustomerFragment();
        Bundle args = new Bundle();
        args.putInt("Page", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabStrip = (TabLayout) view.findViewById(R.id.tab_strip);

        pageToDisplay = getArguments().getInt("Page");

        setUpViewPager();
    }

    private void setUpViewPager() {
        CustomerPagerAdapter mPagerAdapter = new CustomerPagerAdapter(getChildFragmentManager());

        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(0);
            viewPager.setAdapter(mPagerAdapter);
        }

        if (tabStrip != null) {
            tabStrip.setupWithViewPager(viewPager);
        }

        viewPager.setCurrentItem(pageToDisplay);

    }

    public void showSearchView() {
        Fragment page = getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
        if (page != null)
            if (viewPager.getCurrentItem() == 0) {
                ((MyCustomerFragment) page).showSearchView();
            } else {
                ((InvitedCustomerFragment) page).showSearchView();
            }
    }
}
