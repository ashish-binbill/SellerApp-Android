package com.binbill.seller.Wallet;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Dashboard.ProfileModel;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.binbill.seller.Verification.VerificationAdapter;
import com.binbill.seller.Verification.VerificationModel;
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
    TextView tv_wallet, tv_no_data;

    @ViewById
    RecyclerView rv_wallet_txn;

    @ViewById
    AppButton btn_no_data;

    @ViewById
    LinearLayout shimmer_view_container, no_data_layout;
    private ArrayList<WalletTransaction> walletTransactionList;

    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        setUpData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchWalletTransactions();
    }

    private void fetchWalletTransactions() {

        new RetrofitHelper(this).getWalletTransactions(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<WalletTransaction>>() {
                            }.getType();

                            walletTransactionList = new Gson().fromJson(userArray.toString(), classType);
                            handleResponse();
                        }
                    }
                }catch (JSONException e){

                }
            }

            @Override
            public void onErrorResponse() {

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
//        WalletAdapter mAdapter = new WalletAdapter(walletTransactionList, this);
//        rv_wallet_txn.setAdapter(mAdapter);

        rv_wallet_txn.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);
    }

    private void setUpData() {

        ProfileModel profileModel = AppSession.getInstance(this).getSellerProfile();
        if (!Utility.isEmpty(profileModel.getCashBack()))
            tv_wallet.setText(profileModel.getCashBack());
        else
            tv_wallet.setText("0");

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.my_wallet));
    }

}
