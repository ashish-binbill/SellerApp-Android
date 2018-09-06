package com.binbill.seller.Verification;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 9/4/18.
 */

public class RejectReasonModel implements Serializable {

    @SerializedName("id")
    String rejectId;

    @SerializedName("title")
    String title;

    transient boolean selected;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getTitle() {
        return title;
    }

    public String getRejectId() {
        return rejectId;
    }
}
