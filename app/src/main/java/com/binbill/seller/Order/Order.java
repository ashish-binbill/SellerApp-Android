package com.binbill.seller.Order;

import com.binbill.seller.Model.UserModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/5/18.
 */

public class Order implements Serializable {

    @SerializedName("id")
    String orderId;

    @SerializedName("user_id")
    String userId;

    @SerializedName("order_type")
    String orderType;

    @SerializedName("status_type")
    int orderStatus;

    @SerializedName("user")
    UserModel user;

    @SerializedName("order_details")
    ArrayList<OrderItem> orderItems;

    @SerializedName("created_at")
    String orderCreationDate;

    public String getOrderCreationDate() {
        return orderCreationDate;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public UserModel getUser() {
        return user;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderType() {
        return orderType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }
}
