package com.binbill.seller.Customer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 9/3/18.
 */

public class CreditLoyaltyModel implements Serializable {

    @SerializedName("title")
    String title;

    @SerializedName("description")
    String description;

    @SerializedName("transaction_type")
    String transactionType;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    @SerializedName("amount")
    String amount;

    @SerializedName("created_at")
    String date;

    @SerializedName("id")
    String creditId;

    public String getCreditId() {
        return creditId;
    }

    transient boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
