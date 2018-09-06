package com.binbill.seller.Verification;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.Customer.CreditLoyaltyModel;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/4/18.
 */

public class LinkCreditPointsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static class LinkCreditPointsHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mValue, mRemarks, mDate;
        protected RadioButton mSelected;

        public LinkCreditPointsHolder(View view) {
            super(view);
            mRootCard = view;
            mValue = (TextView) view.findViewById(R.id.tv_credit);
            mRemarks = (TextView) view.findViewById(R.id.tv_remarks);
            mDate = (TextView) view.findViewById(R.id.tv_date);
            mSelected = (RadioButton) view.findViewById(R.id.rb_selected);
        }
    }

    private ArrayList<CreditLoyaltyModel> mList;

    public LinkCreditPointsAdapter(int type, ArrayList<CreditLoyaltyModel> list) {
        this.mList = list;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_credit_loyalty_link_item, parent, false);
        return new LinkCreditPointsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final LinkCreditPointsHolder creditLoyaltyHolder = (LinkCreditPointsHolder) holder;
        final CreditLoyaltyModel model = mList.get(position);

        if (!Utility.isEmpty(model.getDate())) {
            creditLoyaltyHolder.mDate.setText("Date : " + Utility.getFormattedDate(14, model.getDate(), 0));
        }

        if (!Utility.isEmpty(model.getDescription())) {
            creditLoyaltyHolder.mRemarks.setText(model.getDescription());
            creditLoyaltyHolder.mRemarks.setVisibility(View.VISIBLE);
        } else
            creditLoyaltyHolder.mRemarks.setVisibility(View.GONE);

        if (!Utility.isEmpty(model.getTransactionType())) {

            if (model.getTransactionType().equalsIgnoreCase(Constants.DEBIT)) {
                creditLoyaltyHolder.mValue.setText("Debit : " + model.getAmount());
            } else if (model.getTransactionType().equalsIgnoreCase(Constants.CREDIT)) {
                creditLoyaltyHolder.mValue.setText("Credit : " + model.getAmount());
            }
        }

        if (model.isSelected())
            creditLoyaltyHolder.mSelected.setChecked(true);
        else
            creditLoyaltyHolder.mSelected.setChecked(false);


        creditLoyaltyHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!model.isSelected()) {
                    setAllRowsUnselected();
                    model.setSelected(true);
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void setAllRowsUnselected() {
        for (CreditLoyaltyModel creditModel : mList) {
            creditModel.setSelected(false);
        }
    }

    public ArrayList<CreditLoyaltyModel> getUpdatedList() {
        return mList;
    }
}
