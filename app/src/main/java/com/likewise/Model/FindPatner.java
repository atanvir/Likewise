package com.likewise.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FindPatner {
    @SerializedName("_id")
    @Expose
    private Id id;
    @SerializedName("uniqueIds")
    @Expose
    private List<String> uniqueIds = null;
    @SerializedName("count")
    @Expose
    private long count;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public List<String> getUniqueIds() {
        return uniqueIds;
    }

    public void setUniqueIds(List<String> uniqueIds) {
        this.uniqueIds = uniqueIds;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }


    public class Id
    {
        @SerializedName("Image")
        @Expose
        private String Image;

        public String getImage() {
            return Image;
        }

        public void setImage(String image) {
            Image = image;
        }
    }
}
