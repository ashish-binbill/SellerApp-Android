package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/17/18.
 */

public class FMCGHeaderModel implements Serializable {

    @SerializedName("name")
    String name;

    @SerializedName("id")
    String id;

    @SerializedName("categoryImageUrl")
    String imageUrl;

    @SerializedName("subCategories")
    ArrayList<FMCGChildModel> subCategories;

    boolean isShowSelectAll;

    public FMCGHeaderModel(String nameString, boolean selected) {
        this.name = nameString;
        this.isShowSelectAll = selected;
    }

    public String getName() {
        return name;
    }

    public boolean isShowSelectAll() {
        return isShowSelectAll;
    }

    public void setShowSelectAll(boolean checked) {
        isShowSelectAll = checked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<FMCGChildModel> getSubCategories() {
        return subCategories;
    }
}
