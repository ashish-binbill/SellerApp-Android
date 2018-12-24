package com.binbill.seller.Order;

import com.binbill.seller.AssistedService.AssistedUserModel;
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

    @SerializedName("user_address_id")
    String userAddress;

    @SerializedName("user")
    UserModel user;

    @SerializedName("is_modified")
    boolean modified;

    @SerializedName("order_item_counts")
    String itemCount;

    @SerializedName("collect_at_store")
    boolean collectAtStore;

    @SerializedName("order_details")
    ArrayList<OrderItem> orderItems;

    @SerializedName("created_at")
    String orderCreationDate;

    @SerializedName("total_amount")
    String totalAmount;

    @SerializedName("seller_discount")
    String sellerDiscount;

    @SerializedName("user_address_detail")
    String address;

    @SerializedName("delivery_user")
    DeliveryModel deliveryUser;

    @SerializedName("seller_review")
    AssistedUserModel.Review sellerReview;

    @SerializedName("delivery_review")
    AssistedUserModel.Review deliveryReview;

    @SerializedName("payment_mode_id")
    int paymentModeId;

    @SerializedName("remaining_seconds")
    long remainingSeconds;

    @SerializedName("delivery_minutes")
    String deliveryMinutes;

    @SerializedName("auto_cancel_remaining_seconds")
    long autoCancelRemainingSeconds;

    @SerializedName("response_information")
    String responseInformation;

    public String getResponseInformation() {
        return responseInformation;
    }

    public String getSellerDiscount() {
        return sellerDiscount;
    }

    public long getAutoCancelRemainingSeconds() {
        return autoCancelRemainingSeconds;
    }

    public String getDeliveryMinutes() {
        return deliveryMinutes;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    public boolean isCollectAtStore() {
        return collectAtStore;
    }

    public int getPaymentModeId() {
        return paymentModeId;
    }

    public AssistedUserModel.Review getSellerReview() {
        return sellerReview;
    }

    public AssistedUserModel.Review getDeliveryReview() {
        return deliveryReview;
    }

    public DeliveryModel getDeliveryUser() {
        return deliveryUser;
    }

    public String getItemCount() {
        return itemCount;
    }

    public String getAddress() {
        return address;
    }

    public boolean isModified() {
        return modified;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

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

    public String getUserAddress() {
        return userAddress;
    }
}
