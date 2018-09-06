package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/23/18.
 */

public class BusinessDetailsModel implements Serializable {

    @SerializedName("id")
    String businessId;

    @SerializedName("title")
    String businessName;

    @SerializedName("image_types")
    ArrayList<MainCategory> businessTypes;

    public ArrayList<MainCategory> getBusinessTypes() {
        return businessTypes;
    }

    public String getBusinessId() {
        return businessId;
    }

    public String getBusinessName() {
        return businessName;
    }
}
