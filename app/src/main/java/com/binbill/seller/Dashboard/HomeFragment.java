package com.binbill.seller.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.Constants;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Offers.OfferActivity_;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final int FMCG_ASSISTED_USER_POS = 0;
    private static final int FMCG_ASSISTED_USER_NO_POS = 1;
    private static final int FMCG_ONLY_USER_HAS_POS = 2;
    private static final int FMCG_ONLY_USER_NO_POS = 3;
    private static final int ASSISTED_ONLY_USER = 4;


    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApiHelper.makeDashboardDataCall(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView transactionValue = (TextView) view.findViewById(R.id.transaction_value);
        final TextView customerCount = (TextView) view.findViewById(R.id.customers);
        final TextView pendingCredit = (TextView) view.findViewById(R.id.pending_credit);
        final TextView loyalty = (TextView) view.findViewById(R.id.loyalty_points);

        final String emptyValue = getString(R.string.rupee_sign) + " 0";

        DashboardModel dashboardModel = AppSession.getInstance(getActivity()).getDashboardData();
        if (dashboardModel != null) {
            transactionValue.setText(getString(R.string.rupee_sign) + " " + dashboardModel.getTotalTransactionValue());
            pendingCredit.setText(getString(R.string.rupee_sign) + " " + dashboardModel.getCreditPending());
            loyalty.setText(dashboardModel.getLoyaltyPoints());
        }

        CardView manageOffers = (CardView) view.findViewById(R.id.manage_offers);
        manageOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OfferActivity_.class));
            }
        });

        final TextView tvLabelCustomer = (TextView) view.findViewById(R.id.tv_label_customer);
        ImageView ivLabelCustomer = (ImageView) view.findViewById(R.id.iv_customer);

        switch (dashboardModel.getSellerType()) {
            case ASSISTED_ONLY_USER:
                tvLabelCustomer.setText(getString(R.string.total_agents));
                if (!Utility.isEmpty(dashboardModel.getAssistedUserCount()))
                    customerCount.setText(dashboardModel.getTotalUserCashback());
                else
                    customerCount.setText("0");
                ivLabelCustomer.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_customer_icon));
                break;
            default:
                tvLabelCustomer.setText(getString(R.string.cashback_to_users));
                if (!Utility.isEmpty(dashboardModel.getTotalUserCashback()))
                    customerCount.setText(getString(R.string.rupee_sign) + " " + dashboardModel.getTotalUserCashback());
                else
                    customerCount.setText(getString(R.string.rupee_sign) + " 0");
                ivLabelCustomer.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_cashback));
                break;
        }
        /**
         * Layout listeners
         */

      /*  LinearLayout transactionLayout = (LinearLayout) view.findViewById(R.id.ll_transaction);
        transactionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        LinearLayout allCredits = (LinearLayout) view.findViewById(R.id.ll_credit);
        allCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pendingCredit.getText().toString().equals(emptyValue)) {
                    Intent intent = new Intent(getActivity(), DashboardListActivity_.class);
                    intent.putExtra(Constants.TYPE, Constants.CREDIT_PENDING);
                    startActivity(intent);
                }
            }
        });

        LinearLayout allPoints = (LinearLayout) view.findViewById(R.id.ll_points);
        allPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loyalty.getText().toString().equals("0")) {
                    Intent intent = new Intent(getActivity(), DashboardListActivity_.class);
                    intent.putExtra(Constants.TYPE, Constants.POINTS);
                    startActivity(intent);
                }
            }
        });

        LinearLayout allTransactions = (LinearLayout) view.findViewById(R.id.ll_transaction);
        allTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!transactionValue.getText().toString().equals(emptyValue)) {
                    Intent intent = new Intent(getActivity(), DashboardListActivity_.class);
                    intent.putExtra(Constants.TYPE, Constants.TRANSACTIONS);
                    startActivity(intent);
                }
            }
        });

        LinearLayout allAgents = (LinearLayout) view.findViewById(R.id.ll_agent_cashback);
        allAgents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tvLabelCustomer.getText().toString().equalsIgnoreCase(getString(R.string.cashback_to_users))) {
                    if (!customerCount.getText().toString().equals(emptyValue)) {
                        Intent intent = new Intent(getActivity(), DashboardListActivity_.class);
                        intent.putExtra(Constants.TYPE, Constants.CASHBACKS);
                        startActivity(intent);
                    }
                } else {
                    FragmentEventListener eventListener = (FragmentEventListener) getActivity();
                    eventListener.changeViewPagerFragment(4);
                }
            }
        });


    }

    private void setUpData() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
