package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 8/28/18.
 */

public class DashboardModel implements Serializable {

    @SerializedName("total_transactions")
    String totalTransactionValue;

    @SerializedName("credit_pending")
    String creditPending;

    @SerializedName("loyalty_points")
    String loyaltyPoints;

    @SerializedName("consumer_counts")
    String consumerCount;

    @SerializedName("notification_count")
    String notificationCount;

    @SerializedName("is_assisted")
    boolean assisted;

    @SerializedName("is_fmcg")
    boolean fmcg;

    public boolean isAssisted() {
        return assisted;
    }

    public boolean isFmcg() {
        return fmcg;
    }

    public String getConsumerCount() {
        return consumerCount;
    }

    public String getCreditPending() {
        return creditPending;
    }

    public String getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public String getNotificationCount() {
        return notificationCount;
    }

    public String getTotalTransactionValue() {
        return totalTransactionValue;
    }
}

