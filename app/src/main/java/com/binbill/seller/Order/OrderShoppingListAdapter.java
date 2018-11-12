package com.binbill.seller.Order;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/6/18.
 */

public class OrderShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OrderItemSelectedInterface {
        void onOrderItemQuantitySelected(int pos);

        void onItemInteraction(boolean enable);

        void onItemAmountChanged();

        void onOrderItemQuantityDenominationSelected(int pos, String quantity, String setQuantity);

        void onSuggestionClicked(int pos);
    }

    public static class OrderShoppingListHolder extends RecyclerView.ViewHolder {
        private final ItemPriceEditTextListener itemPriceListener;
        private final ItemPriceSuggestionMeasurementTextListener itemPriceSuggestionMeasurementTextListener;
        protected View mRootCard;
        protected TextView mItemName, mQuantity, mItemAvailability, mMeasurement, mServiceName, mSKUPrice;
        protected EditText mItemPrice, mAvailableQuantity, mAvailableQuantityNewItem, mQuantityNumber, mAlternateItem;
        protected View mDivider;
        protected ImageView mSkuImage;
        protected ImageView mServiceImage;
        protected LinearLayout layoutService, layoutFMCG;

        public OrderShoppingListHolder(View view, ItemPriceEditTextListener itemPriceEditTextListener, ItemPriceSuggestionMeasurementTextListener itemPriceSuggestionMeasurementTextListener) {
            super(view);
            mRootCard = view;

            this.itemPriceListener = itemPriceEditTextListener;
            this.itemPriceSuggestionMeasurementTextListener = itemPriceSuggestionMeasurementTextListener;
            mItemName = (TextView) view.findViewById(R.id.tv_item_name);
            mQuantity = (TextView) view.findViewById(R.id.tv_item_quantity);
            mQuantityNumber = (EditText) view.findViewById(R.id.et_quantity_number);
            mMeasurement = (TextView) view.findViewById(R.id.tv_item_measurement);
            mItemPrice = (EditText) view.findViewById(R.id.et_item_price);
            mSkuImage = (ImageView) view.findViewById(R.id.ic_sku_image);
            mSKUPrice = (TextView) view.findViewById(R.id.tv_item_price);
            mItemAvailability = (TextView) view.findViewById(R.id.tv_item_unavailable);
            mAvailableQuantity = (EditText) view.findViewById(R.id.et_quantity);
            mAvailableQuantityNewItem = (EditText) view.findViewById(R.id.et_quantity_other_item);
            mDivider = (View) view.findViewById(R.id.v_divider);
            mAlternateItem = (EditText) view.findViewById(R.id.et_alternate_item);
            layoutService = (LinearLayout) view.findViewById(R.id.ll_service_order);
            layoutFMCG = (LinearLayout) view.findViewById(R.id.ll_fmcg_order);
            mServiceImage = (ImageView) view.findViewById(R.id.iv_service_image);
            mServiceName = (TextView) view.findViewById(R.id.tv_service_name);
            this.mItemPrice.addTextChangedListener(itemPriceEditTextListener);
            this.mAvailableQuantityNewItem.addTextChangedListener(itemPriceSuggestionMeasurementTextListener);
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
                mList.get(position).setUpdatedPrice(newPrice.trim());
            }

            if (listener != null)
                listener.onItemAmountChanged();
        }
    }

    private class ItemPriceSuggestionMeasurementTextListener implements TextWatcher {
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
                String measurement = editable.toString();
                Suggestion suggestion;
                if (mList.get(position).getSuggestion() != null)
                    suggestion = mList.get(position).getSuggestion();
                else
                    suggestion = new Suggestion();
                suggestion.setMeasuremenValue(measurement);
                mList.get(position).setSuggestion(suggestion);
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
        return new OrderShoppingListHolder(itemView, new ItemPriceEditTextListener(), new ItemPriceSuggestionMeasurementTextListener());
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final OrderShoppingListHolder orderHolder = (OrderShoppingListHolder) holder;
        final OrderItem model = mList.get(position);
        final OrderItem.OrderSKU skuModel = model.getOrderSKU();

        if (mOrderType.equalsIgnoreCase(Constants.ORDER_TYPE_FMCG)) {
            ((OrderShoppingListHolder) holder).itemPriceListener.updatePosition(holder.getAdapterPosition());
            ((OrderShoppingListHolder) holder).itemPriceSuggestionMeasurementTextListener.updatePosition(holder.getAdapterPosition());

            orderHolder.layoutService.setVisibility(View.GONE);
            orderHolder.layoutFMCG.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(Constants.BASE_URL + "skus/" + model.getItemId() + "/images")
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(ContextCompat.getDrawable(orderHolder.mSkuImage.getContext(), R.drawable.ic_placeholder_sku))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(orderHolder.mSkuImage);


            orderHolder.mItemName.setText(model.getItemTitle());
            orderHolder.mQuantity.setText(" x " + model.getQuantity() + " Nos.");

            if (skuModel != null) {
                if (!Utility.isEmpty(skuModel.getSkuPackNumber()) && Integer.parseInt(skuModel.getSkuPackNumber()) > 0)
                    orderHolder.mMeasurement.setText(skuModel.getSkuMeasurementValue() + " " + skuModel.getSkuMeasurementAcronym() + " x " + skuModel.getSkuPackNumber());
                else
                    orderHolder.mMeasurement.setText(skuModel.getSkuMeasurementValue() + " " + skuModel.getSkuMeasurementAcronym());
            } else if (model.getSuggestion() != null) {
                orderHolder.mMeasurement.setText(model.getSuggestion().getMeasuremenValue());
            } else
                orderHolder.mMeasurement.setText("");


            if (model.getUpdatedQuantityCount() != null) {
                orderHolder.mQuantityNumber.setText(model.getUpdatedQuantityCount() + " Nos.");

                if (model.getUpdatedQuantityCount().equalsIgnoreCase(model.getQuantity()))
                    updateStateMap[position] = false;
                else
                    updateStateMap[position] = true;
                checkStateMap();
            } else
                orderHolder.mQuantityNumber.setText(model.getQuantity() + " Nos.");

            /**
             * Any change in itemPrice, please do it in NA click listner as well.
             * Same code there
             */
            if (mStatus == Constants.STATUS_OUT_FOR_DELIVERY || mStatus == Constants.STATUS_COMPLETE) {
                if (!Utility.isEmpty(model.getSellingPrice()) && Utility.isValueNonZero(model.getSellingPrice()))
                    orderHolder.mItemPrice.setText(model.getSellingPrice());
                else
                    orderHolder.mItemPrice.setText("");
            } else {
                if (!Utility.isEmpty(model.getUpdatedPrice()) && Utility.isValueNonZero(model.getUpdatedPrice()))
                    orderHolder.mItemPrice.setText(model.getUpdatedPrice());
                else
                    orderHolder.mItemPrice.setText("");
            }

            if (model.getSuggestion() != null) {
                if (!Utility.isEmpty(model.getSuggestion().getSuggestionPrice()) && Utility.isValueNonZero(model.getSuggestion().getSuggestionPrice()))
                    orderHolder.mItemPrice.setText(model.getSuggestion().getSuggestionPrice());
                else
                    orderHolder.mItemPrice.setText("");
            }

            String rupee = orderHolder.mSKUPrice.getContext().getString(R.string.rupee_sign);
            if (!Utility.isEmpty(model.getSellingPrice()) && Utility.isValueNonZero(model.getSellingPrice()))
                orderHolder.mSKUPrice.setText(rupee + " " + model.getSellingPrice());
            else
                orderHolder.mSKUPrice.setText("");

            orderHolder.mSKUPrice.setVisibility(View.GONE);

            if (model.isUpdateItemAvailable()) {
                orderHolder.mItemAvailability.setTag("1");
                orderHolder.mAlternateItem.setVisibility(View.GONE);
                orderHolder.mAvailableQuantityNewItem.setVisibility(View.GONE);
                orderHolder.mAvailableQuantity.setVisibility(View.VISIBLE);
            } else {
                orderHolder.mAlternateItem.setVisibility(View.VISIBLE);
                enableDisableAlternateItemEditText(orderHolder.mAlternateItem, false);
                orderHolder.mItemAvailability.setTag("0");

                if (model.getSuggestion() != null && model.getSuggestion().getSuggestionStatus() == Constants.SUGGESTION_STATUS_NEW) {
                    orderHolder.mAvailableQuantityNewItem.setVisibility(View.VISIBLE);
                    orderHolder.mAvailableQuantity.setVisibility(View.GONE);
                } else {
                    orderHolder.mAvailableQuantityNewItem.setVisibility(View.GONE);
                    orderHolder.mAvailableQuantity.setVisibility(View.VISIBLE);
                }
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

                        /***
                         * BrandID check is to identify manual created items
                         * If item is manually created, suggestion need not be shown
                         *
                         * Update: Actually let them be shown, we wont show list
                         */

                        orderHolder.mAlternateItem.setVisibility(View.VISIBLE);
                        enableDisableAlternateItemEditText(orderHolder.mAlternateItem, false);

                        if (model.getSuggestion() != null && model.getSuggestion().getSuggestionStatus() == Constants.SUGGESTION_STATUS_NEW) {
                            orderHolder.mAvailableQuantityNewItem.setVisibility(View.VISIBLE);
                            orderHolder.mAvailableQuantity.setVisibility(View.GONE);
                        } else {
                            orderHolder.mAvailableQuantityNewItem.setVisibility(View.GONE);
                            orderHolder.mAvailableQuantity.setVisibility(View.VISIBLE);
                        }

                        updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);

                        orderHolder.mQuantityNumber.setText(model.getQuantity() + " Nos.");
                    } else {

                        model.setUpdatedQuantityCount(null);
                        model.setUpdatedSKUMeasurement(null);

                        if (model.getUpdatedSKUMeasurement() != null) {
                            OrderItem.OrderSKU updatedSku = model.getUpdatedSKUMeasurement();
                            OrderItem.OrderSKU sku = model.getOrderSKU();
                            if (updatedSku.getSkuId() == null || updatedSku.getSkuId().equalsIgnoreCase(sku.getSkuId())) {
                                updateState = false;
                            } else
                                updateState = true;
                        } else
                            updateState = false;

                        if (!updateState && model.getUpdatedQuantityCount() != null)
                            updateState = true;

                        model.setUpdateItemAvailable(true);
                        textView.setTag("1");


                        model.setSuggestion(null);
                        model.setUpdatedPrice(null);

                        orderHolder.mAlternateItem.setVisibility(View.GONE);
                        orderHolder.mAvailableQuantityNewItem.setVisibility(View.GONE);
                        orderHolder.mAvailableQuantity.setVisibility(View.VISIBLE);
                        updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);

                        if (model.getOrderSKU() != null) {
                            if (!Utility.isEmpty(model.getOrderSKU().getSkuPackNumber()) && Integer.parseInt(model.getOrderSKU().getSkuPackNumber()) > 0)
                                orderHolder.mAvailableQuantity.setText(model.getOrderSKU().getSkuMeasurementValue() + " " + model.getOrderSKU().getSkuMeasurementAcronym() + " x " + model.getOrderSKU().getSkuPackNumber());
                            else
                                orderHolder.mAvailableQuantity.setText(model.getOrderSKU().getSkuMeasurementValue() + " " + model.getOrderSKU().getSkuMeasurementAcronym());
                        } else
                            orderHolder.mAvailableQuantity.setText("");

                        orderHolder.mQuantityNumber.setText(model.getQuantity());
                        orderHolder.mAlternateItem.setText("");

                        if (mStatus == Constants.STATUS_OUT_FOR_DELIVERY || mStatus == Constants.STATUS_COMPLETE) {
                            if (!Utility.isEmpty(model.getSellingPrice()) && Utility.isValueNonZero(model.getSellingPrice()))
                                orderHolder.mItemPrice.setText(model.getSellingPrice());
                            else
                                orderHolder.mItemPrice.setText("");
                        } else {
                            if (!Utility.isEmpty(model.getUpdatedPrice()) && Utility.isValueNonZero(model.getUpdatedPrice()))
                                orderHolder.mItemPrice.setText(model.getUpdatedPrice());
                            else
                                orderHolder.mItemPrice.setText("");
                        }

                        if (model.getSuggestion() != null) {
                            if (!Utility.isEmpty(model.getSuggestion().getSuggestionPrice()) && Utility.isValueNonZero(model.getSuggestion().getSuggestionPrice()))
                                orderHolder.mItemPrice.setText(model.getSuggestion().getSuggestionPrice());
                            else
                                orderHolder.mItemPrice.setText("");
                        }

                    }

                    updateStateMap[position] = updateState;
                    checkStateMap();
                }
            });

            if (model.getUpdatedSKUMeasurement() != null) {
                boolean updateState = false;
                OrderItem.OrderSKU sku = model.getUpdatedSKUMeasurement();

                if (!Utility.isEmpty(sku.getSkuPackNumber()) && Integer.parseInt(sku.getSkuPackNumber()) > 0)
                    orderHolder.mAvailableQuantity.setText(sku.getSkuMeasurementValue() + " " + sku.getSkuMeasurementAcronym() + " x " + sku.getSkuPackNumber());
                else
                    orderHolder.mAvailableQuantity.setText(sku.getSkuMeasurementValue() + " " + sku.getSkuMeasurementAcronym());

                OrderItem.OrderSKU requestedSku = model.getOrderSKU();
                if (sku.getSkuId() == null || requestedSku.getSkuId().equalsIgnoreCase(sku.getSkuId())) {
                    updateState = false;
                } else
                    updateState = true;

                updateStateMap[position] = updateState;
                checkStateMap();
            } else {

                if (model.getOrderSKU() != null) {
                    if (!Utility.isEmpty(model.getOrderSKU().getSkuPackNumber()) && Integer.parseInt(model.getOrderSKU().getSkuPackNumber()) > 0)
                        orderHolder.mAvailableQuantity.setText(model.getOrderSKU().getSkuMeasurementValue() + " " + model.getOrderSKU().getSkuMeasurementAcronym() + " x " + model.getOrderSKU().getSkuPackNumber());
                    else
                        orderHolder.mAvailableQuantity.setText(model.getOrderSKU().getSkuMeasurementValue() + " " + model.getOrderSKU().getSkuMeasurementAcronym());
                } else
                    orderHolder.mAvailableQuantity.setText("");
            }


            if (model.getSuggestion() != null) {
                Suggestion suggestion = model.getSuggestion();
                if (suggestion.getSuggestionStatus() == Constants.SUGGESTION_STATUS_NEW) {
                    orderHolder.mAvailableQuantityNewItem.setVisibility(View.VISIBLE);
                    orderHolder.mAvailableQuantity.setVisibility(View.GONE);

                    orderHolder.mAvailableQuantityNewItem.setText(suggestion.getMeasuremenValue());
                } else {
                    orderHolder.mAvailableQuantityNewItem.setVisibility(View.GONE);
                    orderHolder.mAvailableQuantity.setVisibility(View.VISIBLE);

                    orderHolder.mAvailableQuantity.setText(suggestion.getMeasuremenValue());
                    /**
                     * Specially done for cases when order is already modified and sent to server,
                     * All Suggestion status gets lost due to this
                     */
                    orderHolder.mAvailableQuantityNewItem.setText(suggestion.getMeasuremenValue());
                }
                orderHolder.mAlternateItem.setText(suggestion.getItemName());
            }

            orderHolder.mAvailableQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (orderHolder.mAlternateItem.getVisibility() == View.VISIBLE
                            && Utility.isEmpty(orderHolder.mAlternateItem.getText().toString())) {
                        /**
                         * Do nothing here
                         */
                    } else {
                        if (listener != null)
                            listener.onOrderItemQuantitySelected(position);
                    }
                }
            });

            orderHolder.mAlternateItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onSuggestionClicked(position);
                }
            });

            orderHolder.mQuantityNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    model.setUpdateItemAvailable(true);
//                    orderHolder.mItemAvailability.setTag("1");
//                    orderHolder.mAlternateItem.setVisibility(View.GONE);
//                    updateItemAvailability(orderHolder.mItemAvailability.getContext(), orderHolder.mItemAvailability);

                    if (listener != null) {

                        if (model.getItemBrandId() != null && !Utility.isEmpty(model.getItemBrandId())) {

                            /**
                             * if Item is available and available quantity is equal to the requested quantity,
                             * send quantity requested string
                             */
                            if (orderHolder.mItemAvailability.getTag().toString().equalsIgnoreCase("1") &&
                                    orderHolder.mAvailableQuantity.getText().toString().contains(model.getOrderSKU().getSkuMeasurementValue())) {
                                listener.onOrderItemQuantityDenominationSelected(position, model.getQuantity(), model.getQuantity());
                            } else
                                listener.onOrderItemQuantityDenominationSelected(position, "", model.getQuantity());
                        }
                    }
                }
            });

            if (mStatus != Constants.STATUS_NEW_ORDER || orderModified) {

                if (orderModified && mStatus == Constants.STATUS_NEW_ORDER) {
                    orderHolder.mItemAvailability.setVisibility(View.VISIBLE);
                    orderHolder.mItemAvailability.setEnabled(false);

                    if (!model.isItemAvailability()) {
                        orderHolder.mItemAvailability.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_red_order));
                        orderHolder.mItemAvailability.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.color_white));
                    } else {
                        orderHolder.mItemAvailability.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_order));
                        orderHolder.mItemAvailability.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.text_77));
                    }

                } else
                    orderHolder.mItemAvailability.setVisibility(View.GONE);

                if (orderModified && mStatus == Constants.STATUS_NEW_ORDER && model.getUpdatedSKUMeasurement() != null) {
                    orderHolder.mAvailableQuantityNewItem.setVisibility(View.GONE);
                    orderHolder.mAlternateItem.setVisibility(View.GONE);
                    orderHolder.mAvailableQuantity.setVisibility(View.VISIBLE);
                    orderHolder.mAvailableQuantity.setEnabled(false);

                    orderHolder.mAvailableQuantity.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                    orderHolder.mAvailableQuantity.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.color_white));
                    orderHolder.mAvailableQuantity.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_red_order));

                } else {

                    if (model.getSuggestion() != null) {

                        if (orderModified && mStatus == Constants.STATUS_NEW_ORDER) {
                            orderHolder.mAlternateItem.setVisibility(View.VISIBLE);
                            orderHolder.mAlternateItem.setEnabled(false);
                            orderHolder.mAlternateItem.setFocusable(false);
                        } else
                            orderHolder.mAlternateItem.setVisibility(View.GONE);

                        orderHolder.mAvailableQuantity.setVisibility(View.GONE);
                        orderHolder.mAvailableQuantityNewItem.setVisibility(View.VISIBLE);
                        orderHolder.mAvailableQuantityNewItem.setEnabled(false);
                        orderHolder.mAvailableQuantityNewItem.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                        orderHolder.mAvailableQuantityNewItem.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.text_77));
                        orderHolder.mAvailableQuantityNewItem.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_order));
                    } else {
                        orderHolder.mAlternateItem.setVisibility(View.GONE);
                        orderHolder.mAvailableQuantityNewItem.setVisibility(View.GONE);
                        orderHolder.mAvailableQuantity.setVisibility(View.VISIBLE);
                        orderHolder.mAvailableQuantity.setEnabled(false);
                        orderHolder.mAvailableQuantity.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                        orderHolder.mAvailableQuantity.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.text_77));
                        orderHolder.mAvailableQuantity.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_order));
                    }
                }


                if (orderModified && mStatus == Constants.STATUS_NEW_ORDER && model.getUpdatedQuantityCount() != null) {
                    orderHolder.mQuantityNumber.setVisibility(View.VISIBLE);
                    orderHolder.mQuantityNumber.setEnabled(false);
                    orderHolder.mQuantityNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);


                    if (model.getSuggestion() != null) {
                        orderHolder.mQuantityNumber.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.text_77));
                        orderHolder.mQuantityNumber.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_order));
                    } else {
                        orderHolder.mQuantityNumber.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.color_white));
                        orderHolder.mQuantityNumber.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_red_order));
                    }

                } else {
                    orderHolder.mQuantityNumber.setVisibility(View.VISIBLE);
                    orderHolder.mQuantityNumber.setEnabled(false);
                    orderHolder.mQuantityNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                    orderHolder.mQuantityNumber.setTextColor(ContextCompat.getColor(orderHolder.mItemAvailability.getContext(), R.color.text_77));
                    orderHolder.mQuantityNumber.setBackground(ContextCompat.getDrawable(orderHolder.mItemAvailability.getContext(), R.drawable.edittext_bg_order));
                }

//                orderHolder.mDivider.setVisibility(View.GONE);
            }

            if (mStatus == Constants.STATUS_CANCEL || mStatus == Constants.STATUS_COMPLETE ||
                    mStatus == Constants.STATUS_OUT_FOR_DELIVERY || mStatus == Constants.STATUS_REJECTED ||
                    mStatus == Constants.STATUS_AUTO_CANCEL || mStatus == Constants.STATUS_AUTO_EXPIRED) {
                orderHolder.mItemPrice.setVisibility(View.GONE);

                orderHolder.mAvailableQuantity.setVisibility(View.GONE);
                orderHolder.mAvailableQuantityNewItem.setVisibility(View.GONE);
                orderHolder.mQuantityNumber.setVisibility(View.GONE);

                orderHolder.mSKUPrice.setVisibility(View.VISIBLE);

            }
        } else if (mOrderType.equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE)) {
            orderHolder.layoutService.setVisibility(View.VISIBLE);
            orderHolder.layoutFMCG.setVisibility(View.GONE);

            Picasso.get().load(Constants.BASE_URL + "assisted/" + model.getServiceTypeId() + "/images")
                    .config(Bitmap.Config.RGB_565)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(orderHolder.mServiceImage);

            orderHolder.mServiceName.setText(model.getServiceName());
        }


    }

    private void enableDisableAlternateItemEditText(EditText mAlternateItem, boolean isEnable) {
        mAlternateItem.setFocusable(isEnable);
        mAlternateItem.setFocusableInTouchMode(isEnable);
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
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.edittext_bg_red_order));
            view.setTextColor(ContextCompat.getColor(context, R.color.color_white));
        } else {
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.edittext_bg_order));
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
