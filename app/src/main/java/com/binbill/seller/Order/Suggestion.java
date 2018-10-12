package com.binbill.seller.Order;

import com.binbill.seller.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Suggestion implements Serializable {

    @SerializedName("title")
    String itemName;

    @SerializedName("measurement_value")
    String measuremenValue;

    @SerializedName("id")
    String itemId;

    @SerializedName("measurement_id")
    String measurementId;

   transient int suggestionStatus = Constants.SUGGESTION_STATUS_NO_SUGGESTION;

    public int getSuggestionStatus() {
        return suggestionStatus;
    }

    public void setSuggestionStatus(int suggestionStatus) {
        this.suggestionStatus = suggestionStatus;
    }

    public String getItemId() {
        return itemId;
    }

    public String getMeasurementId() {
        return measurementId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setMeasurementId(String measurementId) {
        this.measurementId = measurementId;
    }

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