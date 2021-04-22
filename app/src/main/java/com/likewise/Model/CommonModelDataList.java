package com.likewise.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommonModelDataList implements Parcelable {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("responseCode")
    @Expose
    private long responseCode;

    @SerializedName("data")
    @Expose
    private List<ResponseBean> data;


    protected CommonModelDataList(Parcel in) {
        status = in.readString();
        message = in.readString();
        responseCode = in.readLong();
        data = in.createTypedArrayList(ResponseBean.CREATOR);
    }

    public static final Creator<CommonModelDataList> CREATOR = new Creator<CommonModelDataList>() {
        @Override
        public CommonModelDataList createFromParcel(Parcel in) {
            return new CommonModelDataList(in);
        }

        @Override
        public CommonModelDataList[] newArray(int size) {
            return new CommonModelDataList[size];
        }
    };

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

    public List<ResponseBean> getData() {
        return data;
    }

    public void setData(List<ResponseBean> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(message);
        dest.writeLong(responseCode);
    }
}
