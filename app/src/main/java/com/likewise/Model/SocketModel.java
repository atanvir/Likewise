package com.likewise.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SocketModel implements Parcelable  {
    @SerializedName("notificationCount")
    @Expose
    private int notificationCount;

    @SerializedName("result")
    @Expose
    private Result result;

    protected SocketModel(Parcel in) {
        notificationCount = in.readInt();
        list = in.createTypedArrayList(List.CREATOR);
        dataTosave = in.readParcelable(DataTosave.class.getClassLoader());
        status = in.readString();
        roomId = in.readString();
        senderId = in.readString();
        receiverId = in.readString();
        message = in.readString();
        coins = in.readString();
        game_id = in.readString();
    }

    public static final Creator<SocketModel> CREATOR = new Creator<SocketModel>() {
        @Override
        public SocketModel createFromParcel(Parcel in) {
            return new SocketModel(in);
        }

        @Override
        public SocketModel[] newArray(int size) {
            return new SocketModel[size];
        }
    };

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @SerializedName("list")
    @Expose
    private java.util.List<List> list = null;

    @SerializedName("dataTosave")
    @Expose
    private DataTosave dataTosave;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("room_id")
    @Expose
    private String roomId;
    @SerializedName(value = "sender_id",alternate = "senderId")
    @Expose
    private String senderId;
    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName(value = "message",alternate = "msg")
    @Expose
    private String message;
    @SerializedName("coins")
    @Expose
    private String coins;
    @SerializedName("game_id")
    @Expose
    private String game_id;

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public SocketModel() {
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public DataTosave getDataTosave() {
        return dataTosave;
    }

    public void setDataTosave(DataTosave dataTosave) {
        this.dataTosave = dataTosave;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public java.util.List<List> getList() {
        return list;
    }

    public void setList(java.util.List<List> list) {
        this.list = list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(notificationCount);
        dest.writeTypedList(list);
        dest.writeParcelable(dataTosave, flags);
        dest.writeString(status);
        dest.writeString(roomId);
        dest.writeString(senderId);
        dest.writeString(receiverId);
        dest.writeString(message);
        dest.writeString(coins);
        dest.writeString(game_id);
    }


    public class Result {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("totalPoints")
        @Expose
        private int totalPoints;
        @SerializedName("coins")
        @Expose
        private int coins;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getTotalPoints() {
            return totalPoints;
        }

        public void setTotalPoints(int totalPoints) {
            this.totalPoints = totalPoints;
        }

        public int getCoins() {
            return coins;
        }

        public void setCoins(int coins) {
            this.coins = coins;
        }

    }


    public static class DataTosave implements Parcelable {

        @SerializedName("sender_id")
        @Expose
        private String senderId;
        @SerializedName("receiver_id")
        @Expose
        private String receiverId;
        @SerializedName("game_id")
        @Expose
        private String gameId;
        @SerializedName("read_status")
        @Expose
        private boolean readStatus;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("status")
        @Expose
        private boolean status;

        @SerializedName("userId")
        @Expose
        private String userId;

        @SerializedName("type")
        @Expose
        private String type;

        public DataTosave(String receiverId,String gameId,String type)
        {
            this.receiverId=receiverId;
            this.gameId=gameId;
            this.type=type;
        }

        protected DataTosave(Parcel in) {
            senderId = in.readString();
            receiverId = in.readString();
            gameId = in.readString();
            readStatus = in.readByte() != 0;
            createdAt = in.readString();
            status = in.readByte() != 0;
            userId = in.readString();
            type = in.readString();
        }

        public static final Creator<DataTosave> CREATOR = new Creator<DataTosave>() {
            @Override
            public DataTosave createFromParcel(Parcel in) {
                return new DataTosave(in);
            }

            @Override
            public DataTosave[] newArray(int size) {
                return new DataTosave[size];
            }
        };

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }


        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getGameId() {
            return gameId;
        }

        public void setGameId(String gameId) {
            this.gameId = gameId;
        }

        public boolean isReadStatus() {
            return readStatus;
        }

        public void setReadStatus(boolean readStatus) {
            this.readStatus = readStatus;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(senderId);
            dest.writeString(receiverId);
            dest.writeString(gameId);
            dest.writeByte((byte) (readStatus ? 1 : 0));
            dest.writeString(createdAt);
            dest.writeByte((byte) (status ? 1 : 0));
            dest.writeString(userId);
            dest.writeString(type);
        }
    }

    public static class List implements Parcelable   {
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("userId")
        @Expose
        private String userId;
        @SerializedName("receiverId")
        @Expose
        private String receiverId;
        @SerializedName("gameId")
        @Expose
        private String gameId;
        @SerializedName("profilePic")
        @Expose
        private String profilePic;
        @SerializedName("fullName")
        @Expose
        private String fullName;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("updatedAt")
        @Expose
        private String updatedAt;
        @SerializedName("__v")
        @Expose
        private int v;
        @SerializedName("aboutus")
        @Expose
        private String aboutus;
        @SerializedName("room_id")
        @Expose
        private String room_id;

        @SerializedName("usersDetail")
        @Expose
        private java.util.List<UsersDetail> usersDetail = null;


        protected List(Parcel in) {
            id = in.readString();
            status = in.readString();
            userId = in.readString();
            receiverId = in.readString();
            gameId = in.readString();
            profilePic = in.readString();
            fullName = in.readString();
            type = in.readString();
            createdAt = in.readString();
            updatedAt = in.readString();
            v = in.readInt();
            aboutus = in.readString();
            room_id = in.readString();
        }

        public static final Creator<List> CREATOR = new Creator<List>() {
            @Override
            public List createFromParcel(Parcel in) {
                return new List(in);
            }

            @Override
            public List[] newArray(int size) {
                return new List[size];
            }
        };

        public java.util.List<UsersDetail> getUsersDetail() {
            return usersDetail;
        }

        public void setUsersDetail(java.util.List<UsersDetail> usersDetail) {
            this.usersDetail = usersDetail;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getGameId() {
            return gameId;
        }

        public void setGameId(String gameId) {
            this.gameId = gameId;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

        public String getAboutus() {
            return aboutus;
        }

        public void setAboutus(String aboutus) {
            this.aboutus = aboutus;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(status);
            dest.writeString(userId);
            dest.writeString(receiverId);
            dest.writeString(gameId);
            dest.writeString(profilePic);
            dest.writeString(fullName);
            dest.writeString(type);
            dest.writeString(createdAt);
            dest.writeString(updatedAt);
            dest.writeInt(v);
            dest.writeString(aboutus);
            dest.writeString(room_id);
        }
    }


    public class UsersDetail {

        @SerializedName("aboutus")
        @Expose
        private String aboutus;

        public String getAboutus() {
            return aboutus;
        }

        public void setAboutus(String aboutus) {
            this.aboutus = aboutus;
        }

    }
}
