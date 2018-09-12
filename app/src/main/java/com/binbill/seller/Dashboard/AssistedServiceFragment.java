package com.binbill.seller.Dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.AssistedService.AddAssistedServiceActivity_;
import com.binbill.seller.AssistedService.AdditionalServiceDialogFragment;
import com.binbill.seller.AssistedService.AssistedServiceAdapter;
import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.ReviewsDialogFragment;
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

/**
 * Created by shruti.vig on 9/2/18.
 */

public class AssistedServiceFragment extends Fragment implements AssistedServiceAdapter.CardInteractionListener, SearchView.OnQueryTextListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView assistedListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<AssistedUserModel> assistedUserList;
    private String mAssistedServiceIdToDelete;
    private AssistedServiceAdapter mAdapter;
    private SearchView searchView;


    public AssistedServiceFragment() {
    }

    public static AssistedServiceFragment newInstance(String param1, String param2) {
        AssistedServiceFragment fragment = new AssistedServiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assisted_service, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAssistedService();
    }

    public void showSearchView() {
        searchView.setVisibility(View.VISIBLE);
    }

    private void fetchAssistedService() {
        ApiHelper.fetchAllAssistedServices(getActivity(), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<AssistedUserModel>>() {
                            }.getType();

                            ArrayList<AssistedUserModel> userList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(getActivity()).setAssistedServiceList(userList);
                            handleResponse();
                        }
                    }else{
                        assistedListView.setVisibility(View.GONE);
                        shimmerview.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);

                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }
                } catch (JSONException e) {
                    assistedListView.setVisibility(View.GONE);
                    shimmerview.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);

                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onErrorResponse() {

                assistedListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);

                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assistedListView = (RecyclerView) view.findViewById(R.id.rv_assisted_service_list);
        shimmerview = (LinearLayout) view.findViewById(R.id.shimmer_view_container);
        noDataLayout = (LinearLayout) view.findViewById(R.id.no_data_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sl_pull_to_refresh);
        searchView = (SearchView) view.findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);

        TextView noDataText = noDataLayout.findViewById(R.id.tv_no_data);
        noDataText.setText(getString(R.string.please_add_assisted_service));

        AppButton noDataButton = noDataLayout.findViewById(R.id.btn_no_data);
        noDataButton.setText(getString(R.string.add_service_now));

        setUpListeners();
        handleResponse();
    }

    private void setUpListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                assistedListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);

                fetchAssistedService();
            }
        });

        AppButton addCustomer = noDataLayout.findViewById(R.id.btn_no_data);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddAssistedServiceActivity_.class));
            }
        });
    }

    private void handleResponse() {
        assistedUserList = AppSession.getInstance(getActivity()).getAssistedServiceList();
        if (assistedUserList != null && assistedUserList.size() > 0) {
            setUpData(assistedUserList);
        } else {
            assistedListView.setVisibility(View.GONE);
            shimmerview.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpData(ArrayList<AssistedUserModel> mUserList) {

        assistedListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        assistedListView.setLayoutManager(llm);
        mAdapter = new AssistedServiceAdapter(mUserList, this);
        assistedListView.setAdapter(mAdapter);

        assistedListView.setVisibility(View.VISIBLE);
        shimmerview.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDeleteAssistedService(int position) {
        mAssistedServiceIdToDelete = assistedUserList.get(position).getId();
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        YesNoDialogFragment fragment = YesNoDialogFragment.newInstance(getString(R.string.delete_service), "Delete Service");
        fragment.show(fm, "YesNoDialogFragment");
    }

    @Override
    public void addAdditionalService(int position) {

        String assistedServiceId = assistedUserList.get(position).getId();
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        AdditionalServiceDialogFragment fragment = AdditionalServiceDialogFragment.newInstance(assistedServiceId, assistedUserList.get(position));
        fragment.show(fm, "AdditionalServiceDialogFragment");
    }

    @Override
    public void onEditServiceCard(int position) {
        AssistedUserModel assistedUserModel = assistedUserList.get(position);
        Intent intent = new Intent(getActivity(), AddAssistedServiceActivity_.class);
        intent.putExtra(Constants.EDIT_ASSISTED_SERVICES, assistedUserModel);
        getActivity().startActivity(intent);
    }

    @Override
    public void onShowReviews(int position) {
        AssistedUserModel assistedUserModel = assistedUserList.get(position);
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        ReviewsDialogFragment fragment = ReviewsDialogFragment.newInstance(getString(R.string.reviews_string), assistedUserModel.getReviews());
        fragment.show(fm, "ReviewsDialogFragment");
    }


    public void onOptionSelected(boolean isProceed) {
        if (isProceed) {
            makeDeleteAssistedServiceApiCall();
        } else {
            /**
             * do nothing
             */
        }
    }

    private void makeDeleteAssistedServiceApiCall() {
        new RetrofitHelper(getActivity()).deleteAssistedService(mAssistedServiceIdToDelete, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                fetchAssistedService();
            }

            @Override
            public void onErrorResponse() {
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    public void onAddService(String assistedServiceId, String linkId, String serviceTypeId, String price) {

        /**
         * Make api call
         */
        new RetrofitHelper(getActivity()).addServiceToAssistedService(assistedServiceId,linkId, serviceTypeId, price, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                invokeSuccessDialog();
                fetchAssistedService();
            }

            @Override
            public void onErrorResponse() {
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));

            }
        });


    }

    public void invokeSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_assisted_service, null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        AppButton yesButton = (AppButton) dialogView.findViewById(R.id.btn_yes);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }
}


