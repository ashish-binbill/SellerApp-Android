package com.binbill.seller.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Offers.OfferActivity_;
import com.binbill.seller.R;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


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

        TextView transactionValue = (TextView) view.findViewById(R.id.transaction_value);
        TextView customerCount = (TextView) view.findViewById(R.id.customers);
        TextView pendingCredit = (TextView) view.findViewById(R.id.pending_credit);
        TextView loyalty = (TextView) view.findViewById(R.id.loyalty_points);

        DashboardModel dashboardModel = AppSession.getInstance(getActivity()).getDashboardData();
        if (dashboardModel != null) {
            transactionValue.setText(getString(R.string.rupee_sign) + " " + dashboardModel.getTotalTransactionValue());
            customerCount.setText(dashboardModel.getConsumerCount());
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
    }

    private void setUpData() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
