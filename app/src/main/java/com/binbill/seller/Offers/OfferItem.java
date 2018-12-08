package com.binbill.seller.Offers;

import com.binbill.seller.Model.FileItem;
import com.binbill.seller.Order.OrderItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/29/18.
 */

public class OfferItem implements Serializable {

    @SerializedName("id")
    String offerId;
    @SerializedName("title")
    String offerTitle;
    @SerializedName("description")
    String offerDescription;
    @SerializedName("end_date")
    String offerEndDate;

    @SerializedName("document_details")
    ArrayList<FileItem> offerFiles;

    @SerializedName("sku")
    OfferSku sku;

    @SerializedName("sku_id")
    String skuId;

    @SerializedName("sku_measurement_id")
    String skuMeasurementId;

    @SerializedName("offer_discount")
    String offerDiscount;

    @SerializedName("seller_mrp")
    String sellerMrp;

    public String getSellerMrp() {
        return sellerMrp;
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

    public class OfferSku implements Serializable{

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
