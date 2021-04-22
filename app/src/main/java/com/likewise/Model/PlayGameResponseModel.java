package com.likewise.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlayGameResponseModel implements Parcelable {

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
    private Data data;

    protected PlayGameResponseModel(Parcel in) {
        status = in.readString();
        message = in.readString();
        responseCode = in.readLong();
        data = in.readParcelable(Data.class.getClassLoader());
    }

    public static final Creator<PlayGameResponseModel> CREATOR = new Creator<PlayGameResponseModel>() {
        @Override
        public PlayGameResponseModel createFromParcel(Parcel in) {
            return new PlayGameResponseModel(in);
        }

        @Override
        public PlayGameResponseModel[] newArray(int size) {
            return new PlayGameResponseModel[size];
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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
        dest.writeParcelable(data, flags);
    }


    public static class FollowerDetail implements Parcelable {

        @SerializedName("_id")
        @Expose
        private Id id;
        @SerializedName("count")
        @Expose
        private long count;


        protected FollowerDetail(Parcel in) {
            id = in.readParcelable(Id.class.getClassLoader());
            count = in.readLong();
        }

        public static final Creator<FollowerDetail> CREATOR = new Creator<FollowerDetail>() {
            @Override
            public FollowerDetail createFromParcel(Parcel in) {
                return new FollowerDetail(in);
            }

            @Override
            public FollowerDetail[] newArray(int size) {
                return new FollowerDetail[size];
            }
        };

        public Id getId() {
            return id;
        }

        public void setId(Id id) {
            this.id = id;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(id, flags);
            dest.writeLong(count);
        }
    }

    public static class Id implements Parcelable {

        @SerializedName("Image")
        @Expose
        private String image;

        protected Id(Parcel in) {
            image = in.readString();
        }

        public static final Creator<Id> CREATOR = new Creator<Id>() {
            @Override
            public Id createFromParcel(Parcel in) {
                return new Id(in);
            }

            @Override
            public Id[] newArray(int size) {
                return new Id[size];
            }
        };

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(image);
        }
    }

    public static class UserDetail implements Parcelable {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("profilePic")
        @Expose
        private String profilePic;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("country")
        @Expose
        private String country;

        protected UserDetail(Parcel in) {
            id = in.readString();
            profilePic = in.readString();
            name = in.readString();
            gender = in.readString();
            country = in.readString();
        }

        public static final Creator<UserDetail> CREATOR = new Creator<UserDetail>() {
            @Override
            public UserDetail createFromParcel(Parcel in) {
                return new UserDetail(in);
            }

            @Override
            public UserDetail[] newArray(int size) {
                return new UserDetail[size];
            }
        };

        public String getCountry() {
            return country;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(profilePic);
            dest.writeString(name);
            dest.writeString(gender);
            dest.writeString(country);
        }
    }

    public static class Data implements Parcelable {

        @SerializedName("followerDetail")
        @Expose
        private List<FollowerDetail> followerDetail = null;
        @SerializedName("userDetails")
        @Expose
        private List<UserDetail> userDetails = null;

        @SerializedName("converseModel")
        @Expose
        private List<Morerecisely> converseModel = null;
        @SerializedName("explanationModel")
        @Expose
        private List<Objective> explanationModel = null;

        public List<Morerecisely> getConverseModel() {
            return converseModel;
        }

        public void setConverseModel(List<Morerecisely> converseModel) {
            this.converseModel = converseModel;
        }

        public List<Objective> getExplanationModel() {
            return explanationModel;
        }

        public void setExplanationModel(List<Objective> explanationModel) {
            this.explanationModel = explanationModel;
        }

        protected Data(Parcel in) {
            followerDetail = in.createTypedArrayList(FollowerDetail.CREATOR);
            userDetails = in.createTypedArrayList(UserDetail.CREATOR);
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

        public List<FollowerDetail> getFollowerDetail() {
            return followerDetail;
        }

        public void setFollowerDetail(List<FollowerDetail> followerDetail) {
            this.followerDetail = followerDetail;
        }

        public List<UserDetail> getUserDetails() {
            return userDetails;
        }

        public void setUserDetails(List<UserDetail> userDetails) {
            this.userDetails = userDetails;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(followerDetail);
            dest.writeTypedList(userDetails);
        }
    }




}
