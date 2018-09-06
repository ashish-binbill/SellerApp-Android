package com.binbill.seller.Model;

import java.io.Serializable;

/**
 * Created by shruti.vig on 8/19/18.
 */

public class ASCBrandCategoryModel implements Serializable {

    String title;
    boolean isChecked;

    public ASCBrandCategoryModel(String text, boolean checked) {
        this.title = text;
        this.isChecked = checked;
    }

    public ASCBrandCategoryModel(String text) {
        this.title = text;
        this.isChecked = false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
