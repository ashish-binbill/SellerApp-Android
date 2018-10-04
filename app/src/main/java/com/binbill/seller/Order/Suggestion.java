package com.binbill.seller.Order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Suggestion implements Serializable {

    @SerializedName("title")
    String itemName;

    @SerializedName("measurement_value")
    String measuremenValue;

    public String getItemName() {
        return itemName;
    }

    public String getMeasuremenValue() {
        return measuremenValue;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setMeasuremenValue(String measuremenValue) {
        this.measuremenValue = measuremenValue;
    }
}