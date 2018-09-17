package com.binbill.seller.Order;

import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.Model.FileItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/8/18.
 */

public class DeliveryModel implements Serializable {

    @SerializedName("id")
    String deliveryBoyId;

    @SerializedName("name")
    String name;

    @SerializedName("mobile_no")
    String mobile;

    @SerializedName("rating")
    String rating;

    @SerializedName("order_counts")
    String orderCount;

    @SerializedName("profile_image_detail")
    FileItem profileImage;

    @SerializedName("service_types")
    ArrayList<AssistedUserModel.ServiceType> serviceType;

    transient boolean selected;

    public ArrayList<AssistedUserModel.ServiceType> getServiceType() {
        return serviceType;
    }

    @SerializedName("reviews")
    ArrayList<AssistedUserModel.Review> reviews;

    public String getDeliveryBoyId() {
        return deliveryBoyId;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getRating() {
        return rating;
    }

    public ArrayList<AssistedUserModel.Review> getReviews() {
        return reviews;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public FileItem getProfileImage() {
        return profileImage;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
