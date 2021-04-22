package com.likewise.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PointDetails {
    @SerializedName("PointTotalMatchPersantege")
    @Expose
    private double pointTotalMatchPersantege;
    @SerializedName("followerDetail")
    @Expose
    private List<FollowerDetail> followerDetail = null;
    @SerializedName("LikeWisePersantege")
    @Expose
    private double likeWisePersantege;
    @SerializedName("TotalMatch")
    @Expose
    private double totalMatch;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("All_LikeWise_Persantege")
    @Expose
    private double All_LikeWise_Persantege;

    public double getAll_LikeWise_Persantege() {
        return All_LikeWise_Persantege;
    }

    public void setAll_LikeWise_Persantege(double all_LikeWise_Persantege) {
        All_LikeWise_Persantege = all_LikeWise_Persantege;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPointTotalMatchPersantege() {
        return pointTotalMatchPersantege;
    }

    public void setPointTotalMatchPersantege(double pointTotalMatchPersantege) {
        this.pointTotalMatchPersantege = pointTotalMatchPersantege;
    }

    public List<FollowerDetail> getFollowerDetail() {
        return followerDetail;
    }

    public void setFollowerDetail(List<FollowerDetail> followerDetail) {
        this.followerDetail = followerDetail;
    }

    public double getLikeWisePersantege() {
        return likeWisePersantege;
    }

    public void setLikeWisePersantege(double likeWisePersantege) {
        this.likeWisePersantege = likeWisePersantege;
    }

    public double getTotalMatch() {
        return totalMatch;
    }

    public void setTotalMatch(double totalMatch) {
        this.totalMatch = totalMatch;
    }
}
