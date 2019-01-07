package com.binbill.seller.Offers;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BarCodeOfferFragment extends Fragment implements OfferAdapter.OfferManipulationListener, YesNoDialogFragment.YesNoClickInterface {

    private RecyclerView offerListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<OfferItem> mBarcodeOfferList;
    private String mOfferIdToDelete = "";
    private Button noDataButton;
    private Button btnAddData;
    private int offerType;
    private OfferAdapter mAdapter;

    public BarCodeOfferFragment() {
    }

    public static BarCodeOfferFragment newInstance(int offerType) {
        BarCodeOfferFragment fragment = new BarCodeOfferFragment();
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
        btnAddData = (SquareAppButton) view.findViewById(R.id.btn_add_data);
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
        btnAddData.setVisibility(View.VISIBLE);


        String sellerId = AppSession.getInstance(getActivity()).getSellerId();
        new RetrofitHelper(getActivity()).fetchBarcodeOffersForSeller(sellerId, offerType, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                swipeRefreshLayout.setRefreshing(false);
                handleResponse(response);
            }

            @Override
            public void onErrorResponse() {
                swipeRefreshLayout.setRefreshing(false);
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
                        if(offerType == Constants.OFFER_TYPE_DISCOUNTED ||
                                offerType == Constants.OFFER_TYPE_BOGO || offerType == Constants.OFFER_TYPE_GENERAL) {
                            btnAddData.setVisibility(View.VISIBLE);
                        }else{
                            btnAddData.setVisibility(View.GONE);
                        }
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
        if (offerType == Constants.OFFER_TYPE_GENERAL)
            mAdapter = new OfferAdapter(this, mBarcodeOfferList, Constants.OFFER_TYPE_GENERAL, offerType);
        else
            mAdapter = new OfferAdapter(this, mBarcodeOfferList, Constants.TYPE_BARCODE_OFFER, offerType);
        offerListView.setAdapter(mAdapter);

        offerListView.setVisibility(View.VISIBLE);
        shimmerview.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }

    private void showNoOfferLayout() {
        btnAddData.setVisibility(View.GONE);
        TextView noDataText = (TextView) noDataLayout.findViewById(R.id.tv_no_data);
        noDataButton = (Button) noDataLayout.findViewById(R.id.btn_no_data);
        try {
            noDataButton.setText(getString(R.string.add_offers));
        }catch (Exception e){
            e.printStackTrace();
        }
        noDataLayout.setVisibility(View.VISIBLE);
        if(offerType == Constants.OFFER_TYPE_DISCOUNTED ||
                offerType == Constants.OFFER_TYPE_BOGO || offerType == Constants.OFFER_TYPE_GENERAL) {
            btnAddData.setVisibility(View.VISIBLE);
        }else{
            btnAddData.setVisibility(View.GONE);
            noDataButton.setVisibility(View.GONE);
        }
        if (offerType == Constants.OFFER_TYPE_DISCOUNTED)
            noDataText.setText(getString(R.string.no_offers_added));
        else {
            ImageView noDataImage = (ImageView) noDataLayout.findViewById(R.id.iv_no_data_image);
            try {
                noDataImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_uhh_ohh));
            }catch (Exception e){
                e.printStackTrace();
            }
            if(offerType == Constants.OFFER_TYPE_BOGO){
                btnAddData.setVisibility(View.GONE);
                noDataButton.setVisibility(View.VISIBLE);
                noDataText.setText(getString(R.string.bogo_no_offers));
            }
            if(offerType == Constants.OFFER_TYPE_EXTRA){
             //   noDataButton.setVisibility(View.VISIBLE);
                noDataText.setText(getString(R.string.extra_no_offers));
            }
            if(offerType == Constants.OFFER_TYPE_GENERAL) {
                btnAddData.setVisibility(View.GONE);
                noDataButton.setVisibility(View.VISIBLE);
                noDataText.setText(getString(R.string.no_general_offers));
            }
            if(offerType == Constants.OFFER_TYPE_NEW_PRODUCT) {
               // noDataButton.setVisibility(View.VISIBLE);
                noDataText.setText(getString(R.string.extra_no_offers));
            }
        }
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

        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddBarCodeOfferActivity_.class);
                if(offerType == Constants.OFFER_TYPE_BOGO) {
                    intent.putExtra("OfferType", Constants.OFFER_TYPE_BOGO);
                }
                if(offerType == Constants.OFFER_TYPE_GENERAL){
                    intent.putExtra("OfferType", Constants.OFFER_TYPE_BOGO);
                }
                if(offerType == Constants.OFFER_TYPE_DISCOUNTED){
                    intent.putExtra("OfferType", Constants.OFFER_TYPE_DISCOUNTED);
                }
                startActivity(intent);
            }
        });

        noDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((OfferActivity) getActivity()).invokeAddOfferOptions();
                Intent intent = new Intent(getActivity(), AddBarCodeOfferActivity_.class);
                if(offerType == Constants.OFFER_TYPE_BOGO) {
                    intent.putExtra("OfferType", Constants.OFFER_TYPE_BOGO);
                }
                if(offerType == Constants.OFFER_TYPE_GENERAL){
                    intent.putExtra("OfferType", Constants.OFFER_TYPE_BOGO);
                }
                if(offerType == Constants.OFFER_TYPE_DISCOUNTED){
                    intent.putExtra("OfferType", Constants.OFFER_TYPE_DISCOUNTED);
                }
                startActivity(intent);
            }
        });
    }


    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        if (positionItem > -1) {
            String saveFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            SimpleDateFormat sdf = new SimpleDateFormat(saveFormat, Locale.ENGLISH);
            mBarcodeOfferList.get(positionItem).setOfferEndDate(sdf.format(mCalendar.getTime()));
            mAdapter.notifyItemChanged(positionItem);

            makeOfferEditCall();
        }
    }

    private void makeOfferEditCall() {

        OfferItem mOfferItem = mBarcodeOfferList.get(positionItem);
        String skuId = mOfferItem.getSkuId();
        String skuMeasurementId = mOfferItem.getSkuMeasurementId();

        String expiry = mOfferItem.getOfferEndDate();

        String brandOfferId = mOfferItem.getOfferId();
        String offerType1 ="";
        if(offerType == Constants.OFFER_TYPE_BOGO) {
            offerType1 = String.valueOf(offerType);
        }
        if(offerType == Constants.OFFER_TYPE_GENERAL){
            offerType1 = String.valueOf(offerType);
        }
        if(offerType == Constants.OFFER_TYPE_DISCOUNTED){
            offerType1 = String.valueOf(offerType);
        }

        new RetrofitHelper(getActivity()).addBarCodeOfferFromSeller(skuId, skuMeasurementId, null, null, expiry, brandOfferId, brandOfferId, offerType1, null, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onErrorResponse() {
                ((OfferActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    Calendar mCalendar = Calendar.getInstance();
    int positionItem = -1;

    @Override
    public void onOfferManupulation(int position, String type) {
        switch (type) {
            case Constants.EDIT_OFFER:
                Intent intent = new Intent(getActivity(), AddBarCodeOfferActivity_.class);
                if(offerType == Constants.OFFER_TYPE_BOGO) {
                    intent.putExtra("OfferType", Constants.OFFER_TYPE_BOGO);
                }
                if(offerType == Constants.OFFER_TYPE_GENERAL){
                    intent.putExtra("OfferType", Constants.OFFER_TYPE_BOGO);
                }
                if(offerType == Constants.OFFER_TYPE_DISCOUNTED){
                    intent.putExtra("OfferType", Constants.OFFER_TYPE_DISCOUNTED);
                }
                intent.putExtra(Constants.OFFER_ITEM, mBarcodeOfferList.get(position));
                startActivity(intent);
                break;
            case Constants.DELETE_OFFER:
                mOfferIdToDelete = mBarcodeOfferList.get(position).getOfferId();
                ((OfferActivity) getActivity()).showYesNoDialog();
                break;
            case Constants.ADD_EXPIRY_IN_OFFER:
                positionItem = position;
                mOfferIdToDelete = mBarcodeOfferList.get(position).getOfferId();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
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

    @Override
    public void onProceedOrder(boolean isApproval, boolean isProceed) {

    }
}
