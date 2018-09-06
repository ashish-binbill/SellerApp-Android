package com.binbill.seller.Order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 9/5/18.
 */

public class OrderItem implements Serializable {

    @SerializedName("id")
    String itemId;

    @SerializedName("title")
    String itemTitle;

    @SerializedName("brand_id")
    String itemBrandId;

    @SerializedName("quantity")
    String quantity;

    @SerializedName("category_id")
    String itemCategoryId;

    @SerializedName("sub_category_id")
    String itemSubCategoryId;

    @SerializedName("main_category_id")
    String itemMainCategoryId;

    @SerializedName("item_availability")
    boolean itemAvailability;

    @SerializedName("sku_measurement")
    OrderSKU orderSKU;

    transient OrderSKU updatedSKUMeasurement;
    transient boolean updateItemAvailable = true;

    public boolean isUpdateItemAvailable() {
        return updateItemAvailable;
    }

    public void setUpdateItemAvailable(boolean isIt) {
        updateItemAvailable = isIt;
    }

    public OrderSKU getUpdatedSKUMeasurement() {
        return updatedSKUMeasurement;
    }

    public void setUpdatedSKUMeasurement(OrderSKU updatedSKUMeasurement) {
        this.updatedSKUMeasurement = updatedSKUMeasurement;
    }

    public void setItemAvailability(boolean itemAvailability) {
        this.itemAvailability = itemAvailability;
    }

    public OrderSKU getOrderSKU() {
        return orderSKU;
    }

    public class OrderSKU implements Serializable {

        @SerializedName("id")
        String skuId;

        @SerializedName("mrp")
        String skuMrp;

        @SerializedName("bar_code")
        String skuBarCode;

        @SerializedName("pack_numbers")
        String skuPackNumber;

        @SerializedName("cashback_percent")
        String cashBackPercent;

        @SerializedName("measurement_type")
        String skuMeasurementType;

        @SerializedName("measurement_value")
        String skuMeasurementValue;

        @SerializedName("measurement_acronym")
        String skuMeasurementAcronym;

        public String getSkuId() {
            return skuId;
        }

        public String getSkuMrp() {
            return skuMrp;
        }

        public String getSkuBarCode() {
            return skuBarCode;
        }

        public String getSkuPackNumber() {
            return skuPackNumber;
        }

        public String getCashBackPercent() {
            return cashBackPercent;
        }

        public String getSkuMeasurementType() {
            return skuMeasurementType;
        }

        public String getSkuMeasurementValue() {
            return skuMeasurementValue;
        }

        public String getSkuMeasurementAcronym() {
            return skuMeasurementAcronym;
        }
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemBrandId() {
        return itemBrandId;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getItemCategoryId() {
        return itemCategoryId;
    }

    public String getItemSubCategoryId() {
        return itemSubCategoryId;
    }

    public String getItemMainCategoryId() {
        return itemMainCategoryId;
    }

    public boolean isItemAvailability() {
        return itemAvailability;
    }
}
