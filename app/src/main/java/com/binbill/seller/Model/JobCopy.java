package com.binbill.seller.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by shruti.vig on 9/4/18.
 */

public class JobCopy implements Serializable {

    @SerializedName("jobId")
    String jobId;

    @SerializedName("copyId")
    String copyId;

    @SerializedName("copyUrl")
    String copyUrl;

    @SerializedName("copyName")
    String copyName;

    @SerializedName("file_type")
    String fileType;

    public String getJobId() {
        return jobId;
    }

    public String getCopyId() {
        return copyId;
    }

    public String getCopyUrl() {
        return copyUrl;
    }

    public String getCopyName() {
        return copyName;
    }

    public String getFileType() {
        return fileType;
    }
}
