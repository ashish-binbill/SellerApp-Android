package com.binbill.seller.DeliveryAgent;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.AssistedService.AddAssistedServiceActivity_;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.ReviewsDialogFragment;
import com.binbill.seller.CustomViews.YesNoDialogFragment;
import com.binbill.seller.Order.DeliveryAgentAdapter;
import com.binbill.seller.Order.DeliveryModel;
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

@EActivity(R.layout.activity_delivery_agent)
public class DeliveryAgentActivity extends BaseActivity implements DeliveryAgentAdapter.CardInteractionListener, YesNoDialogFragment.YesNoClickInterface {
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
    TextView tv_no_data;

    @ViewById
    SwipeRefreshLayout sl_pull_to_refresh;

    @ViewById
    AppButton btn_no_data;

    @ViewById
    ImageView iv_notification;

    private DeliveryAgentAdapter mAdapter;
    private ArrayList<DeliveryModel> deliveryBoyList;
    private String mDeliveryAgentIdToDelete;

    @AfterViews
    public void setUpView() {
        setUpData();
        setUpToolbar();
        setUpListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeDeliveryBoyFetchApiCall();
    }

    private void setUpData() {
        tv_no_data.setText(getString(R.string.no_delivery_boys));
        btn_no_data.setVisibility(View.VISIBLE);
        btn_no_data.setText(getString(R.string.add_delivery_boy_now));
        //btn_no_data.setText(getString(R.string.add));
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
                rv_delivery_agents.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);

                sl_pull_to_refresh.setRefreshing(false);
            }
        });
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
                        rv_delivery_agents.setVisibility(View.GONE);
                        shimmer_view_container.setVisibility(View.GONE);
                        no_data_layout.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                rv_delivery_agents.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.GONE);
                no_data_layout.setVisibility(View.VISIBLE);

                showSnackBar(getString(R.string.something_went_wrong));
            }
        } catch (JSONException e) {
            rv_delivery_agents.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);
            no_data_layout.setVisibility(View.VISIBLE);

            showSnackBar(getString(R.string.something_went_wrong));
        }
    }

    private void showInView(ArrayList<DeliveryModel> deliveryList) {
        this.deliveryBoyList = deliveryList;
        rv_delivery_agents.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_delivery_agents.setLayoutManager(llm);
        mAdapter = new DeliveryAgentAdapter(deliveryList, false, this);
        rv_delivery_agents.setAdapter(mAdapter);

        rv_delivery_agents.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);
    }

    private void setUpListeners() {
        btn_no_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Add delivery boy
                 */
                startActivity(new Intent(DeliveryAgentActivity.this, AddNewDeliveryAgentActivity_.class));
            }
        });

        sl_pull_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeDeliveryBoyFetchApiCall();
            }
        });

        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeliveryAgentActivity.this, AddNewDeliveryAgentActivity_.class));
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.delivery_agents));

        iv_notification.setVisibility(View.VISIBLE);
        iv_notification.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_offer));
    }


    @Override
    public void onShowReviews(int position) {
        DeliveryModel deliveryModel = deliveryBoyList.get(position);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        ReviewsDialogFragment fragment = ReviewsDialogFragment.newInstance(getString(R.string.reviews_string), deliveryModel.getReviews());
        fragment.show(fm, "ReviewsDialogFragment");
    }

    @Override
    public void onDeleteAgent(int position) {
        mDeliveryAgentIdToDelete = deliveryBoyList.get(position).getDeliveryBoyId();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        YesNoDialogFragment fragment = YesNoDialogFragment.newInstance(getString(R.string.delete_agent), "Delete Agent");
        fragment.show(fm, "YesNoDialogFragment");
    }

    @Override
    public void onEditAgent(int position) {
        DeliveryModel deliveryModel = deliveryBoyList.get(position);
        Intent intent = new Intent(this, AddNewDeliveryAgentActivity_.class);
        intent.putExtra(Constants.EDIT_DELIVERY_BOY, deliveryModel);
        startActivity(intent);
    }

    @Override
    public void onOptionSelected(boolean isProceed) {
        if (isProceed) {
            makeDeleteAgentApiCall();
        } else {
            /**
             * do nothing
             */
        }
    }

    @Override
    public void onProceedOrder(boolean isApproval, boolean isProceed) {
        // do nothing
    }

    private void makeDeleteAgentApiCall() {
        new RetrofitHelper(this).deleteAssistedService(mDeliveryAgentIdToDelete, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                makeDeliveryBoyFetchApiCall();
            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }
}
