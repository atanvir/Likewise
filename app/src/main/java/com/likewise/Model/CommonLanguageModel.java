package com.likewise.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommonLanguageModel implements Parcelable {
    @SerializedName("status")
    @Expose
    private List<LanguageModel> status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("responseCode")
    @Expose
    private long responseCode;

    @SerializedName("data")
    @Expose
    private List<ResponseBean> data;


    protected CommonLanguageModel(Parcel in) {
        status = in.createTypedArrayList(LanguageModel.CREATOR);
        message = in.readString();
        responseCode = in.readLong();
        data = in.createTypedArrayList(ResponseBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(status);
        dest.writeString(message);
        dest.writeLong(responseCode);
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommonLanguageModel> CREATOR = new Creator<CommonLanguageModel>() {
        @Override
        public CommonLanguageModel createFromParcel(Parcel in) {
            return new CommonLanguageModel(in);
        }

        @Override
        public CommonLanguageModel[] newArray(int size) {
            return new CommonLanguageModel[size];
        }
    };

    public List<LanguageModel> getStatus() {
        return status;
    }

    public void setStatus(List<LanguageModel> status) {
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
}
