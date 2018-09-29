package com.binbill.seller.Order;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.R;
import com.binbill.seller.Utility;
import com.squareup.picasso.Picasso;

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
        private final ItemPriceEditTextListener itemPriceListener;
        protected View mRootCard;
        protected TextView mItemName, mQuantity, mItemAvailability, mMeasurement, mServiceName;
        protected EditText mItemPrice, mAvailableQuantity;
        protected View mDivider;
        protected ImageView mServiceImage;
        protected LinearLayout layoutService, layoutFMCG;

        public OrderShoppingListHolder(View view, ItemPriceEditTextListener itemPriceEditTextListener) {
            super(view);
            mRootCard = view;

            this.itemPriceListener = itemPriceEditTextListener;
            mItemName = (TextView) view.findViewById(R.id.tv_item_name);
            mQuantity = (TextView) view.findViewById(R.id.tv_item_quantity);
            mMeasurement = (TextView) view.findViewById(R.id.tv_item_measurement);
            mItemPrice = (EditText) view.findViewById(R.id.et_item_price);
            mItemAvailability = (TextView) view.findViewById(R.id.tv_item_unavailable);
            mAvailableQuantity = (EditText) view.findViewById(R.id.et_quantity);
            mDivider = (View) view.findViewById(R.id.v_divider);
            layoutService = (LinearLayout) view.findViewById(R.id.ll_service_order);
            layoutFMCG = (LinearLayout) view.findViewById(R.id.ll_fmcg_order);
            mServiceImage = (ImageView) view.findViewById(R.id.iv_service_image);
            mServiceName = (TextView) view.findViewById(R.id.tv_service_name);
            this.mItemPrice.addTextChangedListener(itemPriceEditTextListener);
        }
    }

    private class ItemPriceEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (!Utility.isEmpty(editable.toString())) {
                String newPrice = editable.toString();

                Log.d("SHRUTI", "Value updated " + position + " Amount " + newPrice.trim());
                mList.get(position).setUpdatedPrice(newPrice.trim());
//                        model.setUpdatedPrice(newPrice.trim());
            }
        }
    }

    private ArrayList<OrderItem> mList;
    private OrderItemSelectedInterface listener;
    private final boolean[] updateStateMap;
    private final int mStatus;
    private final boolean orderModified;
    private String mOrderType;

    public OrderShoppingListAdapter(String orderType, ArrayList<OrderItem> list, OrderItemSelectedInterface object, boolean[] updateStates, int status, boolean isModified) {
        this.listener = object;
        this.mList = list;
        this.mOrderType = orderType;
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
        return new OrderShoppingListHolder(itemView, new ItemPriceEditTextListener());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final OrderShoppingListHolder orderHolder = (OrderShoppingListHolder) holder;
        final OrderItem model = mList.get(position);
        final OrderItem.OrderSKU skuModel = model.getOrderSKU();

        if (mOrderType.equalsIgnoreCase(Constants.ORDER_TYPE_FMCG)) {
            ((OrderShoppingListHolder) holder).itemPriceListener.updatePosition(holder.getAdapterPosition());

            orderHolder.layoutService.setVisibility(View.GONE);
            orderHolder.layoutFMCG.setVisibility(View.VISIBLE);

            orderHolder.mItemName.setText(model.getItemTitle());
            orderHolder.mQuantity.setText(model.getQuantity());

            if (skuModel != null)
                orderHolder.mMeasurement.setText(skuModel.getSkuMeasurementValue() + " " + skuModel.getSkuMeasurementAcronym());
            else
                orderHolder.mMeasurement.setText("");

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

                        if (model.getOrderSKU() != null)
                            orderHolder.mAvailableQuantity.setText(model.getOrderSKU().getSkuMeasurementValue() + " " + model.getOrderSKU().getSkuMeasurementAcronym());
                        else
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
            } else {

                if (model.getOrderSKU() != null)
                    orderHolder.mAvailableQuantity.setText(model.getOrderSKU().getSkuMeasurementValue() + " " + model.getOrderSKU().getSkuMeasurementAcronym());
                else
                    orderHolder.mAvailableQuantity.setText("");
            }

            orderHolder.mAvailableQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (skuModel != null) {
                        model.setUpdateItemAvailable(true);
                        orderHolder.mItemAvailability.setTag("1");
                        updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);

                        if (listener != null)
                            listener.onOrderItemQuantitySelected(position);
                    }
                }
            });

            if (mStatus == Constants.STATUS_CANCEL || mStatus == Constants.STATUS_COMPLETE ||
                    mStatus == Constants.STATUS_OUT_FOR_DELIVERY || mStatus == Constants.STATUS_REJECTED) {

                if (Utility.isEmpty(orderHolder.mItemPrice.getText().toString()))
                    orderHolder.mItemPrice.setVisibility(View.GONE);
                else
                    orderHolder.mItemPrice.setVisibility(View.VISIBLE);

                orderHolder.mItemPrice.setFocusable(false);
                orderHolder.mItemPrice.setFocusableInTouchMode(false);
                orderHolder.mItemPrice.setBackground(null);
                orderHolder.mItemPrice.setTextColor(ContextCompat.getColor(orderHolder.mItemPrice.getContext(),
                        R.color.colorPrimary));
            }

            if (mStatus != Constants.STATUS_NEW_ORDER || orderModified) {

                if (orderModified && mStatus == Constants.STATUS_NEW_ORDER) {
                    orderHolder.mItemAvailability.setVisibility(View.VISIBLE);
                    orderHolder.mItemAvailability.setEnabled(false);

                    if (!model.isItemAvailability()) {
                        orderHolder.mItemAvailability.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_red));
                        orderHolder.mItemAvailability.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.color_white));
                    } else {
                        orderHolder.mItemAvailability.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg));
                        orderHolder.mItemAvailability.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.text_77));
                    }

                } else
                    orderHolder.mItemAvailability.setVisibility(View.GONE);

                if (orderModified && mStatus == Constants.STATUS_NEW_ORDER && model.getUpdatedSKUMeasurement() != null) {

                    orderHolder.mAvailableQuantity.setVisibility(View.VISIBLE);
                    orderHolder.mAvailableQuantity.setEnabled(false);
                    orderHolder.mAvailableQuantity.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.color_white));
                    orderHolder.mAvailableQuantity.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_red));

                } else {
                    orderHolder.mAvailableQuantity.setVisibility(View.VISIBLE);
                    orderHolder.mAvailableQuantity.setEnabled(false);
                    orderHolder.mAvailableQuantity.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.text_77));
                    orderHolder.mAvailableQuantity.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg));
                }

                orderHolder.mDivider.setVisibility(View.GONE);
            }
        } else if (mOrderType.equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE)) {
            orderHolder.layoutService.setVisibility(View.VISIBLE);
            orderHolder.layoutFMCG.setVisibility(View.GONE);

            Picasso.get().load(Constants.BASE_URL + "assisted/" + model.getServiceTypeId() + "/images")
                    .config(Bitmap.Config.RGB_565)
                    .into(orderHolder.mServiceImage);

            orderHolder.mServiceName.setText(model.getServiceName());
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

    public void refreshEvents(ArrayList<OrderItem> itemList) {
        mList = itemList;
        notifyDataSetChanged();
    }

    public boolean[] getUpdatedStateMap() {
        return updateStateMap;
    }

    public ArrayList<OrderItem> getUpdatedOrderList() {
        return mList;
    }
}
