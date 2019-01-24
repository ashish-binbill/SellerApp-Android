package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SkuMeasurement implements Serializable {

    @SerializedName("id")
    String id;

    @SerializedName("measurement_value")
    String measurementValue;

    @SerializedName("title")
    String title;

    @SerializedName("main_category_id")
    String main_category_id;

    @SerializedName("mrp")
    String mrp;

    @SerializedName("measurement_acronym")
    String measurementAcronym;

    @SerializedName("offer_discount")
    String offerDiscount;

    @SerializedName("sku_id")
    String sku_id;


    @SerializedName("id_main")
    String id_main;

    public String getId() {
        return id;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public String getId_main() {
        return id_main;
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

    public String getTitle() {
        return title;
    }

    public String getMain_category_id() {
        return main_category_id;
    }

}
