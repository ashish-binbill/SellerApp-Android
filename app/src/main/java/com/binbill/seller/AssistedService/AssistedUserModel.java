package com.binbill.seller.AssistedService;

import com.binbill.seller.Model.FileItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/2/18.
 */

public class AssistedUserModel implements Serializable{

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    @SerializedName("mobile_no")
    String mobile;

    @SerializedName("reviews")
    ArrayList<Review> reviews;

    @SerializedName("rating")
    String rating;

    @SerializedName("document_details")
    ArrayList<FileItem> documents;

    @SerializedName("service_types")
    ArrayList<ServiceType> serviceTypes;

    @SerializedName("profile_image_detail")
    FileItem profileImage;

    public FileItem getProfileImage() {
        return profileImage;
    }

    public ArrayList<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public String getRating() {
        return rating;
    }

    public ArrayList<FileItem> getDocuments() {
        return documents;
    }

    public class ServiceType implements Serializable{
        @SerializedName("id")
        String id;

        @SerializedName("title")
        String name;

        @SerializedName("service_type")
        String serviceType;

        @SerializedName("service_type_id")
        String serviceTypeId;

        @SerializedName("price")
        ArrayList<Price> price;

        public ArrayList<Price> getPrice() {
            return price;
        }

        public String getServiceType() {
            return serviceType;
        }

        public String getServiceTypeId() {
            return serviceTypeId;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Price implements Serializable{
        @SerializedName("value")
        String value;

        @SerializedName("price_type")
        String priceType;

        public String getPriceType() {
            return priceType;
        }

        public String getValue() {
            return value;
        }
    }

    public class Review implements Serializable{

        @SerializedName("ratings")
        String rating;

        @SerializedName("feedback")
        String feedback;

        @SerializedName("updated_by")
        String userID;

        @SerializedName("user_name")
        String userName;

        public String getUserName() {
            return userName;
        }

        public String getUserID() {
            return userID;
        }

        public String getFeedback() {
            return feedback;
        }

        public String getRating() {
            return rating;
        }
    }
}
