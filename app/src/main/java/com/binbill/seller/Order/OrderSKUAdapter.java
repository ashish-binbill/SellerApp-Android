package com.binbill.seller.Order;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binbill.seller.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shruti.vig on 9/6/18.
 */

class OrderSKUAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OrderSKUInterface {
        void onSKUSelected(OrderItem.OrderSKU sku, String itemID);
    }

    public static class OrderSKUHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mValue;

        public OrderSKUHolder(View view) {
            super(view);
            mRootCard = view;
            mValue = (TextView) view.findViewById(R.id.text);
        }
    }

    private ArrayList<OrderItem.OrderSKU> mList;
    private OrderSKUInterface listener;
    private String itemId;

    public OrderSKUAdapter(ArrayList<OrderItem.OrderSKU> list, OrderSKUInterface skuInterface, String item) {
        this.mList = list;
        this.listener = skuInterface;
        this.itemId = item;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_text_view, parent, false);
        return new OrderSKUHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final OrderSKUHolder orderSKUHolder = (OrderSKUHolder) holder;
        final OrderItem.OrderSKU model = mList.get(position);

        orderSKUHolder.mValue.setText(model.getSkuMeasurementValue() + " " + model.getSkuMeasurementAcronym());

        orderSKUHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onSKUSelected(model, itemId);
            }
        });

    }
}

