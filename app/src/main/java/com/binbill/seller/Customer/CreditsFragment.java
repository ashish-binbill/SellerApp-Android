package com.binbill.seller.Customer;

import android.app.AlertDialog;
import android.content.Context;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.AppButtonGreyed;
import com.binbill.seller.Model.UserModel;
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

public class CreditsFragment extends Fragment {

    private RecyclerView userListView;
    private LinearLayout shimmerview, noDataLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserModel userModel;
    private RelativeLayout recyclerData;
    private AppButton addCredit, settleCredit;
    private int ADD_CREDIT = 1;
    private int SETTLE_CREDIT = 2;
    private TextView totalCredits;


    public CreditsFragment() {
    }

    public static CreditsFragment newInstance(UserModel userModel) {
        CreditsFragment fragment = new CreditsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_MODEL, userModel);
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
        return inflater.inflate(R.layout.fragment_credits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalCredits = (TextView) view.findViewById(R.id.tv_total_credits);
        TextView totalLabel = (TextView) view.findViewById(R.id.tv_total_label);
        totalLabel.setText(getString(R.string.total_credits));
        userListView = (RecyclerView) view.findViewById(R.id.rv_credits_view);

        recyclerData = (RelativeLayout) view.findViewById(R.id.rl_recycler_data);
        shimmerview = (LinearLayout) view.findViewById(R.id.shimmer_view_container);
        noDataLayout = (LinearLayout) view.findViewById(R.id.no_data_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sl_pull_to_refresh);
        addCredit = (AppButton) view.findViewById(R.id.btn_add_credit);
        settleCredit = (AppButton) view.findViewById(R.id.btn_settle_credit);

        TextView noDataTitle = (TextView) noDataLayout.findViewById(R.id.tv_no_data);
        noDataTitle.setText(getString(R.string.no_credit));
        AppButton noDataButton = (AppButton) noDataLayout.findViewById(R.id.btn_no_data);
        noDataButton.setVisibility(View.GONE);
        userModel = (UserModel) getArguments().getSerializable(Constants.USER_MODEL);

        if (userModel != null) {
            totalCredits.setText(userModel.getUserCredit());
            makeCreditHistoryCall();
        }

        setUpListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void makeCreditHistoryCall() {

        recyclerData.setVisibility(View.GONE);
        shimmerview.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.GONE);

        new RetrofitHelper(getActivity()).getUserCredits(userModel.getUserId(), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        String total = jsonObject.optString("total_credits");
                        totalCredits.setText(total);

                        JSONArray userArray = jsonObject.getJSONArray("result");
                        Type classType = new TypeToken<ArrayList<CreditLoyaltyModel>>() {
                        }.getType();

                        ArrayList<CreditLoyaltyModel> creditList = new Gson().fromJson(userArray.toString(), classType);
                        handleResponse(creditList);

                    } else {
                        recyclerData.setVisibility(View.GONE);
                        shimmerview.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);

                        ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    recyclerData.setVisibility(View.GONE);
                    shimmerview.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                    ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                recyclerData.setVisibility(View.GONE);
                shimmerview.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);
                ((BaseActivity) getActivity()).showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void setUpListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                recyclerData.setVisibility(View.GONE);
                shimmerview.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);

                makeCreditHistoryCall();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        addCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invokeAddSettleCreditDialog(ADD_CREDIT);

            }
        });

        settleCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invokeAddSettleCreditDialog(SETTLE_CREDIT);
            }
        });
    }

    private void invokeAddSettleCreditDialog(final int type) {
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
        else if (type == SETTLE_CREDIT)
            yesButton.setText(getString(R.string.settle_credit));
        final AppButtonGreyed noButton = (AppButtonGreyed) dialogView.findViewById(R.id.btn_no);

        if (type == ADD_CREDIT)
            headerTitle.setText(getString(R.string.add_credit));
        else if (type == SETTLE_CREDIT)
            headerTitle.setText(getString(R.string.settle_credit));

        final EditText amount = (EditText) dialogView.findViewById(R.id.et_add_credit);
        if (type == ADD_CREDIT)
            amount.setHint(getString(R.string.add_credit));
        else if (type == SETTLE_CREDIT)
            amount.setHint(getString(R.string.settle_credit));

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

                    makeAddCreditApiCall(dialog, type, amount.getText().toString(), remarks.getText().toString());
                } else
                    amountError.setVisibility(View.VISIBLE);
            }
        });

        dialog.show();
    }

    private void makeAddCreditApiCall(final AlertDialog dialog, int type, String amount, String remarks) {

        String transactionType = "";
        if (type == ADD_CREDIT)
            transactionType = "1";
        else if (type == SETTLE_CREDIT)
            transactionType = "2";
        new RetrofitHelper(getActivity()).addSettleCredit(amount, remarks, transactionType, userModel.getUserId(), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {
                        if (dialog != null)
                            dialog.dismiss();

                        makeCreditHistoryCall();
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

    private void handleResponse(ArrayList<CreditLoyaltyModel> list) {
        if (list != null && list.size() > 0) {
            setUpData(list);
        } else {
            recyclerData.setVisibility(View.GONE);
            shimmerview.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setUpData(ArrayList<CreditLoyaltyModel> list) {

        userListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        userListView.setLayoutManager(llm);
        CreditLoyaltyAdapter mAdapter = new CreditLoyaltyAdapter(1, list);
        userListView.setAdapter(mAdapter);

        recyclerData.setVisibility(View.VISIBLE);
        shimmerview.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
