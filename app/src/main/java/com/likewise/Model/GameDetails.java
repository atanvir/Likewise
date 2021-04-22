package com.likewise.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GameDetails implements Parcelable {
    @SerializedName("converses_id")
    @Expose
    private String conversesId;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("NotificationStatus")
    @Expose
    private int notificationStatus;
    @SerializedName("morePrecisely")
    @Expose
    private String morePrecisely;
    @SerializedName("objective")
    @Expose
    private String objective;
    @SerializedName("images")
    @Expose
    private List<String> images = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("languageCode")
    @Expose
    private String languageCode;
    @SerializedName("customInstructions")
    @Expose
    private String customInstructions;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private int v;
    @SerializedName("gameType")
    @Expose
    private String gameType;
    @SerializedName("card_pass_user_id")
    @Expose
    private String card_pass_user_id;

    public String getCard_pass_user_id() {
        return card_pass_user_id;
    }

    public void setCard_pass_user_id(String card_pass_user_id) {
        this.card_pass_user_id = card_pass_user_id;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    protected GameDetails(Parcel in) {
        conversesId = in.readString();
        status = in.readInt();
        notificationStatus = in.readInt();
        morePrecisely = in.readString();
        objective = in.readString();
        images = in.createStringArrayList();
        id = in.readString();
        mode = in.readString();
        languageCode = in.readString();
        customInstructions = in.readString();
        userId = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        v = in.readInt();
    }

    public static final Creator<GameDetails> CREATOR = new Creator<GameDetails>() {
        @Override
        public GameDetails createFromParcel(Parcel in) {
            return new GameDetails(in);
        }

        @Override
        public GameDetails[] newArray(int size) {
            return new GameDetails[size];
        }
    };

    public String getConversesId() {
            return conversesId;
        }

        public void setConversesId(String conversesId) {
            this.conversesId = conversesId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getNotificationStatus() {
            return notificationStatus;
        }

        public void setNotificationStatus(int notificationStatus) {
            this.notificationStatus = notificationStatus;
        }

        public String getMorePrecisely() {
            return morePrecisely;
        }

        public void setMorePrecisely(String morePrecisely) {
            this.morePrecisely = morePrecisely;
        }

        public String getObjective() {
            return objective;
        }

        public void setObjective(String objective) {
            this.objective = objective;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }

        public String getCustomInstructions() {
            return customInstructions;
        }

        public void setCustomInstructions(String customInstructions) {
            this.customInstructions = customInstructions;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversesId);
        dest.writeInt(status);
        dest.writeInt(notificationStatus);
        dest.writeString(morePrecisely);
        dest.writeString(objective);
        dest.writeStringList(images);
        dest.writeString(id);
        dest.writeString(mode);
        dest.writeString(languageCode);
        dest.writeString(customInstructions);
        dest.writeString(userId);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeInt(v);
    }


    public class LanguageDetails {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("picture")
        @Expose
        private String picture;

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

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

    }


    public class MorePrecisely {

        @SerializedName("converse")
        @Expose
        private String converse;

        public String getConverse() {
            return converse;
        }

        public void setConverse(String converse) {
            this.converse = converse;
        }

    }

    public class GameCriteriaExplanations {

        @SerializedName("picture")
        @Expose
        private List<Picture> picture = null;

        @SerializedName("explanation")
        @Expose
        private String explanation;

        public List<Picture> getPicture() {
            return picture;
        }

        public void setPicture(List<Picture> picture) {
            this.picture = picture;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

    }
    public class Picture {

        @SerializedName("picture")
        @Expose
        private String picture;
        @SerializedName("type")
        @Expose
        private String type;

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }
}
