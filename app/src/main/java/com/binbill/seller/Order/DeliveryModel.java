package com.binbill.seller.Order;

import com.binbill.seller.AssistedService.AssistedUserModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 9/8/18.
 */

public class DeliveryModel implements Serializable {
    /**
     * {
     * "id": 17,
     * "name": "Mr Sagar Chauhan",
     * "mobile_no": "9873179584",
     * "reviews": [
     * {
     * "ratings": 4,
     * "feedback": "We are testing you.",
     * "updated_by": 27774
     * }
     * ],
     * "document_details": [
     * {
     * "type": "3",
     * "index": "xytbc2pfi4",
     * "file_name": "4-xytbc2pfi4.jpg",
     * "file_type": "jpg",
     * "image_type": "",
     * "updated_by": 4
     * }
     * ],
     * "profile_image_detail": {
     * "type": "5",
     * "index": "v5ibtrj024",
     * "file_name": "4-v5ibtrj024.jpg",
     * "file_type": "jpg",
     * "image_type": "",
     * "updated_by": 4
     * },
     * "service_types": [
     * {
     * "service_type_id": 0,
     * "seller_id": 2457,
     * "price": {
     * "value": 50,
     * "price_type": 1
     * },
     * "id": 19,
     * "service_type": "Delivery Boy"
     * }
     * ],
     * "rating": 4
     * }
     */

    @SerializedName("id")
    String deliveryBoyId;

    @SerializedName("name")
    String name;

    @SerializedName("mobile_no")
    String mobile;

    @SerializedName("rating")
    String rating;

    @SerializedName("reviews")
    AssistedUserModel.Review reviews;

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

    public AssistedUserModel.Review getReviews() {
        return reviews;
    }
}
