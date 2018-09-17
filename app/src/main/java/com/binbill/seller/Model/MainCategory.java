package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 8/15/18.
 */

public class MainCategory implements Serializable{

    @SerializedName("id")
    String id;

    @SerializedName("title")
    String name;

    /**
     * Reused to get agent name during job completion in service type order
     */
    @SerializedName("name")
    String agentName;

    public String getAgentName() {
        return agentName;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
