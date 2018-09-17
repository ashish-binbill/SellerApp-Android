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

    @SerializedName("address_detail")
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

    @SerializedName("seller_details")
    SellerDetails sellerDetails;

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

        public BasicDetails getBasicDetails() {
            return basicDetails;
        }
    }

    public class BasicDetails implements Serializable {

        /**
         * "category_id": 26,
         * "is_complete": true,
         * "business_name": "Shruti Fashion P T",
         * "payment_modes": "2",
         * "shop_open_day": "5"
         */

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

}
