package com.binbill.seller.Customer;

import android.content.Context;
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

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.R;
import com.binbill.seller.Registration.ImagePreviewActivity_;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/4/18.
 */

public class TransactionFragment extends Fragment implements TransactionAdapter.TransactionCardInterface {

    private RecyclerView txnListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserModel userModel;
    private TextView totalTransaction;
    private ArrayList<TransactionModel> txnList;


    public TransactionFragment() {
    }

    public static TransactionFragment newInstance(UserModel userModel) {
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_MODEL, userModel);
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
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalTransaction = (TextView) view.findViewById(R.id.tv_total_txn);
        txnListView = (RecyclerView) view.findViewById(R.id.rv_txn_view);

        shimmerview = (LinearLayout) view.findViewById(R.id.shimmer_view_container);
        noDataLayout = (LinearLayout) view.findViewById(R.id.no_data_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sl_pull_to_refresh);

        TextView noDataTitle = (TextView) noDataLayout.findViewById(R.id.tv_no_data);
        noDataTitle.setText(getString(R.string.no_txn));
        SquareAppButton noDataButton = (SquareAppButton) noDataLayout.findViewById(R.id.btn_no_data);
        noDataButton.setVisibility(View.GONE);
        userModel = (UserModel) getArguments().getSerializable(Constants.USER_MODEL);

        if (userModel != null) {
            makeTransactionHistoryCall();
        }

        setUpListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void makeTransactionHistoryCall() {

        txnListView.setVisibility(View.GONE);
        shimmerview.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.GONE);

        new RetrofitHelper(getActivity()).getUserTransactions(userModel.getUserId(), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        String total = jsonObject.optString("total_transactions");
                        totalTransaction.setText(getString(R.string.rupee_sign) + total);

                        JSONArray userArray = jsonObject.getJSONArray("result");
                        Type classType = new TypeToken<ArrayList<TransactionModel>>() {
                        }.getType();

                        ArrayList<TransactionModel> txnList = new Gson().fromJson(userArray.toString(), classType);
                        handleResponse(txnList);

                    } else {
                        txnListView.setVisibility(View.GONE);
                        shimmerview.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);

                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    txnListView.setVisibility(View.GONE);
                    shimmerview.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                txnListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void setUpListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                txnListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);

                makeTransactionHistoryCall();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void handleResponse(ArrayList<TransactionModel> list) {
        if (list != null && list.size() > 0) {
            setUpData(list);
        } else {
            txnListView.setVisibility(View.GONE);
            shimmerview.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpData(ArrayList<TransactionModel> list) {

        this.txnList = list;
        txnListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        txnListView.setLayoutManager(llm);
        TransactionAdapter mAdapter = new TransactionAdapter(list, this);
        txnListView.setAdapter(mAdapter);

        txnListView.setVisibility(View.VISIBLE);
        shimmerview.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewBill(int pos) {
        TransactionModel transactionModel = txnList.get(pos);

        if (transactionModel.getJobCopies() != null && transactionModel.getJobCopies().size() > 0) {
            Intent intent = new Intent(getActivity(), ImagePreviewActivity_.class);
            intent.putExtra(Constants.FILE_URI, transactionModel.getJobCopies());
            intent.putExtra(Constants.IMAGE_TYPE, Constants.TYPE_URL);
            startActivity(intent);
        }else
            ((BaseActivity) getActivity()).showSnackBar(getString(R.string.no_copies));
    }
}

