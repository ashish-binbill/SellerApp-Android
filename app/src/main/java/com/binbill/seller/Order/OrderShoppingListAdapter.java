package com.binbill.seller.Order;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/6/18.
 */

public class OrderShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OrderItemSelectedInterface {
        void onOrderItemQuantitySelected(int pos);

        void onItemInteraction(boolean enable);
    }

    public static class OrderShoppingListHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mItemName, mQuantity, mItemAvailability, mMeasurement;
        protected EditText mItemPrice, mAvailableQuantity;
        protected View mDivider;

        public OrderShoppingListHolder(View view) {
            super(view);
            mRootCard = view;
            mItemName = (TextView) view.findViewById(R.id.tv_item_name);
            mQuantity = (TextView) view.findViewById(R.id.tv_item_quantity);
            mMeasurement = (TextView) view.findViewById(R.id.tv_item_measurement);
            mItemPrice = (EditText) view.findViewById(R.id.et_item_price);
            mItemAvailability = (TextView) view.findViewById(R.id.tv_item_unavailable);
            mAvailableQuantity = (EditText) view.findViewById(R.id.et_quantity);
            mDivider = (View) view.findViewById(R.id.v_divider);
        }
    }

    private ArrayList<OrderItem> mList;
    private OrderItemSelectedInterface listener;
    private final boolean[] updateStateMap;
    private final int mStatus;
    private final boolean orderModified;

    public OrderShoppingListAdapter(ArrayList<OrderItem> list, OrderItemSelectedInterface object, boolean[] updateStates, int status, boolean isModified) {
        this.listener = object;
        this.mList = list;
        this.updateStateMap = updateStates;
        this.mStatus = status;
        this.orderModified = isModified;
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
        orderHolder.mQuantity.setText(model.getQuantity());
        orderHolder.mMeasurement.setText(skuModel.getSkuMeasurementValue() + " " + skuModel.getSkuMeasurementAcronym());

        if (!Utility.isEmpty(model.getUpdatedPrice()))
            orderHolder.mItemPrice.setText(model.getUpdatedPrice());
        else
            orderHolder.mItemPrice.setText("");

        if (model.isUpdateItemAvailable()) {
            orderHolder.mItemAvailability.setTag("1");
        } else {
            orderHolder.mItemAvailability.setTag("0");
        }

        updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);

        orderHolder.mItemAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean updateState = false;
                TextView textView = (TextView) view;
                String tag = (String) textView.getTag();
                if (tag.equalsIgnoreCase("1")) {

                    updateState = true;
                    model.setUpdateItemAvailable(false);
                    textView.setTag("0");
                    updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);

                    orderHolder.mAvailableQuantity.setText("");
                } else {

                    if (model.getUpdatedSKUMeasurement() != null) {
                        OrderItem.OrderSKU updatedSku = model.getUpdatedSKUMeasurement();
                        OrderItem.OrderSKU sku = model.getOrderSKU();
                        if (updatedSku.getSkuId() == null || updatedSku.getSkuId().equalsIgnoreCase(sku.getSkuId())) {
                            updateState = false;
                        } else
                            updateState = true;
                    } else
                        updateState = false;

                    model.setUpdateItemAvailable(true);
                    textView.setTag("1");
                    updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);
                }

                updateStateMap[position] = updateState;
                checkStateMap();
            }
        });

        if (model.getUpdatedSKUMeasurement() != null) {
            boolean updateState = false;
            OrderItem.OrderSKU sku = model.getUpdatedSKUMeasurement();
            orderHolder.mAvailableQuantity.setText(sku.getSkuMeasurementValue() + " " + sku.getSkuMeasurementAcronym());

            OrderItem.OrderSKU requestedSku = model.getOrderSKU();
            if (sku.getSkuId() == null || requestedSku.getSkuId().equalsIgnoreCase(sku.getSkuId())) {
                updateState = false;
            } else
                updateState = true;

            updateStateMap[position] = updateState;
            checkStateMap();
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

        orderHolder.mItemPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!Utility.isEmpty(editable.toString())) {
                    String newPrice = editable.toString();
                    model.setUpdatedPrice(newPrice.trim());
                }
            }
        });


        if (mStatus == Constants.STATUS_CANCEL || mStatus == Constants.STATUS_COMPLETE ||
                mStatus == Constants.STATUS_OUT_FOR_DELIVERY || mStatus == Constants.STATUS_REJECTED) {

            if(Utility.isEmpty(orderHolder.mItemPrice.getText().toString()))
                orderHolder.mItemPrice.setVisibility(View.GONE);
            else
                orderHolder.mItemPrice.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            int margin20 = Utility.convertDPtoPx(orderHolder.mItemPrice.getContext(), 20);
            int margin10 = Utility.convertDPtoPx(orderHolder.mItemPrice.getContext(), 10);
            int margin5 = Utility.convertDPtoPx(orderHolder.mItemPrice.getContext(), 5);
            layoutParams.setMargins(margin20, margin10, 0, 0);

            orderHolder.mItemPrice.setLayoutParams(layoutParams);
            orderHolder.mItemPrice.setPadding(margin5, margin5, margin5, margin5);
            orderHolder.mItemPrice.setFocusable(false);
            orderHolder.mItemPrice.setFocusableInTouchMode(false);
            orderHolder.mItemPrice.setBackground(null);
            orderHolder.mItemPrice.setTextColor(ContextCompat.getColor(orderHolder.mItemPrice.getContext(),
                    R.color.colorPrimary));
        }

        if (mStatus != Constants.STATUS_NEW_ORDER || orderModified) {
            orderHolder.mItemAvailability.setVisibility(View.GONE);
            orderHolder.mAvailableQuantity.setVisibility(View.GONE);

            orderHolder.mDivider.setVisibility(View.GONE);
        }
    }


    private void checkStateMap() {
        boolean enable = false;
        for (boolean state : updateStateMap)
            if (state) {
                enable = true;
                break;
            }

        if (listener != null)
            listener.onItemInteraction(enable);
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

    public boolean[] getUpdatedStateMap() {
        return updateStateMap;
    }

    public ArrayList<OrderItem> getUpdatedOrderList() {
        return mList;
    }
}
