package com.binbill.seller.Order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.BinBillSeller;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Customer.AddCustomerActivity_;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * Created by shruti.vig on 9/5/18.
 */

public class ActiveOrderFragment extends Fragment implements OrderAdapter.OrderSelectedInterface {

    private RecyclerView orderListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Order> mOrderList;
    private OrderAdapter mAdapter;


    public ActiveOrderFragment() {
    }

    public static ActiveOrderFragment newInstance() {
        ActiveOrderFragment fragment = new ActiveOrderFragment();
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
        fetchOrders();
//        connectSocket();
    }

    private void connectSocket() {
        BinBillSeller.getSocket(getActivity()).connect();
        BinBillSeller.getSocket(getActivity()).on("order-placed", SOCKET_EVENT_ORDER_PLACED);
        BinBillSeller.getSocket(getActivity()).on("order-status-change", SOCKET_EVENT_ORDER_STATUS_CHANGED);
        BinBillSeller.getSocket(getActivity()).on("assisted-status-change", SOCKET_ASSISTED_ORDER_STATUS_CHANGED);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private Emitter.Listener SOCKET_EVENT_ORDER_PLACED = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            fetchOrders();
        }
    };

    private Emitter.Listener SOCKET_EVENT_ORDER_STATUS_CHANGED = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            fetchOrders();
        }
    };

    private Emitter.Listener SOCKET_ASSISTED_ORDER_STATUS_CHANGED = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            fetchOrders();
        }
    };

    @Override
    public void setMenuVisibility(final boolean visible) {
        if (visible) {
            connectSocket();
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
        noDataText.setText(getString(R.string.no_active_orders));

        AppButton noDataButton = (AppButton) noDataLayout.findViewById(R.id.btn_no_data);
        noDataButton.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sl_pull_to_refresh);

        setUpListeners();
        handleResponse();
    }

    private void setUpListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                orderListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);
                fetchOrders();

            }
        });

        AppButton addCustomer = noDataLayout.findViewById(R.id.btn_no_data);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddCustomerActivity_.class));
            }
        });
    }

    private void fetchOrders() {
        ApiHelper.fetchOrders(getActivity(), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<Order>>() {
                            }.getType();

                            ArrayList<Order> userList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(getActivity()).setOrderList(userList);
                            handleResponse();
                        }
                    } else {
                        orderListView.setVisibility(View.GONE);
                        shimmerview.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);

                        if (isAdded())
                            ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }
                } catch (JSONException e) {
                    orderListView.setVisibility(View.GONE);
                    shimmerview.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);

                    if (isAdded())
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onErrorResponse() {

                orderListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);

                if (isAdded())
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void handleResponse() {
        ArrayList<Order> orderList = AppSession.getInstance(getActivity()).getOrderList();
        if (orderList != null && orderList.size() > 0) {
            setUpData(orderList);
        } else {
            orderListView.setVisibility(View.GONE);
            shimmerview.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpData(ArrayList<Order> list) {

        this.mOrderList = list;
        orderListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        orderListView.setLayoutManager(llm);
        mAdapter = new OrderAdapter(mOrderList, this);
        orderListView.setAdapter(mAdapter);

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

        Intent intent = new Intent(getActivity(), OrderDetailsActivity_.class);
        intent.putExtra(Constants.ORDER_ID, order.getOrderId());
        startActivity(intent);

    }
}

