package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SellerDeliveryModel implements Serializable {

    @SerializedName("id")
    String id;
    @SerializedName("minimum_order_value")
    String minimum_order_value;
    @SerializedName("maximum_order_value")
    String maximum_order_value;
    @SerializedName("delivery_charges")
    String delivery_charges;
    @SerializedName("title")
    String title;
    @SerializedName("status_type")
    String status_type;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMinimum_order_value() {
        return minimum_order_value;
    }

    public void setMinimum_order_value(String minimum_order_value) {
        this.minimum_order_value = minimum_order_value;
    }

    public String getMaximum_order_value() {
        return maximum_order_value;
    }

    public void setMaximum_order_value(String maximum_order_value) {
        this.maximum_order_value = maximum_order_value;
    }

    public String getDelivery_charges() {
        return delivery_charges;
    }

    public void setDelivery_charges(String delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus_type() {
        return status_type;
    }

    public void setStatus_type(String status_type) {
        this.status_type = status_type;
    }
}
