package com.binbill.seller.Customer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/3/18.
 */

public class CreditLoyaltyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int mType;

    public static class CreditLoyaltyHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mValue, mRemarks, mDate, mMonth, mTitle;

        public CreditLoyaltyHolder(View view) {
            super(view);
            mRootCard = view;
            mMonth = (TextView) view.findViewById(R.id.tv_month);
            mValue = (TextView) view.findViewById(R.id.tv_points);
            mRemarks = (TextView) view.findViewById(R.id.tv_remarks);
            mDate = (TextView) view.findViewById(R.id.tv_day);
            mTitle = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    private ArrayList<CreditLoyaltyModel> mList;

    public CreditLoyaltyAdapter(int type, ArrayList<CreditLoyaltyModel> list) {
        this.mList = list;
        this.mType = type;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_credit_loyalty_item, parent, false);
        return new CreditLoyaltyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final CreditLoyaltyHolder creditLoyaltyHolder = (CreditLoyaltyHolder) holder;
        final CreditLoyaltyModel model = mList.get(position);

        if (!Utility.isEmpty(model.getDate())) {
            creditLoyaltyHolder.mDate.setText(Utility.getFormattedDate(15, model.getDate(), 0));
            creditLoyaltyHolder.mMonth.setText(Utility.getFormattedDate(16, model.getDate(), 0));
        }

        if (!Utility.isEmpty(model.getDescription())) {
            creditLoyaltyHolder.mRemarks.setText(model.getDescription());
            creditLoyaltyHolder.mRemarks.setVisibility(View.VISIBLE);
        } else
            creditLoyaltyHolder.mRemarks.setVisibility(View.GONE);

        if (!Utility.isEmpty(model.getTransactionType())) {

            if (model.getTransactionType().equalsIgnoreCase(Constants.DEBIT)) {
                if (mType == 1)
                    creditLoyaltyHolder.mTitle.setText("Credit Debited : ");
                else
                    creditLoyaltyHolder.mTitle.setText("Points Debited : ");
            } else if (model.getTransactionType().equalsIgnoreCase(Constants.CREDIT)) {
                if (mType == 1)
                    creditLoyaltyHolder.mTitle.setText("Credit Added : ");
                else
                    creditLoyaltyHolder.mTitle.setText("Points Added : ");

            }

            creditLoyaltyHolder.mValue.setText(model.getAmount());
        }
    }
}