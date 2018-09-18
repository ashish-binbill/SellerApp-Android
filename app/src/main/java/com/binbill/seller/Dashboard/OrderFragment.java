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

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.Order.OrderFragmentPagerAdapter;
import com.binbill.seller.R;

/**
 * Created by shruti.vig on 9/6/18.
 */

public class OrderFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabStrip;

    public OrderFragment() {
    }

    public static OrderFragment newInstance() {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.fragment_order_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
//        ApiHelper.fetchOrders(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabStrip = (TabLayout) view.findViewById(R.id.tab_strip);

        setUpViewPager();
    }

    private void setUpViewPager() {
        OrderFragmentPagerAdapter mPagerAdapter = new OrderFragmentPagerAdapter(getChildFragmentManager());

        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(0);
            viewPager.setAdapter(mPagerAdapter);
        }

        if (tabStrip != null) {
            tabStrip.setupWithViewPager(viewPager);
        }
    }
}
