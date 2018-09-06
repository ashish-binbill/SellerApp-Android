package com.binbill.seller.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binbill.seller.Interface.ItemSelectedInterface;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/16/18.
 */

public class DeliveryDistanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ItemSelectedInterface listener;

    public static class DeliveryDistanceHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mDistance;

        public DeliveryDistanceHolder(View view) {
            super(view);
            mRootCard = view;
            mDistance = (TextView) view.findViewById(R.id.tv_main_category);
        }
    }

    private ArrayList<String> mMainList;

    public DeliveryDistanceAdapter(ArrayList<String> list, ItemSelectedInterface itemSelectedInterface) {
        this.mMainList = list;
        this.listener = itemSelectedInterface;
    }


    @Override
    public int getItemCount() {
        return mMainList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_selection_list, parent, false);
        return new DeliveryDistanceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final DeliveryDistanceHolder deliveryDistanceHolder = (DeliveryDistanceHolder) holder;

        deliveryDistanceHolder.mDistance.setText(mMainList.get(position));

        deliveryDistanceHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onItemSelected(mMainList.get(position));
            }
        });
    }
}
