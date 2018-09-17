package com.binbill.seller.Verification;

import com.binbill.seller.Model.JobCopy;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/4/18.
 */

public  class VerificationModel implements Serializable {

    @SerializedName("id")
    String jobId;

    @SerializedName("home_delivered")
    boolean homeDelivered;

    @SerializedName("status_name")
    String cashbackStatus;

    @SerializedName("cashback_id")
    String cashbackId;

    @SerializedName("copies")
    ArrayList<JobCopy> jobCopyList;

    @SerializedName("amount_paid")
    String amount;

    @SerializedName("pending_cashback")
    String cashback;

    @SerializedName("item_counts")
    String itemCount;

    @SerializedName("is_pending")
    boolean pending;

    @SerializedName("user_name")
    String userName;

    @SerializedName("user_id")
    String userId;

    @SerializedName("total_credits")
    String totalCredits;

    @SerializedName("redeemed_credits")
    String redeemedCredits;

    @SerializedName("total_loyalty")
    String totalLoyalty;

    @SerializedName("redeemed_loyalty")
    String redeemedLoyalty;

    public String getRedeemedCredits() {
        return redeemedCredits;
    }

    public String getRedeemedLoyalty() {
        return redeemedLoyalty;
    }

    public String getTotalCredits() {
        return totalCredits;
    }

    public String getTotalLoyalty() {
        return totalLoyalty;
    }

    public String getCashbackId() {
        return cashbackId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getJobId() {
        return jobId;
    }

    public boolean isHomeDelivered() {
        return homeDelivered;
    }

    public String getCashbackStatus() {
        return cashbackStatus;
    }

    public ArrayList<JobCopy> getJobCopyList() {
        return jobCopyList;
    }

    public String getAmount() {
        return amount;
    }

    public String getCashback() {
        return cashback;
    }

    public String getItemCount() {
        return itemCount;
    }

    public boolean isPending() {
        return pending;
    }
}
