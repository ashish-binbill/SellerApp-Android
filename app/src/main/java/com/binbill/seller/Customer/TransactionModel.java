package com.binbill.seller.Customer;

import com.binbill.seller.Model.JobCopy;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/5/18.
 */

public class TransactionModel implements Serializable {

    @SerializedName("id")
    String transactionId;

    @SerializedName("home_delivered")
    boolean homeDelivered;

    @SerializedName("cashback_status")
    String cashbackStatus;

    @SerializedName("copies")
    ArrayList<JobCopy> jobCopies;

    @SerializedName("amount_paid")
    String amount;

    @SerializedName("total_credits")
    String totalCredits;

    @SerializedName("redeemed_credits")
    String redeemedCredits;

    @SerializedName("total_loyalty")
    String totalLoyalty;

    @SerializedName("redeemed_loyalty")
    String redeemedLoyalty;

    @SerializedName("total_cashback")
    String totalCashback;

    @SerializedName("item_counts")
    String itemCount;

    @SerializedName("user_name")
    String userName;

    @SerializedName("created_at")
    String date;

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public boolean isHomeDelivered() {
        return homeDelivered;
    }

    public String getCashbackStatus() {
        return cashbackStatus;
    }

    public ArrayList<JobCopy> getJobCopies() {
        return jobCopies;
    }

    public String getAmount() {
        return amount;
    }

    public String getTotalCredits() {
        return totalCredits;
    }

    public String getRedeemedCredits() {
        return redeemedCredits;
    }

    public String getTotalLoyalty() {
        return totalLoyalty;
    }

    public String getRedeemedLoyalty() {
        return redeemedLoyalty;
    }

    public String getTotalCashback() {
        return totalCashback;
    }

    public String getItemCount() {
        return itemCount;
    }
}
