package com.likewise.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoinModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("speedToRevealChat")
    @Expose
    private Integer speedToRevealChat;
    @SerializedName("numberOfCoinsStart")
    @Expose
    private Integer numberOfCoinsStart;
    @SerializedName("earnCoinsEvery")
    @Expose
    private Integer earnCoinsEvery;
    @SerializedName("referFriend")
    @Expose
    private Integer referFriend;
    @SerializedName("dailyCollection")
    @Expose
    private Integer dailyCollection;
    @SerializedName("watchVideo")
    @Expose
    private Integer watchVideo;
    @SerializedName("coin")
    @Expose
    private Integer coin;
    @SerializedName("point")
    @Expose
    private Integer point;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSpeedToRevealChat() {
        return speedToRevealChat;
    }

    public void setSpeedToRevealChat(Integer speedToRevealChat) {
        this.speedToRevealChat = speedToRevealChat;
    }

    public Integer getNumberOfCoinsStart() {
        return numberOfCoinsStart;
    }

    public void setNumberOfCoinsStart(Integer numberOfCoinsStart) {
        this.numberOfCoinsStart = numberOfCoinsStart;
    }

    public Integer getEarnCoinsEvery() {
        return earnCoinsEvery;
    }

    public void setEarnCoinsEvery(Integer earnCoinsEvery) {
        this.earnCoinsEvery = earnCoinsEvery;
    }

    public Integer getReferFriend() {
        return referFriend;
    }

    public void setReferFriend(Integer referFriend) {
        this.referFriend = referFriend;
    }

    public Integer getDailyCollection() {
        return dailyCollection;
    }

    public void setDailyCollection(Integer dailyCollection) {
        this.dailyCollection = dailyCollection;
    }

    public Integer getWatchVideo() {
        return watchVideo;
    }

    public void setWatchVideo(Integer watchVideo) {
        this.watchVideo = watchVideo;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }
}
