package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 8/30/18.
 */

public class UserModel implements Serializable {

    /**
     * "id": 27774,
     "name": "Pramjeet",
     "mobile_no": "8750568036",
     "email": null,
     "email_verified": false,
     "email_secret": null,
     "location": null,
     "latitude": null,
     "longitude": null,
     "image_name": "active-27774-1535711045036.jpeg",
     "gender": null,
     "imageUrl": "/consumer/27774/images/1535711045036",
     "wallet_value": 0,
     "addresses": [],
     "hasPin": false
     */

    @SerializedName("name")
    String userName;

    @SerializedName("email")
    String userEmail;

    @SerializedName("image_name")
    String userImage;

    @SerializedName("mobile_no")
    String userMobile;

    @SerializedName("latitude")
    String userLatitude;

    @SerializedName("longitude")
    String userLongitude;

    @SerializedName("location")
    String userLocation;

    @SerializedName("gender")
    String gender;

    @SerializedName("wallet_value")
    String walletValue;

    @SerializedName("id")
    String userId;

    @SerializedName("user_status_type")
    String userStatusType;

    @SerializedName("cashback_total")
    String userCashback;

    @SerializedName("redeemed_cashback")
    String userRedeemedCashback;

    @SerializedName("loyalty_total")
    String userLoyalty;

    @SerializedName("redeemed_loyalty")
    String userRedeemedLoyalty;

    @SerializedName("credit_total")
    String userCredit;

    @SerializedName("redeemed_credits")
    String userRedeemedCredit;

    @SerializedName("transaction_counts")
    String transactionCount;

    @SerializedName("linked")
    boolean linked;

    @SerializedName("distance")
    String userDistance;

    transient boolean isSelected = false;

    public String getUserDistance() {
        return userDistance;
    }

    public String getUserStatusType() {
        return userStatusType;
    }

    public String getUserImage() {
        return userImage;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserCashback() {
        return userCashback;
    }

    public String getUserRedeemedCashback() {
        return userRedeemedCashback;
    }

    public String getUserLoyalty() {
        return userLoyalty;
    }

    public String getUserRedeemedLoyalty() {
        return userRedeemedLoyalty;
    }

    public String getUserCredit() {
        return userCredit;
    }

    public String getUserRedeemedCredit() {
        return userRedeemedCredit;
    }

    public String getTransactionCount() {
        return transactionCount;
    }

    public boolean isLinked() {
        return linked;
    }

    public String getUserLatitude() {
        return userLatitude;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public String getGender() {
        return gender;
    }

    public String getWalletValue() {
        return walletValue;
    }
}
