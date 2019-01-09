package com.binbill.seller.Order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.Customer.AddCustomerActivity_;
import com.binbill.seller.Model.SellerDeliveryModel;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NotRespondedOrderFragment extends Fragment implements OrderAdapter.OrderSelectedInterface {

    private RecyclerView orderListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Order> mOrderList;
    private OrderAdapter mAdapter;
    private ArrayList<Order> orderList;
    private int page = 0;
    private LinearLayoutManager llm;
    private boolean isOrderCall = false;
    private int lastPage = 0;
    private  double AmtBeforeDis;

    /**
     * api: past order with query status_type=2
     */

    public NotRespondedOrderFragment() {
    }

    public static NotRespondedOrderFragment newInstance() {
        NotRespondedOrderFragment fragment = new NotRespondedOrderFragment();
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
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 0;
        fetchOrders();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        if (visible) {
//            connectSocket();
        }

        super.setMenuVisibility(visible);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderListView = (RecyclerView) view.findViewById(R.id.rv_order_list);
        shimmerview = (LinearLayout) view.findViewById(R.id.shimmer_view_container);
        noDataLayout = (LinearLayout) view.findViewById(R.id.no_data_layout);
        TextView noDataText = (TextView) noDataLayout.findViewById(R.id.tv_no_data);
        noDataText.setText(getString(R.string.no_orders_not_responded));

        ImageView noDataImage = (ImageView) noDataLayout.findViewById(R.id.iv_no_data_image);
        noDataImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_data_smile));

        SquareAppButton noDataButton = (SquareAppButton) noDataLayout.findViewById(R.id.btn_no_data);
        noDataButton.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sl_pull_to_refresh);

        setUpListeners();
        handleResponse();
    }

    RecyclerView.OnScrollListener OnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = llm.getChildCount();
            int totalItemCount = llm.getItemCount();
            int firstVisibleItemPosition = llm.findFirstVisibleItemPosition();

            if (!isOrderCall && page < lastPage && (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount)
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= Constants.ORDER_PAGE_SIZE) {
                loadMoreItems();
                Log.d("SHRUTI", visibleItemCount + " " + totalItemCount + " " +
                        firstVisibleItemPosition);
            }
        }
    };

    private void loadMoreItems() {
        page = ++page;
        fetchOrders();
    }

    private void setUpListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                orderListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);
                page = 0;
                fetchOrders();

            }
        });

        SquareAppButton addCustomer = noDataLayout.findViewById(R.id.btn_no_data);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddCustomerActivity_.class));
            }
        });
    }

    private void fetchOrders() {
        isOrderCall = true;

        new RetrofitHelper(getActivity()).fetchNotRespondedOrders("2", new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                isOrderCall = false;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        if (jsonObject.has("last_page") && !jsonObject.isNull("last_page"))
                            lastPage = jsonObject.optInt("last_page");

                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<Order>>() {
                            }.getType();
                          /*  if(jsonObject.has("before_discount_amount")){
                                AmtBeforeDis = jsonObject.getDouble("before_discount_amount");
                            }else{
                                AmtBeforeDis =0.0;
                            }*/
                            orderList = new Gson().fromJson(userArray.toString(), classType);
                            handleResponse();
                        }
                    } else {
                        orderListView.setVisibility(View.GONE);
                        shimmerview.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);

                        if (isVisible() && isAdded())
                            ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }
                } catch (JSONException e) {
                    orderListView.setVisibility(View.GONE);
                    shimmerview.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);

                    if (isVisible() && isAdded())
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onErrorResponse() {

                orderListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);

                if (isVisible() && isAdded()) {
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }, page);
    }

    private void handleResponse() {
        if (orderList != null && orderList.size() > 0) {
            setUpData(orderList);
        } else {
            orderListView.setVisibility(View.GONE);
            shimmerview.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpData(ArrayList<Order> list) {

        if (this.mOrderList == null || page == 0)
            this.mOrderList = list;
        else
            this.mOrderList.addAll(list);
        orderListView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        orderListView.setLayoutManager(llm);
        mAdapter = new OrderAdapter(mOrderList, this);
        if (page != lastPage)
            mAdapter.setLoadMore(true);
        else
            mAdapter.setLoadMore(false);
        orderListView.setAdapter(mAdapter);
        orderListView.addOnScrollListener(OnScrollListener);

        if (list.size() > 0) {
            int pos = Constants.ORDER_PAGE_SIZE * page;
            if (pos >= 0 && pos < mAdapter.getItemCount()) {
                final int position = pos;
                orderListView.post(new Runnable() {
                    @Override
                    public void run() {
                        orderListView.scrollToPosition(position);
                    }
                });
            }
        }

        orderListView.setVisibility(View.VISIBLE);
        shimmerview.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onOrderSelected(int pos) {
        Order order = mOrderList.get(pos);
        ArrayList<SellerDeliveryModel> deliverCharges = mOrderList.get(pos).getSellerDeliveryRules();
        Intent intent = new Intent(getActivity(), OrderDetailsActivity_.class);
        intent.putExtra(Constants.ORDER_ID, order.getOrderId());
        intent.putExtra("SellerDeliveryCharges", deliverCharges);
        intent.putExtra("AmtBeforeDelivery", order.getAmountBeforeDiscount());
        OrderDetailsActivity.isCollectStore = order.isCollectAtStore();
        startActivity(intent);

    }
}


