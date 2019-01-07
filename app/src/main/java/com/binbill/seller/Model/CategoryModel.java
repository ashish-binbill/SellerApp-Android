package com.binbill.seller.Model;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;

    private String id;

    private String imageUrl;

    private boolean isSelected;

    public CategoryModel() {

    }

    public CategoryModel(String name) {

        this.name = name;

    }

    public CategoryModel(String name, String id, String imageUrl, boolean isSelected) {

        this.name = name;
        this.isSelected = isSelected;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}

