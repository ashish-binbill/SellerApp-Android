package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SkuMeasurement implements Serializable {

    @SerializedName("id")
    String id;

    @SerializedName("measurement_value")
    String measurementValue;

    @SerializedName("mrp")
    String mrp;

    @SerializedName("measurement_acronym")
    String measurementAcronym;

    @SerializedName("offer_discount")
    String offerDiscount;

    @SerializedName("sku_id")
    String sku_id;

    public String getId() {
        return id;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public String getMrp() {
        return mrp;
    }

    public String getMeasurementAcronym() {
        return measurementAcronym;
    }

    public String getOfferDiscount() {
        return offerDiscount;
    }

    public String getSku_id() {
        return sku_id;
    }

}
