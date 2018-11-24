package com.binbill.seller.Offers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BarCodeOfferFragment extends Fragment implements OfferAdapter.OfferManipulationListener, YesNoDialogFragment.YesNoClickInterface {

    private RecyclerView offerListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<OfferItem> mBarcodeOfferList;
    private String mOfferIdToDelete = "";
    private Button noDataButton;

    public BarCodeOfferFragment() {
    }

    public static BarCodeOfferFragment newInstance() {
        BarCodeOfferFragment fragment = new BarCodeOfferFragment();
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
        makeOfferFetchApiCall();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        offerListView = (RecyclerView) view.findViewById(R.id.rv_order_list);
        shimmerview = (LinearLayout) view.findViewById(R.id.shimmer_view_container);
        noDataLayout = (LinearLayout) view.findViewById(R.id.no_data_layout);

        noDataButton = (AppButton) noDataLayout.findViewById(R.id.btn_no_data);
        noDataButton.setVisibility(View.VISIBLE);

        ImageView noDataImage = (ImageView) noDataLayout.findViewById(R.id.iv_no_data_image);
        noDataImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_no_data_smile));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sl_pull_to_refresh);

        setUpListeners();
    }

    private void makeOfferFetchApiCall() {

        shimmerview.setVisibility(View.VISIBLE);
        offerListView.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);


        String sellerId = AppSession.getInstance(getActivity()).getSellerId();
        new RetrofitHelper(getActivity()).fetchBarcodeOffersForSeller(sellerId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response);
            }

            @Override
            public void onErrorResponse() {
                if (isAdded()) {
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    showNoOfferLayout();
                }
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

        mBarcodeOfferList = new Gson().fromJson(offerArray.toString(), classType);
        offerListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        offerListView.setLayoutManager(llm);
        OfferAdapter mAdapter = new OfferAdapter(this, mBarcodeOfferList, Constants.TYPE_BARCODE_OFFER);
        offerListView.setAdapter(mAdapter);

        offerListView.setVisibility(View.VISIBLE);
        shimmerview.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }

    private void showNoOfferLayout() {
        TextView noDataText = (TextView) noDataLayout.findViewById(R.id.tv_no_data);
        noDataText.setText(getString(R.string.no_offers_added));

        noDataButton = (Button) noDataLayout.findViewById(R.id.btn_no_data);
        noDataButton.setText(getString(R.string.add_offers));
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
//                ((OfferActivity) getActivity()).invokeAddOfferOptions();
                Intent intent = new Intent(getActivity(), AddBarCodeOfferActivity_.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onOfferManupulation(int position, String type) {
        switch (type) {
            case Constants.EDIT_OFFER:
                Intent intent = new Intent(getActivity(), AddBarCodeOfferActivity_.class);
                intent.putExtra(Constants.OFFER_ITEM, mBarcodeOfferList.get(position));
                startActivity(intent);
                break;
            case Constants.DELETE_OFFER:

                mOfferIdToDelete = mBarcodeOfferList.get(position).getOfferId();
                ((OfferActivity) getActivity()).showYesNoDialog();
                break;
        }
    }

    private void makeDeleteOfferApiCall() {

        new RetrofitHelper(getActivity()).deleteOfferForSeller(mOfferIdToDelete, new RetrofitHelper.RetrofitCallback() {
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

    public void onOptionSelected(boolean isProceed) {
        if (isProceed) {
            makeDeleteOfferApiCall();
        } else {
/**
 * do nothing
 */
        }
    }
}
