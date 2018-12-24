package com.binbill.seller.Wallet;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class WalletTransaction implements Serializable{

    @SerializedName("id")
    String id;

    @SerializedName("seller_id")
    String sellerId;

    @SerializedName("title")
    String title;

    @SerializedName("job_id")
    String jobId;

    @SerializedName("user_id")
    String userId;

    @SerializedName("order_id")
    String orderId;

    @SerializedName("created_at")
    String date;

    @SerializedName("transaction_type")
    String transactionType;

    @SerializedName("cashback_source")
    String cashbackSource;

    @SerializedName("amount")
    String amount;

    @SerializedName("status_type")
    String statusType;

    @SerializedName("is_paytm")
    boolean paytm;

    @SerializedName("user_name")
    String userName;

    @SerializedName("mobile_no")
    String mobile;

    public String getMobile() {
        return mobile;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getTitle() {
        return title;
    }

    public String getJobId() {
        return jobId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getCashbackSource() {
        return cashbackSource;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatusType() {
        return statusType;
    }

    public boolean isPaytm() {
        return paytm;
    }

    public String getUserName() {
        return userName;
    }
}
