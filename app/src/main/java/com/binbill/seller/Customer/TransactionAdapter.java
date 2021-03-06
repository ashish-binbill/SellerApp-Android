package com.binbill.seller.Customer;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/5/18.
 */

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    interface TransactionCardInterface{
        void onViewBill(int pos);
    }

    public static class TransactionHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mUserName, mTransactionId, mItemCount, mHomeDelivery,
                mAmount, mBBCash, mTotalCredits, mTotalPoints, mViewBill;

        public TransactionHolder(View view) {
            super(view);
            mRootCard = view;
            mUserName = (TextView) view.findViewById(R.id.tv_customer_name);
            mTransactionId = (TextView) view.findViewById(R.id.tv_txn_id);
            mItemCount = (TextView) view.findViewById(R.id.tv_item_count);
            mHomeDelivery = (TextView) view.findViewById(R.id.tv_home_delivery);
            mAmount = (TextView) view.findViewById(R.id.tv_amount);
            mBBCash = (TextView) view.findViewById(R.id.tv_bb_cash);
            mTotalCredits = (TextView) view.findViewById(R.id.tv_credit);
            mTotalPoints = (TextView) view.findViewById(R.id.tv_points);
            mViewBill = (TextView) view.findViewById(R.id.tv_view_bill);
        }
    }

    private ArrayList<TransactionModel> mList;
    private final TransactionCardInterface mListener;

    public TransactionAdapter(ArrayList<TransactionModel> list, TransactionCardInterface interfaceObject) {
        this.mList = list;
        this.mListener = interfaceObject;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_transaction_item, parent, false);
        return new TransactionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final TransactionHolder transactionHolder = (TransactionHolder) holder;
        final TransactionModel transaction = mList.get(position);

//        transactionHolder.mUserName.setText(transaction.getUserName());

//        if (!Utility.isEmpty(transaction.getd())) {
//            transactionHolder.mDate.setText(Utility.getFormattedDate(15, model.getDate(), 0));
//            transactionHolder.mMonth.setText(Utility.getFormattedDate(16, model.getDate(), 0));
//        }

        transactionHolder.mTransactionId.setText(" " + transaction.getTransactionId());
        transactionHolder.mItemCount.setText(" " + transaction.getItemCount());
        String amountText = " " + transactionHolder.mAmount.getContext().getString(R.string.rupee_sign) + transaction.getAmount();
        transactionHolder.mAmount.setText(amountText);

        String homeDelivery = "No";
        if (transaction.isHomeDelivered())
            homeDelivery = "Yes";

        transactionHolder.mHomeDelivery.setText(" " + homeDelivery);
        transactionHolder.mBBCash.setText(" " + transaction.getTotalCashback());
        transactionHolder.mTotalCredits.setText(" " + transaction.getTotalCredits());
        transactionHolder.mTotalPoints.setText(" " + transaction.getTotalLoyalty());

        SpannableString content = new SpannableString(transactionHolder.mViewBill.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        transactionHolder.mViewBill.setText(content);


        transactionHolder.mViewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onViewBill(position);
            }
        });
    }
}
