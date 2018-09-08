package com.binbill.seller.Order;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Offers.OfferItem;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_select_delivery_agent)
public class SelectDeliveryAgentActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    RecyclerView rv_delivery_agents;

    @ViewById
    LinearLayout shimmer_view_container;

    @ViewById
    LinearLayout no_data_layout;

    @ViewById
    TextView tv_no_data, iv_skip;

    @ViewById
    SwipeRefreshLayout sl_pull_to_refresh;

    @ViewById
    AppButton btn_no_data;
    private ArrayList<OfferItem> mOfferList;


    @AfterViews
    public void setUpView() {
        setUpData();
        setUpToolbar();
        setUpListeners();
        makeDeliveryBoyFetchApiCall();
    }

    private void setUpData() {
        tv_no_data.setText(getString(R.string.no_delivery_boys));
    }

    private void makeDeliveryBoyFetchApiCall() {

        rv_delivery_agents.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        no_data_layout.setVisibility(View.GONE);

        new RetrofitHelper(this).fetchDeliveryBoysForSeller(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response);
                sl_pull_to_refresh.setRefreshing(false);
            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));
                showNoDataLayout();
                sl_pull_to_refresh.setRefreshing(false);
            }
        });
    }

    private void handleResponse(String response) {

    }

    private void showNoDataLayout() {

    }

    private void setUpListeners() {
        iv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        sl_pull_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeDeliveryBoyFetchApiCall();
            }
        });


    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.assign_delivery_boy));

        iv_skip.setVisibility(View.VISIBLE);
    }


}
