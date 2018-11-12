package com.binbill.seller.Wallet;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Dashboard.ProfileModel;
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

@EActivity(R.layout.activity_wallet)
public class WalletActivity extends BaseActivity {
    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    TextView tv_wallet, tv_no_data, tv_wallet_balance, tv_mobile;

    @ViewById
    RecyclerView rv_wallet_txn;

    @ViewById
    AppButton btn_no_data, btn_redeem, btn_confirm;

    @ViewById
    LinearLayout shimmer_view_container, no_data_layout, ll_wallet_layout;

    @ViewById
    RelativeLayout rl_confirm_layout;

    @ViewById
    ProgressBar progress;

    @ViewById
    SwipeRefreshLayout sl_pull_to_refresh;
    private ArrayList<WalletTransaction> walletTransactionList;
    private String walletAmount;

    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        setUpData();
        setUpListeners();

        btn_no_data.setVisibility(View.GONE);
        tv_no_data.setText(getString(R.string.no_wallet_txn));
    }

    private void setUpListeners() {
        sl_pull_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchWalletTransactions();
            }
        });

        btn_redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.isEmpty(walletAmount) && !walletAmount.equalsIgnoreCase("null")) {
                    float amountFloat = Float.parseFloat(walletAmount);
                    if (amountFloat > 0) {
                        tv_wallet_balance.setText(" " + tv_wallet.getText().toString());

                        String mobile = AppSession.getInstance(WalletActivity.this).getMobile();
                        String confirmNumber = getString(R.string.confirm_paytm, mobile);
                        SpannableString ss1 = new SpannableString(confirmNumber);
                        ss1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),confirmNumber.indexOf("-") + 2, confirmNumber.indexOf("-") + 2 + mobile.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss1.setSpan(new RelativeSizeSpan(1.3f), confirmNumber.indexOf("-") + 2, confirmNumber.indexOf("-") + 2 + mobile.length(), 0);

                        tv_mobile.setText(ss1);

                        setUpToolbar(true);

                        ll_wallet_layout.setVisibility(View.GONE);
                        rl_confirm_layout.setVisibility(View.VISIBLE);
                    } else
                        showSnackBar(getString(R.string.insufficient_wallet_amount));
                }
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_confirm.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);

                new RetrofitHelper(WalletActivity.this).redeemWalletAmount(new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optBoolean("status")) {
                                invokeSuccessDialog();
                            } else {
                                showSnackBar(getString(R.string.unable_to_redeem));
                            }
                        } catch (JSONException e) {
                            showSnackBar(getString(R.string.unable_to_redeem));
                        }
                        btn_confirm.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onErrorResponse() {
                        btn_confirm.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        showSnackBar(getString(R.string.unable_to_redeem));
                    }
                });
            }
        });
    }

    public void invokeSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_assisted_service, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fetchWalletTransactions();

        TextView titleText = (TextView) dialogView.findViewById(R.id.title);
        titleText.setText(getString(R.string.redeem_successfully));
        AppButton yesButton = (AppButton) dialogView.findViewById(R.id.btn_yes);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                rl_confirm_layout.setVisibility(View.GONE);
                ll_wallet_layout.setVisibility(View.VISIBLE);
                setUpToolbar();
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchWalletTransactions();
    }

    private void fetchWalletTransactions() {
        no_data_layout.setVisibility(View.GONE);
        rv_wallet_txn.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        new RetrofitHelper(this).getWalletTransactions(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        walletAmount = jsonObject.optString("total_cashback");
                        setUpData(walletAmount);
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");

                            if (userArray != null && userArray.length() > 0) {
                                Type classType = new TypeToken<ArrayList<WalletTransaction>>() {
                                }.getType();

                                walletTransactionList = new Gson().fromJson(userArray.toString(), classType);
                                handleResponse();

                                no_data_layout.setVisibility(View.GONE);
                                rv_wallet_txn.setVisibility(View.VISIBLE);
                                shimmer_view_container.setVisibility(View.GONE);
                            } else {
                                no_data_layout.setVisibility(View.VISIBLE);
                                rv_wallet_txn.setVisibility(View.GONE);
                                shimmer_view_container.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (JSONException e) {

                    no_data_layout.setVisibility(View.VISIBLE);
                    rv_wallet_txn.setVisibility(View.GONE);
                    shimmer_view_container.setVisibility(View.GONE);
                }

                sl_pull_to_refresh.setRefreshing(false);
            }

            @Override
            public void onErrorResponse() {
                no_data_layout.setVisibility(View.VISIBLE);
                rv_wallet_txn.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.GONE);

                showSnackBar(getString(R.string.unable_to_redeem));

                sl_pull_to_refresh.setRefreshing(false);
            }
        });

    }

    private void handleResponse() {
        if (walletTransactionList != null && walletTransactionList.size() > 0) {
            setUpListData();
        } else {
            rv_wallet_txn.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);
            no_data_layout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpListData() {
        rv_wallet_txn.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_wallet_txn.setLayoutManager(llm);
        WalletAdapter mAdapter = new WalletAdapter(walletTransactionList);
        rv_wallet_txn.setAdapter(mAdapter);

        rv_wallet_txn.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);
    }

    private void setUpData(String walletAmount) {

        if (Utility.isEmpty(walletAmount)) {
            ProfileModel profileModel = AppSession.getInstance(this).getSellerProfile();
            if (!Utility.isEmpty(profileModel.getCashBack()))
                tv_wallet.setText(getString(R.string.rupee_sign) + " " + String.format("%.2f", Float.parseFloat(profileModel.getCashBack())));
            else
                tv_wallet.setText(getString(R.string.rupee_sign) + " 0");
        } else
            tv_wallet.setText(getString(R.string.rupee_sign) + " " + String.format("%.2f", Float.parseFloat(walletAmount)));
    }

    private void setUpData() {
        setUpData("");
    }

    private void setUpToolbar(boolean isConfirm) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        if (isConfirm)
            toolbarText.setText(getString(R.string.confirm_number));
        else
            toolbarText.setText(getString(R.string.my_wallet));
    }

    private void setUpToolbar() {
        setUpToolbar(false);
    }
}
