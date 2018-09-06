package com.binbill.seller.Verification;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/4/18.
 */

public class RejectReasonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static class RejectReasonHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mReason;
        protected RadioButton mSelected;

        public RejectReasonHolder(View view) {
            super(view);
            mRootCard = view;
            mReason = (TextView) view.findViewById(R.id.tv_reason);
            mSelected = (RadioButton) view.findViewById(R.id.rb_selected);
        }
    }

    private ArrayList<RejectReasonModel> mList;

    public RejectReasonAdapter(ArrayList<RejectReasonModel> list) {
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
                inflate(R.layout.row_reject_reason, parent, false);
        return new RejectReasonHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final RejectReasonHolder rejectReasonHolder = (RejectReasonHolder) holder;
        final RejectReasonModel model = mList.get(position);

        rejectReasonHolder.mReason.setText(model.getTitle());

        if (model.isSelected())
            rejectReasonHolder.mSelected.setChecked(true);
        else
            rejectReasonHolder.mSelected.setChecked(false);


        rejectReasonHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
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
        for (RejectReasonModel model : mList) {
            model.setSelected(false);
        }
    }

    public ArrayList<RejectReasonModel> getUpdatedList() {
        return mList;
    }
}
