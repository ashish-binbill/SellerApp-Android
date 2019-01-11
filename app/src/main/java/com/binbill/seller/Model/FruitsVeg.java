package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FruitsVeg implements Serializable {

    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
