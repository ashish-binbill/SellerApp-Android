package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

public class CreditLoyaltyDashboardModel {

    @SerializedName("total_credit")
    String totalCredit;

    @SerializedName("user_id")
    String userId;

    @SerializedName("total_points")
    String totalPoints;

    @SerializedName("transaction_type")
    String transactionType;

    @SerializedName("user")
    UserModel user;

    @SerializedName("user_address_detail")
    String address;

    public String getAddress() {
        return address;
    }

    public String getTotalPoints() {
        return totalPoints;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTotalCredit() {
        return totalCredit;
    }

    public UserModel getUser() {
        return user;
    }

    public String getUserId() {
        return userId;
    }
}
