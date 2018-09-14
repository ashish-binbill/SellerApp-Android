package com.binbill.seller.Registration;

import java.io.Serializable;

/**
 * Created by shruti.vig on 9/12/18.
 */

public class Seller implements Serializable {

    String id;
    String name;
    String address;
    boolean selected;

    Seller(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
