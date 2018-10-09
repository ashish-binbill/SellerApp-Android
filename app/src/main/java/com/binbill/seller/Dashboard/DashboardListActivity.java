package com.binbill.seller.Dashboard;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.CreditLoyaltyDashboardModel;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
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

@EActivity(R.layout.activity_dashboard_list)
public class DashboardListActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    SwipeRefreshLayout sl_pull_to_refresh;

    @ViewById
    LinearLayout shimmer_view_container, no_data_layout;

    @ViewById
    TextView tv_no_data;

    @ViewById
    RecyclerView rv_credits_view;

    @ViewById
    ImageView iv_no_data_image;

    @ViewById
    AppButton btn_no_data;
    private int mType;

    @AfterViews
    public void initiateViews() {
        mType = getIntent().getIntExtra(Constants.TYPE, 1);

        setUpToolbar();
        setUpListeners();
        btn_no_data.setVisibility(View.GONE);

        switch (mType) {
            case Constants.CREDIT_PENDING:
                iv_no_data_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_no_credit));
                break;
            case Constants.POINTS:
                iv_no_data_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_no_points));
                break;
            case Constants.TRANSACTIONS:
                iv_no_data_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_no_txn));
                break;
            default:
                iv_no_data_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_no_cashback));
        }
        tv_no_data.setText(getString(R.string.no_credit));

    }

    private void setUpListeners() {
        sl_pull_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                rv_credits_view.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.VISIBLE);
                no_data_layout.setVisibility(View.GONE);

                if (mType == Constants.CREDIT_PENDING)
                    makeCreditHistoryCall();
                else if (mType == Constants.POINTS)
                    makePointsHistoryCall();
                else if (mType == Constants.TRANSACTIONS)
                    makeTransactionHistoryCall();
                else
                    makeCashbackHistoryCall();

                sl_pull_to_refresh.setRefreshing(false);
            }
        });
    }

    private void makePointsHistoryCall() {
        rv_credits_view.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        no_data_layout.setVisibility(View.GONE);

        new RetrofitHelper(this).getSellerPoints(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        JSONArray userArray = jsonObject.getJSONArray("result");
                        Type classType = new TypeToken<ArrayList<CreditLoyaltyDashboardModel>>() {
                        }.getType();

                        ArrayList<CreditLoyaltyDashboardModel> creditList = new Gson().fromJson(userArray.toString(), classType);
                        handleResponse(creditList);

                    } else {
                        rv_credits_view.setVisibility(View.GONE);
                        shimmer_view_container.setVisibility(View.GONE);
                        no_data_layout.setVisibility(View.VISIBLE);

                        showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    rv_credits_view.setVisibility(View.GONE);
                    shimmer_view_container.setVisibility(View.GONE);
                    no_data_layout.setVisibility(View.VISIBLE);
                    showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                rv_credits_view.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);
                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void makeTransactionHistoryCall() {
        rv_credits_view.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        no_data_layout.setVisibility(View.GONE);

        new RetrofitHelper(this).getSellerTransactions(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        JSONArray userArray = jsonObject.getJSONArray("result");
                        Type classType = new TypeToken<ArrayList<CreditLoyaltyDashboardModel>>() {
                        }.getType();

                        ArrayList<CreditLoyaltyDashboardModel> creditList = new Gson().fromJson(userArray.toString(), classType);
                        handleResponse(creditList);

                    } else {
                        rv_credits_view.setVisibility(View.GONE);
                        shimmer_view_container.setVisibility(View.GONE);
                        no_data_layout.setVisibility(View.VISIBLE);

                        showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    rv_credits_view.setVisibility(View.GONE);
                    shimmer_view_container.setVisibility(View.GONE);
                    no_data_layout.setVisibility(View.VISIBLE);
                    showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                rv_credits_view.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);
                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void makeCashbackHistoryCall() {
        rv_credits_view.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        no_data_layout.setVisibility(View.GONE);

        new RetrofitHelper(this).getSellerCashbacks(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        JSONArray userArray = jsonObject.getJSONArray("result");
                        Type classType = new TypeToken<ArrayList<CreditLoyaltyDashboardModel>>() {
                        }.getType();

                        ArrayList<CreditLoyaltyDashboardModel> creditList = new Gson().fromJson(userArray.toString(), classType);
                        handleResponse(creditList);

                    } else {
                        rv_credits_view.setVisibility(View.GONE);
                        shimmer_view_container.setVisibility(View.GONE);
                        no_data_layout.setVisibility(View.VISIBLE);

                        showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    rv_credits_view.setVisibility(View.GONE);
                    shimmer_view_container.setVisibility(View.GONE);
                    no_data_layout.setVisibility(View.VISIBLE);
                    showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                rv_credits_view.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);
                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mType == Constants.CREDIT_PENDING)
            makeCreditHistoryCall();
        else if (mType == Constants.POINTS)
            makePointsHistoryCall();
        else if (mType == Constants.TRANSACTIONS)
            makeTransactionHistoryCall();
        else
            makeCashbackHistoryCall();
    }

    private void makeCreditHistoryCall() {

        rv_credits_view.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        no_data_layout.setVisibility(View.GONE);

        new RetrofitHelper(this).getSellerCredits(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        JSONArray userArray = jsonObject.getJSONArray("result");
                        Type classType = new TypeToken<ArrayList<CreditLoyaltyDashboardModel>>() {
                        }.getType();

                        ArrayList<CreditLoyaltyDashboardModel> creditList = new Gson().fromJson(userArray.toString(), classType);
                        handleResponse(creditList);

                    } else {
                        rv_credits_view.setVisibility(View.GONE);
                        shimmer_view_container.setVisibility(View.GONE);
                        no_data_layout.setVisibility(View.VISIBLE);

                        showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    rv_credits_view.setVisibility(View.GONE);
                    shimmer_view_container.setVisibility(View.GONE);
                    no_data_layout.setVisibility(View.VISIBLE);
                    showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                rv_credits_view.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);
                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void handleResponse(ArrayList<CreditLoyaltyDashboardModel> list) {

        if (list != null && list.size() > 0) {
            setUpData(list);
        } else {
            rv_credits_view.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);
            no_data_layout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpData(ArrayList<CreditLoyaltyDashboardModel> list) {

        rv_credits_view.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_credits_view.setLayoutManager(llm);

        /**
         *  1 ---- credit
         *  2 ---- points
         *  3 ----- transactions
         *  4 ------ cashbacks
         */
        CreditPointsDashboardAdapter mAdapter = new CreditPointsDashboardAdapter(mType, list);
        rv_credits_view.setAdapter(mAdapter);

        rv_credits_view.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);
    }


    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        if (mType == Constants.CREDIT_PENDING)
            toolbarText.setText(getString(R.string.credit_pending));
        else if (mType == Constants.POINTS)
            toolbarText.setText(getString(R.string.loyalty_discounts));
        else if (mType == Constants.TRANSACTIONS)
            toolbarText.setText(getString(R.string.transactions_report));
        else
            toolbarText.setText(getString(R.string.cashback_to_users));
    }

}
