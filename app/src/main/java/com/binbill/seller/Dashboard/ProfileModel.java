package com.binbill.seller.Dashboard;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 9/8/18.
 */

public class ProfileModel implements Serializable {

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    @SerializedName("owner_name")
    String ownerName;

    @SerializedName("gstin")
    String gstin;

    @SerializedName("pan_no")
    String pan;

    @SerializedName("reg_no")
    String regNo;

    @SerializedName("is_service")
    boolean service;

    @SerializedName("is_onboarded")
    boolean onboarded;

    @SerializedName("address")
    String address;

    @SerializedName("latitude")
    String latitude;

    @SerializedName("longitude")
    String longitude;

    @SerializedName("url")
    String url;

    @SerializedName("contact_no")
    String contactNo;

    @SerializedName("email")
    String email;

    @SerializedName("seller_type_id")
    String sellerTypeId;

    @SerializedName("cashback_total")
    String cashBack;

    @SerializedName("rating")
    String rating;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getGstin() {
        return gstin;
    }

    public String getPan() {
        return pan;
    }

    public String getRegNo() {
        return regNo;
    }

    public boolean isService() {
        return service;
    }

    public boolean isOnboarded() {
        return onboarded;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUrl() {
        return url;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getEmail() {
        return email;
    }

    public String getSellerTypeId() {
        return sellerTypeId;
    }

    public String getCashBack() {
        return cashBack;
    }

    public String getRating() {
        return rating;
    }
}
