package com.binbill.seller.Dashboard;

import com.binbill.seller.Model.FileItem;
import com.binbill.seller.Model.JobCopy;
import com.binbill.seller.Model.StateCityModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

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

    @SerializedName("address_detail")
    String address;

    @SerializedName("address")
    String businessAddress;

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

    public String getBusinessAddress() {
        return businessAddress;
    }

    @SerializedName("city")
    StateCityModel.CityModel city;

    @SerializedName("state")
    StateCityModel state;

    @SerializedName("location")
    StateCityModel.LocalityModel locality;

    @SerializedName("seller_details")
    SellerDetails sellerDetails;


    public StateCityModel.CityModel getCity() {
        return city;
    }

    public StateCityModel.LocalityModel getLocality() {
        return locality;
    }

    public StateCityModel getState() {
        return state;
    }

    public SellerDetails getSellerDetails() {
        return sellerDetails;
    }

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

    public class SellerDetails implements Serializable {

        @SerializedName("basic_details")
        BasicDetails basicDetails;

        @SerializedName("business_details")
        BusinessDetails businessDetails;

        public BusinessDetails getBusinessDetails() {
            return businessDetails;
        }

        public BasicDetails getBasicDetails() {
            return basicDetails;
        }
    }

    public class BasicDetails implements Serializable {

        @SerializedName("category_id")
        String catId;

        @SerializedName("business_name")
        String businessName;

        @SerializedName("payment_modes")
        String paymentModes;

        @SerializedName("shop_open_day")
        String shopOpenDays;

        @SerializedName("home_delivery")
        String homeDelivery;

        @SerializedName("home_delivery_remarks")
        String homeDeliveryRemarks;

        @SerializedName("close_time")
        String closeTime;

        @SerializedName("start_time")
        String startTime;

        @SerializedName("pay_online")
        String payOnline;

        @SerializedName("documents")
        ArrayList<FileItem> documents;

        public String getPayOnline() {
            return payOnline;
        }

        public ArrayList<FileItem> getDocuments() {
            return documents;
        }

        public String getCloseTime() {
            return closeTime;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getHomeDelivery() {
            return homeDelivery;
        }

        public String getHomeDeliveryRemarks() {
            return homeDeliveryRemarks;
        }

        public String getBusinessName() {
            return businessName;
        }

        public String getCatId() {
            return catId;
        }

        public String getPaymentModes() {
            return paymentModes;
        }

        public String getShopOpenDays() {
            return shopOpenDays;
        }

    }

    public class BusinessDetails implements Serializable{
        @SerializedName("business_type")
        String businessType;

        @SerializedName("documents")
        ArrayList<FileItem> documents;

        public ArrayList<FileItem> getDocuments() {
            return documents;
        }

        public String getBusinessType() {
            return businessType;
        }
    }

}
