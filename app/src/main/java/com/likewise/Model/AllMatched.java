package com.likewise.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllMatched {
    @SerializedName("Image")
    @Expose
    private String image;
    @SerializedName("suggestion")
    @Expose
    private Integer suggestion;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Integer suggestion) {
        this.suggestion = suggestion;
    }



}
