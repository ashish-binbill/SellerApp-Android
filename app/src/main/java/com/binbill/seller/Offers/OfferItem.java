package com.binbill.seller.Offers;

import com.binbill.seller.Model.FileItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/29/18.
 */

public class OfferItem implements Serializable {

    /**
     * {
     * "id":2,
     * "offer_type":1,
     * "title":null,
     * "description":null,
     * "sku_measurement_type":1,
     * "start_date":"2018-12-08",
     * "end_date":"2019-01-01",
     * "sku_id":2099,
     * "brand_id":2851,
     * "brand_mrp":null,
     * "excluded_seller_ids":null,
     * "sku_measurement_id":2429,
     * "offer_value":20,
     * "has_image":false,
     * "sku_title":"Act II Classic Salted",
     * "brand_title":"Act Ii",
     * "measurement_value":120,
     * "measurement_acronym":"gm",
     * "acronym":"gm",
     * "mrp":30,
     * "bar_code":"8901512564306"
     * }
     */
    @SerializedName("id")
    String offerId;

    @SerializedName("offer_type")
    String offerType;

    @SerializedName("title")
    String offerTitle;

    @SerializedName("description")
    String offerDescription;

    @SerializedName("sku_title")
    String skuTitle;

    @SerializedName("brand_title")
    String brandTitle;

    @SerializedName("measurement_value")
    String measurementValue;

    @SerializedName("acronym")
    String acronym;

    @SerializedName("mrp")
    String mrp;

    @SerializedName("sku_measurement_type")
    String skuMeasurementType;

    @SerializedName("brand_id")
    String brandId;

    @SerializedName("end_date")
    String offerEndDate;

    @SerializedName("document_details")
    ArrayList<FileItem> offerFiles;

    @SerializedName("sku")
    OfferSku sku;

    @SerializedName("sku_id")
    String skuId;

    @SerializedName("has_seller_offer")
    boolean offerAdded;

    @SerializedName("sku_measurement_id")
    String skuMeasurementId;

    @SerializedName("offer_discount")
    String offerDiscount;

    @SerializedName("offer_value")
    String offerValue;

    @SerializedName("bar_code")
    String barCodeSuggestedOffer;

    @SerializedName("brand_offer_id")
    String brandOfferId;

    public String getBrandOfferId() {
        return brandOfferId;
    }

    public String getBarCodeSuggestedOffer() {
        return barCodeSuggestedOffer;
    }

    public void setOfferEndDate(String offerEndDate) {
        this.offerEndDate = offerEndDate;
    }

    public boolean isOfferAdded() {
        return offerAdded;
    }

    public String getOfferType() {
        return offerType;
    }

    public String getSkuTitle() {
        return skuTitle;
    }

    public String getBrandTitle() {
        return brandTitle;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public String getAcronym() {
        return acronym;
    }

    public String getMrp() {
        return mrp;
    }

    public String getSkuMeasurementType() {
        return skuMeasurementType;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getOfferValue() {
        return offerValue;
    }

    public ArrayList<FileItem> getOfferFiles() {
        return offerFiles;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public String getOfferEndDate() {
        return offerEndDate;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public String getOfferId() {
        return offerId;
    }

    public OfferSku getSku() {
        return sku;
    }

    public String getSkuId() {
        return skuId;
    }

    public String getSkuMeasurementId() {
        return skuMeasurementId;
    }

    public String getOfferDiscount() {
        return offerDiscount;
    }

    public class OfferSku implements Serializable {

        @SerializedName("sku_title")
        String skuTitle;

        @SerializedName("measurement_value")
        String measurementValue;

        @SerializedName("acronym")
        String acronym;

        @SerializedName("mrp")
        String mrp;

        @SerializedName("bar_code")
        String barCode;

        public String getSkuTitle() {
            return skuTitle;
        }

        public String getMeasurementValue() {
            return measurementValue;
        }

        public String getAcronym() {
            return acronym;
        }

        public String getMrp() {
            return mrp;
        }

        public String getBarCode() {
            return barCode;
        }
    }
}
