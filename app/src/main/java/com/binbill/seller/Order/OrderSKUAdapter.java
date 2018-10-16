package com.binbill.seller.Order;

import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.R;
import com.binbill.seller.Utility;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/6/18.
 */

class OrderSKUAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OrderSKUInterface {
        void onSKUSelected(OrderItem.OrderSKU sku, String itemID);

        void onSKUSelected(SuggestionSku sku, String itemID);
    }

    public static class OrderSKUHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mValue;
        protected ImageView skuImage;

        public OrderSKUHolder(View view) {
            super(view);
            mRootCard = view;
            mValue = (TextView) view.findViewById(R.id.text);
            skuImage = (ImageView) view.findViewById(R.id.ic_sku_image);
        }
    }

    private ArrayList<OrderItem.OrderSKU> mList;
    private ArrayList<SuggestionSku> mSuggestionList;
    private OrderSKUInterface listener;
    private String itemId;

    public OrderSKUAdapter(ArrayList<OrderItem.OrderSKU> list, OrderSKUInterface skuInterface, String item) {
        this.mList = list;
        this.listener = skuInterface;
        this.itemId = item;
    }

    public OrderSKUAdapter(ArrayList<SuggestionSku> list, OrderSKUInterface skuInterface, String item, boolean isSuggestion) {
        this.mSuggestionList = list;
        this.listener = skuInterface;
        this.itemId = item;
    }

    @Override
    public int getItemCount() {

        if (mList != null)
            return mList.size();
        else
            return mSuggestionList.size();
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

        if (mList != null) {
            final OrderSKUHolder orderSKUHolder = (OrderSKUHolder) holder;
            final OrderItem.OrderSKU model = mList.get(position);

            if (!Utility.isEmpty(model.getSkuPackNumber()) && Integer.parseInt(model.getSkuPackNumber()) > 0)
                orderSKUHolder.mValue.setText(model.getSkuMeasurementValue() + " " + model.getSkuMeasurementAcronym() + " x " + model.getSkuPackNumber());
            else
                orderSKUHolder.mValue.setText(model.getSkuMeasurementValue() + " " + model.getSkuMeasurementAcronym());

            orderSKUHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onSKUSelected(model, itemId);
                }
            });
        } else {
            final OrderSKUHolder orderSKUHolder = (OrderSKUHolder) holder;
            final SuggestionSku model = mSuggestionList.get(position);

            orderSKUHolder.mValue.setText(model.getTitle());

            orderSKUHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onSKUSelected(model, itemId);
                }
            });

            Picasso.get()
                    .load(Constants.BASE_URL + "skus/" + model.getId() + "/images")
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(ContextCompat.getDrawable(orderSKUHolder.skuImage.getContext(), R.drawable.ic_placeholder_sku))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(orderSKUHolder.skuImage);
            orderSKUHolder.skuImage.setVisibility(View.VISIBLE);

        }

    }
}

