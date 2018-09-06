package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 8/29/18.
 */

public class FileItem implements Serializable {

    @SerializedName("type")
    String type;
    @SerializedName("file_name")
    String fileName;
    @SerializedName("file_type")
    String fileType;
    @SerializedName("image_type")
    String imageType;
    @SerializedName("updated_by")
    String updatedBy;

    public String getType() {
        return type;
    }

    public String getFileType() {
        return fileType;
    }

    public String getImageType() {
        return imageType;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getFileName() {
        return fileName;
    }
}
