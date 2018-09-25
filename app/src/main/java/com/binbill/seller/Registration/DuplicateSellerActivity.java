package com.binbill.seller.Registration;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.SplashActivity;
import com.binbill.seller.UpgradeHelper;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

@EActivity(R.layout.activity_duplicate_seller)
public class DuplicateSellerActivity extends BaseActivity implements DuplicateShopAdapter.AlreadyAddedShopListener {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    TextView tv_header;

    @ViewById
    RecyclerView rv_shop_list;

    @ViewById
    FrameLayout fl_same_shop, fl_new_shop;

    @ViewById
    ProgressBar btn_new_progress, btn_next_progress;

    @ViewById
    AppButton btn_new, btn_next;
    private ArrayList<Seller> sellerList;
    private Seller sellerDetails;
    private UserRegistrationDetails userRegistrationDetails;


    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        setUpListeners();

        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        switch (getIntent().getStringExtra(Constants.DUPLICATE_ITEM)) {
            case Constants.GSTIN:
                tv_header.setText(getString(R.string.found_shop, Constants.GSTIN));
                break;
            case Constants.PAN:
                tv_header.setText(getString(R.string.found_shop, Constants.PAN));
                break;
            case Constants.NONE:
                tv_header.setText(getString(R.string.found_shop, Constants.GSTIN + "/" + Constants.PAN));
                break;

        }

        sellerList = (ArrayList<Seller>) getIntent().getSerializableExtra(Constants.SELLER_LIST);
        rv_shop_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_shop_list.setLayoutManager(llm);
        DuplicateShopAdapter mAdapter = new DuplicateShopAdapter(sellerList, this);
        rv_shop_list.setAdapter(mAdapter);
    }

    private void setUpListeners() {
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Create new call
                 */

                btn_new_progress.setVisibility(View.VISIBLE);
                btn_new.setVisibility(View.GONE);

                HashMap<String, String> map = new HashMap<>();

                String gstin = getIntent().getStringExtra("gstin");
                String pan = getIntent().getStringExtra("pan");
                String category = getIntent().getStringExtra("category_id");

                if (!Utility.isEmpty(gstin))
                    map.put("gstin", gstin);
                if (!Utility.isEmpty(pan))
                    map.put("pan", pan);
                if (!Utility.isEmpty(category))
                    map.put("category_id", category);


                new RetrofitHelper(DuplicateSellerActivity.this).createNewShop(map, new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optBoolean("status")) {

                                JSONObject sellerDetails = jsonObject.getJSONObject("seller_detail");
                                String sellerId = sellerDetails.getString("id");
                                userRegistrationDetails.setId(sellerId);
                                AppSession.getInstance(DuplicateSellerActivity.this).setUserRegistrationDetails(userRegistrationDetails);

                                SharedPref.putString(DuplicateSellerActivity.this, SharedPref.SELLER_ID, sellerId);

                                int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, 0);
                                Intent intent = RegistrationResolver.getNextIntent(DuplicateSellerActivity.this, registrationIndex);
                                startActivity(intent);
                            } else
                                showSnackBar(getString(R.string.something_went_wrong));
                        } catch (JSONException e) {
                            showSnackBar(getString(R.string.something_went_wrong));
                        }
                        btn_new_progress.setVisibility(View.GONE);
                        btn_new.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onErrorResponse() {
                        showSnackBar(getString(R.string.something_went_wrong));
                        btn_new_progress.setVisibility(View.GONE);
                        btn_new.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Link call
                 */
                btn_next_progress.setVisibility(View.VISIBLE);
                btn_next.setVisibility(View.GONE);

                HashMap<String, String> map = new HashMap<>();

                String gstin = getIntent().getStringExtra("gstin");
                String pan = getIntent().getStringExtra("pan");
                String category = getIntent().getStringExtra("category_id");

                if (!Utility.isEmpty(sellerDetails.getId()))
                    map.put("id", sellerDetails.getId());
                if (!Utility.isEmpty(gstin))
                    map.put("gstin", gstin);
                if (!Utility.isEmpty(pan))
                    map.put("pan", pan);
                if (!Utility.isEmpty(category))
                    map.put("category_id", category);

                new RetrofitHelper(DuplicateSellerActivity.this).useExistingShop(map, new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optBoolean("status")) {
                                String sellerId = sellerDetails.getId();
                                userRegistrationDetails.setId(sellerId);
                                AppSession.getInstance(DuplicateSellerActivity.this).setUserRegistrationDetails(userRegistrationDetails);

                                SharedPref.putString(DuplicateSellerActivity.this, SharedPref.SELLER_ID, sellerId);
                                makeDashboardCall();
                            } else
                                showSnackBar(getString(R.string.something_went_wrong));
                        } catch (JSONException e) {
                            showSnackBar(getString(R.string.something_went_wrong));
                        }
                        btn_next_progress.setVisibility(View.GONE);
                        btn_next.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onErrorResponse() {
                        btn_next_progress.setVisibility(View.GONE);
                        btn_next.setVisibility(View.VISIBLE);
                        showSnackBar(getString(R.string.something_went_wrong));
                    }
                });
            }
        });
    }

    private void makeDashboardCall() {
        btn_next_progress.setVisibility(View.VISIBLE);
        btn_next.setVisibility(View.GONE);

        ApiHelper.makeDashboardDataCall(this, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        Type classType = new TypeToken<DashboardModel>() {
                        }.getType();

                        DashboardModel dashboardModel = new Gson().fromJson(jsonObject.toString(), classType);
                        AppSession.getInstance(DuplicateSellerActivity.this).setDashboardData(dashboardModel);

                        Intent intent = RegistrationResolver.getNextIntent(DuplicateSellerActivity.this, 3);

                        if(dashboardModel.getForceUpdate() != null){
                            if (dashboardModel.getForceUpdate().equalsIgnoreCase("TRUE"))
                                UpgradeHelper.invokeUpdateDialog(DuplicateSellerActivity.this, true);
                            else if (dashboardModel.getForceUpdate().equalsIgnoreCase("FALSE"))
                                UpgradeHelper.invokeUpdateDialog(DuplicateSellerActivity.this, false);
                        }else {
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    finish();
                }
            }

            @Override
            public void onErrorResponse() {
                finish();
            }
        });


    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.register));
    }

    @Override
    public void onShopSelected(int pos) {

        sellerDetails = sellerList.get(pos);
        fl_same_shop.setVisibility(View.VISIBLE);
    }
}
