package com.binbill.seller.Order;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/6/18.
 */

public class OrderShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OrderItemSelectedInterface {
        void onOrderItemQuantitySelected(int pos);
    }

    public static class OrderShoppingListHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mItemName, mQuantity, mItemAvailability, mMeasurement;
        protected EditText mItemPrice, mAvailableQuantity;

        public OrderShoppingListHolder(View view) {
            super(view);
            mRootCard = view;
            mItemName = (TextView) view.findViewById(R.id.tv_item_name);
            mQuantity = (TextView) view.findViewById(R.id.tv_item_quantity);
            mMeasurement = (TextView) view.findViewById(R.id.tv_item_measurement);
            mItemPrice = (EditText) view.findViewById(R.id.et_item_price);
            mItemAvailability = (TextView) view.findViewById(R.id.tv_item_unavailable);
            mAvailableQuantity = (EditText) view.findViewById(R.id.et_quantity);
        }
    }

    private ArrayList<OrderItem> mList;
    private OrderItemSelectedInterface listener;

    public OrderShoppingListAdapter(ArrayList<OrderItem> list, OrderItemSelectedInterface object) {
        this.listener = object;
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
                inflate(R.layout.row_order_shopping_list_item, parent, false);
        return new OrderShoppingListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final OrderShoppingListHolder orderHolder = (OrderShoppingListHolder) holder;
        final OrderItem model = mList.get(position);
        final OrderItem.OrderSKU skuModel = model.getOrderSKU();


        orderHolder.mItemName.setText(model.getItemTitle());
        orderHolder.mQuantity.setText("x " + model.getQuantity());
        orderHolder.mMeasurement.setText(skuModel.getSkuMeasurementValue() + " " + skuModel.getSkuMeasurementAcronym());

        if (model.isUpdateItemAvailable()) {
            orderHolder.mItemAvailability.setTag("1");
        } else {
            orderHolder.mItemAvailability.setTag("0");
        }

        updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);

        orderHolder.mItemAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) view;
                String tag = (String) textView.getTag();
                if (tag.equalsIgnoreCase("1")) {
                    model.setUpdateItemAvailable(false);
                    textView.setTag("0");
                    updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);
                } else {
                    model.setUpdateItemAvailable(true);
                    textView.setTag("1");
                    updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);
                }
            }
        });

        if (model.getUpdatedSKUMeasurement() != null) {
            OrderItem.OrderSKU sku = model.getUpdatedSKUMeasurement();
            orderHolder.mAvailableQuantity.setText(sku.getSkuMeasurementValue() + " " + sku.getSkuMeasurementAcronym());
        }

        orderHolder.mAvailableQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                model.setUpdateItemAvailable(true);
                orderHolder.mItemAvailability.setTag("1");
                updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);

                if (listener != null)
                    listener.onOrderItemQuantitySelected(position);
            }
        });
    }

    private void updateItemAvailability(Context context, TextView view) {
        String tag = (String) view.getTag();
        if (tag.equalsIgnoreCase("0")) {
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.edittext_bg_red));
            view.setTextColor(ContextCompat.getColor(context, R.color.color_white));
        } else {
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.edittext_bg));
            view.setTextColor(ContextCompat.getColor(context, R.color.text_77));
        }
    }

    public void refreshEvents() {
        notifyDataSetChanged();
    }
}
