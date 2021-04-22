package com.likewise.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateGameResponse implements Parcelable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("responseCode")
    @Expose
    private int responseCode;
    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("notificationId")
    @Expose
    private String notificationId;


    protected CreateGameResponse(Parcel in) {
        status = in.readString();
        message = in.readString();
        responseCode = in.readInt();
        data = in.readParcelable(Data.class.getClassLoader());
        notificationId=in.readString();
    }

    public static final Creator<CreateGameResponse> CREATOR = new Creator<CreateGameResponse>() {
        @Override
        public CreateGameResponse createFromParcel(Parcel in) {
            return new CreateGameResponse(in);
        }

        @Override
        public CreateGameResponse[] newArray(int size) {
            return new CreateGameResponse[size];
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

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(message);
        dest.writeInt(responseCode);
        dest.writeParcelable(data, flags);
        dest.writeString(notificationId);
    }

    public static class Data implements Parcelable {
        @SerializedName("game_details")
        @Expose
        private GameDetails game_details;
        @SerializedName("receiver_id")
        @Expose
        private String receiver_id;


        protected Data(Parcel in) {
            game_details = in.readParcelable(GameDetails.class.getClassLoader());
            receiver_id = in.readString();
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public GameDetails getGame_details() {
            return game_details;
        }

        public void setGame_details(GameDetails game_details) {
            this.game_details = game_details;
        }

        public String getReceiver_id() {
            return receiver_id;
        }

        public void setReceiver_id(String receiver_id) {
            this.receiver_id = receiver_id;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(game_details, flags);
            dest.writeString(receiver_id);
        }
    }
}
