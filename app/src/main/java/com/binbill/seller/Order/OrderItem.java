package com.binbill.seller.Order;

import com.binbill.seller.Model.MainCategory;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 9/5/18.
 */

public class OrderItem implements Serializable {

    @SerializedName("id")
    String itemId;

    @SerializedName("uid")
    String uid;

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

    @SerializedName("unit_price")
    String updatedPrice;

    @SerializedName("selling_price")
    String sellingPrice;

    @SerializedName("updated_measurement")
    OrderSKU updatedSKUMeasurement;

    @SerializedName("service_name")
    String serviceName;

    @SerializedName("updated_quantity")
    String updatedQuantityCount;

    /******************************************** TYPE_SERVICE ****************************************/
    /**
     *  "end_date": "2018-09-14T09:46:55.891Z",
     "base_price": 25,
     "start_date": "2018-09-14T09:42:47.579Z",
     "service_name": "Cook",
     "service_user": {
     "id": 17,
     "name": "Mr Sagar Chauhan"
     },
     "total_amount": 25,
     */
    @SerializedName("service_type_id")
    String serviceTypeId;

    @SerializedName("end_date")
    String endDate;

    @SerializedName("start_date")
    String startDate;

    @SerializedName("service_user")
    MainCategory serviceUser;

    @SerializedName("total_amount")
    String totalAmount;

    @SerializedName("suggestion")
    Suggestion suggestion;

    public String getUid() {
        return uid;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public MainCategory getServiceUser() {
        return serviceUser;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public String getServiceName() {
        return serviceName;
    }

    transient boolean updateItemAvailable = true;

    public String getUpdatedQuantityCount() {
        return updatedQuantityCount;
    }

    public void setUpdatedQuantityCount(String updatedQuantityCount) {
        this.updatedQuantityCount = updatedQuantityCount;
    }

    public String getUpdatedPrice() {
        return updatedPrice;
    }

    public void setUpdatedPrice(String updatedPrice) {
        this.updatedPrice = updatedPrice;
    }

    public String getSellingPrice(){
        return sellingPrice;
    }

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

    public void setOrderSKU(OrderSKU orderSKU) {
        this.orderSKU = orderSKU;
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

        public void setSkuMrp(String skuMrp) {
            this.skuMrp = skuMrp;
        }

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
