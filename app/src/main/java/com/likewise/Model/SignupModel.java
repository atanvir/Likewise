package com.likewise.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class SignupModel implements Parcelable {

    private String name;
    private String email;
    private String username;
    private String password;
    private List<String> languageCode;
    private String deviceToken;
    private String deviceType;
    private String type;


    protected SignupModel(Parcel in) {
        name = in.readString();
        email = in.readString();
        username = in.readString();
        password = in.readString();
    }

    public static final Creator<SignupModel> CREATOR = new Creator<SignupModel>() {
        @Override
        public SignupModel createFromParcel(Parcel in) {
            return new SignupModel(in);
        }

        @Override
        public SignupModel[] newArray(int size) {
            return new SignupModel[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<String> getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(List<String> languageCode) {
        this.languageCode = languageCode;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SignupModel(String fullName, String emailId, String userName, String password) {
        this.name = fullName;
        this.email = emailId;
        this.username = userName;
        this.password = password;
    }

    public SignupModel() {
    }

    @NonNull
    @Override
    public String toString() {
        return "fullName:"+name+"\n"+
                "emailId:"+email+"\n"+
                "userName:"+username+"\n"+
                "password:"+password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(username);
        parcel.writeString(password);
    }
}
