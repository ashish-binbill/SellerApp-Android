package com.binbill.seller.Order;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

@EActivity(R.layout.activity_select_delivery_agent)
public class SelectDeliveryAgentActivity extends BaseActivity implements DeliveryAgentAdapter.CardInteractionListener {

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
    SquareAppButton btn_no_data;

    @ViewById
    AppButton btn_accept;

    @ViewById
    ProgressBar btn_accept_progress;
    private DeliveryAgentAdapter mAdapter;

    @ViewById
    FrameLayout fl_button;
    private String serviceTypeId;


    @AfterViews
    public void setUpView() {
        setUpData();
        setUpToolbar();
        setUpListeners();

        if (getIntent().hasExtra(Constants.ORDER_TYPE_SERVICE)) {
            iv_skip.setVisibility(View.GONE);
        }
        makeDeliveryBoyFetchApiCall();
    }

    private void setUpData() {
        tv_no_data.setText(getString(R.string.no_delivery_boys));
        btn_no_data.setVisibility(View.VISIBLE);
        btn_no_data.setText(getString(R.string.skip));
    }

    private void makeDeliveryBoyFetchApiCall() {

        fl_button.setVisibility(View.GONE);
        rv_delivery_agents.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        no_data_layout.setVisibility(View.GONE);

        if (getIntent().hasExtra(Constants.ORDER_TYPE_SERVICE)) {
            serviceTypeId = getIntent().getStringExtra(Constants.ORDER_TYPE_SERVICE);

            new RetrofitHelper(this).fetchDeliveryBoysForSeller(new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {
                    handleResponse(response);
                    sl_pull_to_refresh.setRefreshing(false);
                }

                @Override
                public void onErrorResponse() {
                    showSnackBar(getString(R.string.something_went_wrong));
                    fl_button.setVisibility(View.GONE);
                    rv_delivery_agents.setVisibility(View.GONE);
                    shimmer_view_container.setVisibility(View.GONE);
                    no_data_layout.setVisibility(View.VISIBLE);

                    sl_pull_to_refresh.setRefreshing(false);
                }
            }, serviceTypeId);
        } else {

            new RetrofitHelper(this).fetchDeliveryBoysForSeller(new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {
                    handleResponse(response);
                    sl_pull_to_refresh.setRefreshing(false);
                }

                @Override
                public void onErrorResponse() {
                    showSnackBar(getString(R.string.something_went_wrong));
                    fl_button.setVisibility(View.GONE);
                    rv_delivery_agents.setVisibility(View.GONE);
                    shimmer_view_container.setVisibility(View.GONE);
                    no_data_layout.setVisibility(View.VISIBLE);

                    sl_pull_to_refresh.setRefreshing(false);
                }
            });
        }
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("status")) {
                if (jsonObject.optJSONArray("result") != null) {
                    JSONArray userArray = jsonObject.getJSONArray("result");
                    Type classType = new TypeToken<ArrayList<DeliveryModel>>() {
                    }.getType();

                    ArrayList<DeliveryModel> deliveryList = new Gson().fromJson(userArray.toString(), classType);
                    if (deliveryList != null && deliveryList.size() > 0) {
                        showInView(deliveryList);
                    } else {
                        fl_button.setVisibility(View.GONE);
                        rv_delivery_agents.setVisibility(View.GONE);
                        shimmer_view_container.setVisibility(View.GONE);
                        no_data_layout.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                fl_button.setVisibility(View.GONE);
                rv_delivery_agents.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);

                showSnackBar(getString(R.string.something_went_wrong));
            }
        } catch (JSONException e) {
            fl_button.setVisibility(View.GONE);
            rv_delivery_agents.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);
            no_data_layout.setVisibility(View.VISIBLE);

            showSnackBar(getString(R.string.something_went_wrong));
        }

    }

    private void showInView(ArrayList<DeliveryModel> deliveryList) {
        rv_delivery_agents.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_delivery_agents.setLayoutManager(llm);

        if (getIntent().hasExtra(Constants.ORDER_TYPE_SERVICE))
            mAdapter = new DeliveryAgentAdapter(deliveryList, true, this, serviceTypeId);
        else
            mAdapter = new DeliveryAgentAdapter(deliveryList, true, this);
        rv_delivery_agents.setAdapter(mAdapter);

        fl_button.setVisibility(View.VISIBLE);
        rv_delivery_agents.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);
    }

    private void setUpListeners() {

        btn_no_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        iv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        sl_pull_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeDeliveryBoyFetchApiCall();
            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<DeliveryModel> list = mAdapter.getUpdatedList();
                String deliveryBoySelected = "";
                for (DeliveryModel model : list) {
                    if (model.isSelected())
                        deliveryBoySelected = model.getDeliveryBoyId();
                }

                if (!Utility.isEmpty(deliveryBoySelected)) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DELIVERY_AGENT_ID, deliveryBoySelected);
                    setResult(RESULT_OK, intent);
                    finish();
                } else
                    showSnackBar(getString(R.string.please_select_boy));
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.assign_delivery_boy));

        iv_skip.setVisibility(View.VISIBLE);
    }


    @Override
    public void onShowReviews(int position) {

    }

    @Override
    public void onDeleteAgent(int position) {

    }

    @Override
    public void onEditAgent(int position) {

    }
}
