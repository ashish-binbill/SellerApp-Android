package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 8/28/18.
 */

public class DashboardModel implements Serializable {

    private static final int FMCG_ASSISTED_USER_POS = 0;
    private static final int FMCG_ASSISTED_USER_NO_POS = 1;
    private static final int FMCG_ONLY_USER_HAS_POS = 2;
    private static final int FMCG_ONLY_USER_NO_POS = 3;
    private static final int ASSISTED_ONLY_USER = 4;

    @SerializedName("total_transactions")
    String totalTransactionValue;

    @SerializedName("credit_pending")
    String creditPending;

    @SerializedName("forceUpdate")
    String forceUpdate;

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

    @SerializedName("has_pos")
    boolean hasPos;

    @SerializedName("assisted_count")
    String assistedUserCount;

    @SerializedName("user_cashback")
    String totalUserCashback;

    @SerializedName("rush_hours")
    boolean rushHour;

    @SerializedName("is_data_manually_added")
    boolean categoryBrandDataManuallyAdded;


    transient int sellerType;


    public String getForceUpdate() {
        return forceUpdate;
    }

    public boolean isCategoryBrandDataManuallyAdded() {
        return categoryBrandDataManuallyAdded;
    }

    public boolean isRushHour() {
        return rushHour;
    }

    public String getAssistedUserCount() {
        return assistedUserCount;
    }

    public String getTotalUserCashback() {
        return totalUserCashback;
    }

    public boolean isHasPos() {
        return hasPos;
    }


    public int getSellerType() {

        int sellerType = 0;
        if (this.isAssisted()) {
            if (this.isFmcg()) {
                if (this.isHasPos())
                    sellerType = FMCG_ASSISTED_USER_POS;
                else
                    sellerType = FMCG_ASSISTED_USER_NO_POS;
            } else
                sellerType = ASSISTED_ONLY_USER;
        } else {
            if (this.isFmcg()) {
                if (this.isHasPos())
                    sellerType = FMCG_ONLY_USER_HAS_POS;
                else
                    sellerType = FMCG_ONLY_USER_NO_POS;
            }
        }
        return this.sellerType = sellerType;

    }


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

