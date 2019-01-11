package com.binbill.seller.Offers;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.CustomViews.YesNoDialogFragment;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NormalOfferFragment extends Fragment implements OfferAdapter.OfferManipulationListener, YesNoDialogFragment.YesNoClickInterface {

    private RecyclerView offerListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<OfferItem> mNormalOfferList;
    private String mOfferIdToDelete = "";
    private Button noDataButton;
    private int offerType = 1;

    private RelativeLayout loaderLayout;

    public NormalOfferFragment() {
    }

    public static NormalOfferFragment newInstance(int offerType) {
        NormalOfferFragment fragment = new NormalOfferFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.OFFER_TYPE, offerType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        offerType = getArguments().getInt(Constants.OFFER_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        makeOfferFetchApiCall();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        offerListView = (RecyclerView) view.findViewById(R.id.rv_order_list);
        shimmerview = (LinearLayout) view.findViewById(R.id.shimmer_view_container);
        noDataLayout = (LinearLayout) view.findViewById(R.id.no_data_layout);

        noDataButton = (SquareAppButton) noDataLayout.findViewById(R.id.btn_no_data);
        noDataButton.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sl_pull_to_refresh);
        loaderLayout = (RelativeLayout) view.findViewById(R.id.just_sec_layout);

        setUpListeners();
    }

    private void makeOfferFetchApiCall() {

        shimmerview.setVisibility(View.VISIBLE);
        offerListView.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);


        String sellerId = AppSession.getInstance(getActivity()).getSellerId();
        new RetrofitHelper(getActivity()).fetchSuggestedOffersForSeller(sellerId, offerType, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response);
            }

            @Override
            public void onErrorResponse() {
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                showNoOfferLayout();
            }
        });
    }

    private void handleResponse(String response) {
        swipeRefreshLayout.setRefreshing(false);
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
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                showNoOfferLayout();
            }
        } catch (JSONException e) {
            ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            showNoOfferLayout();
        }
    }

    private void setUpOffersInList(JSONArray offerArray) {
        /**
         * setup recycler view
         */
        Type classType = new TypeToken<ArrayList<OfferItem>>() {
        }.getType();

        mNormalOfferList = new Gson().fromJson(offerArray.toString(), classType);
        offerListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        offerListView.setLayoutManager(llm);
        OfferAdapter mAdapter = new OfferAdapter(this, mNormalOfferList, offerType);
        offerListView.setAdapter(mAdapter);

        offerListView.setVisibility(View.VISIBLE);
        shimmerview.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }

    private void showNoOfferLayout() {
        TextView noDataText = (TextView) noDataLayout.findViewById(R.id.tv_no_data);
        if(offerType == (Constants.OFFER_TYPE_DISCOUNTED)){
            noDataText.setText("These are Suggested Discount Offers that you can promote " +
                    "to your customers. Currently, there are no Suggested Discount Offers.");
        }else if(offerType == (Constants.OFFER_TYPE_BOGO)){
            noDataText.setText("These are Suggested BOGO Offers that you can promote to your " +
                    "customers. Currently, there are no Suggested BOGO Offers.");
        }else if(offerType == (Constants.OFFER_TYPE_EXTRA)){
            noDataText.setText("These are Suggested Extra Quantity Offers that you can promote to" +
                    " your customers. Currently, there are no Suggested Extra Quantity Offers.");
        }else if(offerType == (Constants.OFFER_TYPE_NEW_PRODUCT)){
            noDataText.setText("These are Suggested New Products that you can promote to" +
                    " your customers. Currently, there are no New Products.");
        }else if(offerType == (Constants.OFFER_TYPE_GENERAL)){
            noDataText.setText("These are Suggested General Offers that you can promote to" +
                    " your customers. Currently, there are no Suggested General Offers.");
        }
       // noDataText.setText(getString(R.string.no_offers));

        noDataButton = (Button) noDataLayout.findViewById(R.id.btn_no_data);
        noDataButton.setText(getString(R.string.add_offers));
        noDataButton.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.VISIBLE);

        offerListView.setVisibility(View.GONE);
        shimmerview.setVisibility(View.GONE);
    }

    private void setUpListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                offerListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);

                makeOfferFetchApiCall();
            }
        });

        noDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OfferActivity) getActivity()).invokeAddOfferOptions();
            }
        });
    }

    @Override
    public void onOfferManupulation(int position, String type) {
        switch (type) {
            case Constants.DELETE_OFFER:
                mOfferIdToDelete = mNormalOfferList.get(position).getOfferId();
                ((SuggestedOffersActivity) getActivity()).showYesNoDialog();
                break;
            case Constants.ADD_OFFER_TO_SELLER:
                mOfferIdToDelete = mNormalOfferList.get(position).getOfferId();

                OfferItem offerItem = mNormalOfferList.get(position);
                if (offerType != Constants.OFFER_TYPE_GENERAL) {
                    Intent intent = new Intent(getActivity(), AddBarCodeOfferActivity_.class);
                    intent.putExtra(Constants.OFFER_ITEM, offerItem);
                    intent.putExtra(Constants.OFFER_TYPE, offerType);
                    intent.putExtra("OfferType", offerType);
                    startActivity(intent);
                } else {
                    makeLinkOfferWithSellerApiCall();
                }
                break;
            case Constants.NEED_THIS_ITEM:
                mOfferIdToDelete = mNormalOfferList.get(position).getOfferId();
                makeNeedThisItemApiCall();
                break;
        }
    }

    private void makeNeedThisItemApiCall() {
        loaderLayout.setVisibility(View.VISIBLE);
        new RetrofitHelper(getActivity()).sellerNeedThisItem(offerType, mOfferIdToDelete, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                loaderLayout.setVisibility(View.GONE);
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.service_requested_successfully));
                makeOfferFetchApiCall();
            }

            @Override
            public void onErrorResponse() {
                loaderLayout.setVisibility(View.GONE);
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void makeLinkOfferWithSellerApiCall() {
        loaderLayout.setVisibility(View.VISIBLE);
        new RetrofitHelper(getActivity()).linkOfferWithSeller(offerType, mOfferIdToDelete, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                loaderLayout.setVisibility(View.GONE);
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.offer_successfully_added));
                makeOfferFetchApiCall();
            }

            @Override
            public void onErrorResponse() {
                loaderLayout.setVisibility(View.GONE);
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void makeDeleteOfferApiCall() {

        new RetrofitHelper(getActivity()).deleteSuggestedOfferForSeller(offerType, mOfferIdToDelete, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                makeOfferFetchApiCall();
            }

            @Override
            public void onErrorResponse() {
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
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

    @Override
    public void onProceedOrder(boolean isApproval, boolean isProceed) {

    }
}