package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 8/17/18.
 */

public class FMCGChildModel implements Serializable {

    @SerializedName("name")
    String name;

    @SerializedName("id")
    String id;
    @SerializedName("refId")
    String refId;
    @SerializedName("categoryImageUrl")
    String imageUrl;

    transient boolean userSelected;

    public FMCGChildModel(String nameString, String id, boolean selected) {
        this.name = nameString;
        this.userSelected = selected;
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getRefId() {
        return refId;
    }

    public String getName() {
        return name;
    }

    public boolean isUserSelected() {
        return userSelected;
    }

    public void setUserSelected(boolean checked) {
        userSelected = checked;
    }

    public void setName(String name) {
        this.name = name;
    }
}
