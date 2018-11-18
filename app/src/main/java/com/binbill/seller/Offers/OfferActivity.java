package com.binbill.seller.Offers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.YesNoDialogFragment;
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

@EActivity(R.layout.activity_offer)
public class OfferActivity extends BaseActivity implements YesNoDialogFragment.YesNoClickInterface, OfferAdapter.OfferManipulationListener {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ImageView iv_notification;

    @ViewById
    RecyclerView rv_offers;

    @ViewById
    LinearLayout shimmer_view_container;

    @ViewById
    LinearLayout no_data_layout;

    @ViewById
    TextView tv_no_data;

    @ViewById
    AppButton btn_no_data;
    private ArrayList<OfferItem> mOfferList;
    private String mOfferIdToDelete;

    @AfterViews
    public void setUpView() {
        setUpToolbar();
        setUpListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeOfferFetchApiCall();
    }

    private void invokeAddOfferOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_offer_type, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final Dialog offerDialog = builder.create();
        offerDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        offerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout normalOffer = (RelativeLayout) dialogView.findViewById(R.id.rl_normal_offer);
        RelativeLayout barcodeOffer = (RelativeLayout) dialogView.findViewById(R.id.rl_barcode_offer);

        normalOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfferActivity.this, AddOfferActivity_.class);
                startActivity(intent);

                offerDialog.dismiss();
            }
        });

        barcodeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfferActivity.this, AddBarCodeOfferActivity_.class);
                startActivity(intent);

                offerDialog.dismiss();
            }
        });

        TextView headerTitle = (TextView) dialogView.findViewById(R.id.header);
        headerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offerDialog.dismiss();
            }
        });

        offerDialog.show();
    }

    private void setUpListeners() {
        btn_no_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invokeAddOfferOptions();
            }
        });

        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invokeAddOfferOptions();
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.my_offers));

        iv_notification.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_offer));
        iv_notification.setVisibility(View.VISIBLE);
    }

    private void makeOfferFetchApiCall() {

        shimmer_view_container.setVisibility(View.VISIBLE);
        rv_offers.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);


        String sellerId = AppSession.getInstance(this).getSellerId();
        new RetrofitHelper(this).fetchOffersForSeller(sellerId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response);
            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));
                showNoOfferLayout();
            }
        });
    }

    private void handleResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("status")) {

                if (jsonObject.optJSONArray("result") != null) {
                    JSONArray offerArray = jsonObject.getJSONArray("result");
                    if (offerArray.length() > 0) {
                        setUpOffersInList(offerArray);
                    } else
                        showNoOfferLayout();
                }
            } else {
                showSnackBar(getString(R.string.something_went_wrong));
                showNoOfferLayout();
            }
        } catch (JSONException e) {
            showSnackBar(getString(R.string.something_went_wrong));
            showNoOfferLayout();
        }
    }

    private void setUpOffersInList(JSONArray offerArray) {
        /**
         * setup recycler view
         */
        Type classType = new TypeToken<ArrayList<OfferItem>>() {
        }.getType();

        mOfferList = new Gson().fromJson(offerArray.toString(), classType);
        rv_offers.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_offers.setLayoutManager(llm);
        OfferAdapter mAdapter = new OfferAdapter(this, mOfferList);
        rv_offers.setAdapter(mAdapter);

        rv_offers.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);
    }

    private void showNoOfferLayout() {
        tv_no_data.setText(getString(R.string.no_offers_added));
        btn_no_data.setText(getString(R.string.add_offers));
        no_data_layout.setVisibility(View.VISIBLE);
        rv_offers.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.GONE);
    }

    @Override
    public void onOfferManupulation(int position, String type) {
        switch (type) {
            case Constants.ADD_USER_FOR_OFFER:
                Intent addIntent = new Intent(OfferActivity.this, PublishOfferToUserActivity_.class);
                addIntent.putExtra(Constants.OFFER_ITEM, mOfferList.get(position));
                addIntent.putExtra(Constants.FLOW_TYPE, Constants.SHOW_LINKED_USERS);
                startActivity(addIntent);
                break;
            case Constants.EDIT_OFFER:
                Intent intent = new Intent(OfferActivity.this, AddOfferActivity_.class);
                intent.putExtra(Constants.OFFER_ITEM, mOfferList.get(position));
                startActivity(intent);
                break;
            case Constants.DELETE_OFFER:

                mOfferIdToDelete = mOfferList.get(position).getOfferId();
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                YesNoDialogFragment fragment = YesNoDialogFragment.newInstance(getString(R.string.delete_offer), "Delete Offer");
                fragment.show(fm, "YesNoDialogFragment");
                break;
        }
    }

    @Override
    public void onOptionSelected(boolean isProceed) {
        if (isProceed) {
            makeDeleteOfferApiCall();
        } else {
/**
 * do nothing
 */
        }
    }

    private void makeDeleteOfferApiCall() {

        new RetrofitHelper(this).deleteOfferForSeller(mOfferIdToDelete, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                makeOfferFetchApiCall();
            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }
}
