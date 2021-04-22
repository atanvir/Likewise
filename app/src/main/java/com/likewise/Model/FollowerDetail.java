package com.likewise.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FollowerDetail {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("scoreMax")
    @Expose
    private int scoreMax;
    @SerializedName("pointMax")
    @Expose
    private int pointMax;
    @SerializedName("sumStreaks")
    @Expose
    private int sumStreaks;
    @SerializedName("bonus")
    @Expose
    private int bonus;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScoreMax() {
        return scoreMax;
    }

    public void setScoreMax(int scoreMax) {
        this.scoreMax = scoreMax;
    }

    public int getPointMax() {
        return pointMax;
    }

    public void setPointMax(int pointMax) {
        this.pointMax = pointMax;
    }

    public int getSumStreaks() {
        return sumStreaks;
    }

    public void setSumStreaks(int sumStreaks) {
        this.sumStreaks = sumStreaks;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
