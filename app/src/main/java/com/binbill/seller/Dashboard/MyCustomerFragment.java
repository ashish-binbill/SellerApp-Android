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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.AppButtonGreyed;
import com.binbill.seller.Customer.AddCustomerActivity_;
import com.binbill.seller.Customer.UserInformationActivity_;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.Offers.UserAdapter;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/31/18.
 */

public class MyCustomerFragment extends Fragment implements UserAdapter.CardInteractionListener, SearchView.OnQueryTextListener {

    private RecyclerView userListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private UserAdapter mAdapter;
    private ArrayList<UserModel> mUserList;
    private int ADD_CREDIT = 1;
    private int ADD_POINTS = 2;
    private int page = 0;
    private LinearLayoutManager llm;
    private boolean isCallInProgress = false;
    private int lastPage = 0;


    public MyCustomerFragment() {
    }

    public static MyCustomerFragment newInstance() {
        MyCustomerFragment fragment = new MyCustomerFragment();
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
        return inflater.inflate(R.layout.fragment_my_customer, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefreshPage();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userListView = (RecyclerView) view.findViewById(R.id.rv_customer_list);
        shimmerview = (LinearLayout) view.findViewById(R.id.shimmer_view_container);
        noDataLayout = (LinearLayout) view.findViewById(R.id.no_data_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sl_pull_to_refresh);
        searchView = (SearchView) view.findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);

        setUpListeners();
        handleResponse();
    }

    public void showSearchView() {
        searchView.setVisibility(View.VISIBLE);
    }

    private void setUpListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                onRefreshPage();

            }
        });

        AppButton addCustomer = noDataLayout.findViewById(R.id.btn_no_data);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddCustomerActivity_.class));
            }
        });
    }


    public void onRefreshPage() {

        userListView.setVisibility(View.GONE);
        shimmerview.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.GONE);

        page = 0;
        fetchCustomers();
    }

    RecyclerView.OnScrollListener OnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = llm.getChildCount();
            int totalItemCount = llm.getItemCount();
            int firstVisibleItemPosition = llm.findFirstVisibleItemPosition();

            if (!isCallInProgress && page < lastPage && (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount)
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= Constants.CUSTOMER_PAGE_SIZE) {
                loadMoreItems();
                Log.d("SHRUTI", visibleItemCount + " " + totalItemCount + " " +
                        firstVisibleItemPosition);
            }
        }
    };

    private void loadMoreItems() {
        page = ++page;
        fetchCustomers();
    }


    private void fetchCustomers() {

        isCallInProgress = true;
        ApiHelper.fetchAllCustomer(getActivity(), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                isCallInProgress = false;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        if (jsonObject.has("last_page") && !jsonObject.isNull("last_page"))
                            lastPage = jsonObject.optInt("last_page");

                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<UserModel>>() {
                            }.getType();

                            ArrayList<UserModel> userList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(getActivity()).setMyCustomerList(userList);
                            handleResponse();
                        }
                    } else {
                        userListView.setVisibility(View.GONE);
                        shimmerview.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);
                        if (isAdded())
                            ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }
                } catch (JSONException e) {
                    userListView.setVisibility(View.GONE);
                    shimmerview.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                    if (isAdded())
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onErrorResponse() {

                userListView.setVisibility(View.GONE);
                shimmerview.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);

                if (isAdded())
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                swipeRefreshLayout.setRefreshing(false);

            }
        }, page);
    }

    private void handleResponse() {
        ArrayList<UserModel> userList = AppSession.getInstance(getActivity()).getMyCustomerList();
        if (userList != null && userList.size() > 0) {
            setUpData(userList);
        } else {
            userListView.setVisibility(View.GONE);
            shimmerview.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpData(ArrayList<UserModel> list) {

        if (this.mUserList == null)
            this.mUserList = list;
        else
            this.mUserList.addAll(list);
        userListView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        userListView.setLayoutManager(llm);
        mAdapter = new UserAdapter(Constants.MY_CUSTOMER, mUserList, this, false);
        if (page != lastPage)
            mAdapter.setLoadMore(true);
        else
            mAdapter.setLoadMore(false);
        userListView.setAdapter(mAdapter);
//        userListView.addOnScrollListener(OnScrollListener);

        if (list.size() > 0) {
            int pos = Constants.CUSTOMER_PAGE_SIZE * page;
            if (pos >= 0 && pos < mAdapter.getItemCount()) {
                final int position = pos;
                userListView.post(new Runnable() {
                    @Override
                    public void run() {
                        userListView.scrollToPosition(position);
                    }
                });
            }
        }

        userListView.setVisibility(View.VISIBLE);
        shimmerview.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCardInteraction() {

    }

    @Override
    public void onCardClicked(int position) {
        UserModel selectedModel = mUserList.get(position);
        Intent intent = new Intent(getActivity(), UserInformationActivity_.class);
        intent.putExtra(Constants.USER_MODEL, selectedModel);
        startActivity(intent);
    }

    @Override
    public void onAddPoints(int position) {
        UserModel selectedModel = mUserList.get(position);
        invokeAddSettleCreditDialog(ADD_POINTS, selectedModel.getUserId());
    }

    @Override
    public void onAddCredits(int position) {
        UserModel selectedModel = mUserList.get(position);
        invokeAddSettleCreditDialog(ADD_CREDIT, selectedModel.getUserId());
    }

    @Override
    public void onCustomerAdded(int position) {

    }

    private void invokeAddSettleCreditDialog(final int type, final String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_settle_credit, null);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView headerTitle = (TextView) dialogView.findViewById(R.id.header);
        final AppButton yesButton = (AppButton) dialogView.findViewById(R.id.btn_yes);
        final LinearLayout yesButtonProgress = (LinearLayout) dialogView.findViewById(R.id.btn_yes_progress);
        if (type == ADD_CREDIT)
            yesButton.setText(getString(R.string.add_credit));
        else if (type == ADD_POINTS)
            yesButton.setText(getString(R.string.add_points));
        final AppButtonGreyed noButton = (AppButtonGreyed) dialogView.findViewById(R.id.btn_no);

        if (type == ADD_CREDIT)
            headerTitle.setText(getString(R.string.add_credit));
        else if (type == ADD_POINTS)
            headerTitle.setText(getString(R.string.add_points));

        final EditText amount = (EditText) dialogView.findViewById(R.id.et_add_credit);
        if (type == ADD_CREDIT)
            amount.setHint(getString(R.string.add_credit_rupee));
        else if (type == ADD_POINTS)
            amount.setHint(getString(R.string.add_points));

        final EditText remarks = (EditText) dialogView.findViewById(R.id.et_add_remarks);
        final TextView charLeft = (TextView) dialogView.findViewById(R.id.tv_char_left);
        final TextView amountError = (TextView) dialogView.findViewById(R.id.tv_error_add_credit);

        remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charLeft.setText(count + "/150");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(getActivity(), noButton);
                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountError.setVisibility(View.GONE);
                Utility.hideKeyboard(getActivity(), yesButton);
                if (!Utility.isEmpty(amount.getText().toString())) {
                    yesButtonProgress.setVisibility(View.VISIBLE);
                    yesButton.setVisibility(View.GONE);
                    makeAddCreditApiCall(dialog, type, amount.getText().toString(), remarks.getText().toString(), userId);
                } else
                    amountError.setVisibility(View.VISIBLE);
            }
        });

        dialog.show();
    }

    private void makeAddCreditApiCall(final AlertDialog dialog, int type, String amount, String remarks, String userId) {

        String transactionType = "";
        if (type == ADD_CREDIT) {
            transactionType = "1";

            new RetrofitHelper(getActivity()).addSettleCredit(amount, remarks, transactionType, userId, new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optBoolean("status")) {
                            if (dialog != null)
                                dialog.dismiss();

                            page = 0;
                            fetchCustomers();
                        } else {
                            if (dialog != null)
                                dialog.dismiss();
                            ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                        }


                    } catch (JSONException e) {
                        if (dialog != null)
                            dialog.dismiss();
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void onErrorResponse() {
                    if (dialog != null)
                        dialog.dismiss();
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            });
        } else if (type == ADD_POINTS) {
            transactionType = "1";
            new RetrofitHelper(getActivity()).addSettleLoyaltyPoints(amount, remarks, transactionType, userId, new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optBoolean("status")) {
                            if (dialog != null)
                                dialog.dismiss();

                            page = 0;
                            fetchCustomers();
                        } else {
                            if (dialog != null)
                                dialog.dismiss();
                            ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                        }


                    } catch (JSONException e) {
                        if (dialog != null)
                            dialog.dismiss();
                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void onErrorResponse() {
                    if (dialog != null)
                        dialog.dismiss();
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            });
        }

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mAdapter != null)
            mAdapter.getFilter().filter(newText);
        return true;
    }
}

