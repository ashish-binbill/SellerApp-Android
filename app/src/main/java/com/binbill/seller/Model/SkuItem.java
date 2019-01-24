package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class SkuItem implements Serializable {

    @SerializedName("title")
    String title;

    @SerializedName("id")
    String id;

     @SerializedName("sku_measurements")
     ArrayList<SkuMeasurement> skuMeasurements;

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public ArrayList<SkuMeasurement> getSkuMeasurements() {
        return skuMeasurements;
    }
}
