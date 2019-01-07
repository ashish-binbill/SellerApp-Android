package com.binbill.seller.Model;

import java.io.Serializable;

public class InventoryModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;

   // private String id;

   // private boolean isSelected;

    public InventoryModel() {

    }

    public InventoryModel(String name) {

        this.name = name;

    }

    public InventoryModel(String name, String id, boolean isSelected) {

        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
