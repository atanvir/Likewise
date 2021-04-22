package com.likewise.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.likewise.Utility.InstagramLogin;

import java.util.List;

public class CommonModelDataObject {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("responseCode")
    @Expose
    private long responseCode;

    @SerializedName(value = "data",alternate = "graphql")
    @Expose
    private ResponseBean data;

    @SerializedName(value = "countryList",alternate = "result1")
    @Expose
    private List<CountryList> countryList = null;

    @SerializedName(value = "created",alternate = "createMatchFind")
    @Expose
    private Integer created;
    @SerializedName(value = "wordCount",alternate = "wordCoutAnlls")
    @Expose
    private Integer wordCount;
    @SerializedName(value = "mode",alternate = "finalFavoriteModes")
    @Expose
    private Integer mode;
    @SerializedName("played")
    @Expose
    private Integer played;
    @SerializedName(value = "patners",alternate = "commonMatchFind")
    @Expose
    private Integer patners;
    @SerializedName("rank")
    @Expose
    private Integer rank;
    @SerializedName("accuracy")
    @Expose
    private Integer accuracy;

    @SerializedName("access_token")
    @Expose
    private String access_token;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("id")
    @Expose
    private String id;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getPlayed() {
        return played;
    }

    public void setPlayed(Integer played) {
        this.played = played;
    }

    public Integer getPatners() {
        return patners;
    }

    public void setPatners(Integer patners) {
        this.patners = patners;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public List<CountryList> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryList> countryList) {
        this.countryList = countryList;
    }

    public ResponseBean getData() {
        return data;
    }

    public void setData(ResponseBean data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(long responseCode) {
        this.responseCode = responseCode;
    }

    public static class CountryList {
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("picture")
        @Expose
        private String picture;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("updatedAt")
        @Expose
        private String updatedAt;
        @SerializedName("__v")
        @Expose
        private long v;

        private boolean isChecked;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public long getV() {
            return v;
        }

        public void setV(long v) {
            this.v = v;
        }

        @NonNull
        @Override
        public String toString() {
            return "code:"+code;
        }


    }
}




