package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

public class CreditLoyaltyDashboardModel {
    /**
     * {
     "user_id": 27771,
     "transaction_type": 1,
     "total_credit": 2800,
     "address": {},
     "name": "Arpit ",
     "image_name": "active-27773-1530540475861.jpeg",
     "mobile_no": "9694833641",
     "email": "arpit.gupta@binbill.com",
     "user_address_detail": ","

     }
     */

    @SerializedName("total_credit")
    String totalCredit;

    @SerializedName("id")
    String userId;

    @SerializedName("total_transactions")
    String totalTransactions;

    @SerializedName("total_points")
    String totalPoints;

    @SerializedName("transaction_type")
    String transactionType;

    @SerializedName("name")
    String name;

    @SerializedName("mobile_no")
    String mobile;

    @SerializedName("email")
    String email;

    @SerializedName("image_name")
    String image;

    @SerializedName("user_address_detail")
    String address;

    @SerializedName("cashback_total")
    String totalCashback;

    public String getTotalTransactions() {
        return totalTransactions;
    }

    public String getTotalCashback() {
        return totalCashback;
    }

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

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getUserId() {
        return userId;
    }
}
