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

    transient String refId;

    @SerializedName("categoryImageUrl")
    String imageUrl;

    @SerializedName("subCategories")
    ArrayList<FMCGChildModel> subCategories;

    boolean isShowSelectAll;

    public FMCGHeaderModel(String nameString, String id, boolean selected) {
        this.name = nameString;
        this.isShowSelectAll = selected;
        this.id = id;
    }

    public FMCGHeaderModel(String nameString, String id, String url,boolean selected ){
        this.name = nameString;
        this.isShowSelectAll = selected;
        this.id = id;
        this.imageUrl = url;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<FMCGChildModel> getSubCategories() {
        return subCategories;
    }
}
