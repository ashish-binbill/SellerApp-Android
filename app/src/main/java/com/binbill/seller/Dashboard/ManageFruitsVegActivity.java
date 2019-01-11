package com.binbill.seller.Dashboard;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.Adapter.ManageFruitVegAdapter;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.Model.FruitsVeg;
import com.binbill.seller.Model.SkuItem;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.Order.Order;
import com.binbill.seller.Order.OrderAdapter;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

@EActivity(R.layout.activity_manage_fruits_veg)
public class ManageFruitsVegActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById(R.id.btn_no_data)
    SquareAppButton btnNoData;

    @ViewById(R.id.sl_pull_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @ViewById
    RecyclerView rv_fruits_veg_list;

    @ViewById
    LinearLayout shimmer_view_container,no_data_layout;

    @ViewById
    TextView tv_no_data;

    @ViewById(R.id.btn_add_data)
    SquareAppButton btnSave;

    UserRegistrationDetails userRegistrationDetails;
    String title;
    String toolbar_text1 = "";
    ManageFruitVegAdapter mAdapter;

    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        getIntentData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                rv_fruits_veg_list.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.VISIBLE);
                no_data_layout.setVisibility(View.GONE);
                makeAPICall();

            }
        });
    }

    private void setUpToolbar() {
        toolbar_text1 = getIntent().getStringExtra("Title");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbarText.setText(toolbar_text1);
    }

    private void getIntentData() {
        title = getIntent().getStringExtra("Id");
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        btnNoData.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        btnSave.setText("Save");
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeAPICall();
    }

    private void makeAPICall() {
        rv_fruits_veg_list.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        no_data_layout.setVisibility(View.GONE);
        int categoryId = Integer.parseInt(title);
        new RetrofitHelper(this).fetchFruitsVeg(userRegistrationDetails.getFruitsCategoryId(),
                categoryId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        if (jsonObject.optJSONObject("result") != null) {
                            JSONObject jObject = jsonObject.getJSONObject("result");
                            JSONArray skuArray = jObject.getJSONArray("sku_items");
                            Type classType = new TypeToken<ArrayList<SkuItem>>() {
                            }.getType();

                            ArrayList<SkuItem> skuList = new Gson().fromJson(skuArray.toString(), classType);
                            AppSession.getInstance(ManageFruitsVegActivity.this).setSkuItemList(skuList);
                            handleResponse();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse() {
                shimmer_view_container.setVisibility(View.GONE);
                rv_fruits_veg_list.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void handleResponse() {
        swipeRefreshLayout.setRefreshing(false);
        ArrayList<SkuItem> skuList = AppSession.getInstance(ManageFruitsVegActivity.this).getSkuItemList();
        if (skuList != null && skuList.size() > 0) {
            setUpData(skuList);
        } else {
            rv_fruits_veg_list.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);
            no_data_layout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setUpData(ArrayList<SkuItem> list) {
        rv_fruits_veg_list.setHasFixedSize(true);
        rv_fruits_veg_list.setLayoutManager(new LinearLayoutManager(this));
        rv_fruits_veg_list.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);
        mAdapter = new ManageFruitVegAdapter(list, this);
        rv_fruits_veg_list.setAdapter(mAdapter);
    }
}
