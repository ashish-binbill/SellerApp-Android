package com.binbill.seller.Offers;

import com.binbill.seller.Model.FileItem;
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
}
