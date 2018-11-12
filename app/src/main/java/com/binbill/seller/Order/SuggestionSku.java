package com.binbill.seller.Order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SuggestionSku implements Serializable{

    @SerializedName("sub_category_name")
    String subCategoryName;

    @SerializedName("brand_id")
    String brandId;

    @SerializedName("category_id")
    String categoryId;

    @SerializedName("sub_category_id")
    String subCategoryId;

    @SerializedName("main_category_id")
    String mainCategoryId;

    @SerializedName("title")
    String title;

    @SerializedName("id")
    String id;

    @SerializedName("priority_index")
    String priorityIndex;

    @SerializedName("sku_measurements")
    ArrayList<OrderItem.OrderSKU> measurement;

    public ArrayList<OrderItem.OrderSKU> getMeasurement() {
        return measurement;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public String getMainCategoryId() {
        return mainCategoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getPriorityIndex() {
        return priorityIndex;
    }
}
