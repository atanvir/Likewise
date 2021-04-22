package com.likewise.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;

public class MessageResponse {
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
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("read_status")
    @Expose
    private boolean readStatus;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("encryped")
    @Expose
    private String encryped;
    @SerializedName("sender_by")
    @Expose
    private boolean sender_by;

    @SerializedName("receiver_by")
    @Expose
    private boolean receiver_by;

    @SerializedName("SenderMessage")
    @Expose
    private String SenderMessage;

    @SerializedName("ReceiverMessage")
    @Expose
    private String ReceiverMessage;
    @SerializedName("lives")
    @Expose
    private String lives;

    private boolean isMatched;

    @SerializedName("score")
    @Expose
    private double score;

    @SerializedName("point")
    @Expose
    private long point;

    @SerializedName("bonus")
    @Expose
    private int bonus;


    @SerializedName("streaks")
    @Expose
    private int streaks;

    @SerializedName("all_matched")
    @Expose
    private int all_matched;

    @SerializedName("pointId")
    @Expose
    private String pointId;

    @SerializedName("is_typing")
    @Expose
    private boolean typing;

    @SerializedName("Image")
    @Expose
    private String Image;

    @SerializedName("card")
    @Expose
    private String card;

    @SerializedName("isEncrypted")
    @Expose
    private boolean isEncrypted;

    private boolean isLiveDeducted;

    @SerializedName("coins")
    @Expose
    private String coins;

    private boolean isStreak;

    @SerializedName("event_name")
    @Expose
    private String event_name;



    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public boolean isStreak() {
        return isStreak;
    }

    public void setStreak(boolean streak) {
        isStreak = streak;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }



    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public boolean isLiveDeducted() {
        return isLiveDeducted;
    }

    public void setLiveDeducted(boolean liveDeducted) {
        isLiveDeducted = liveDeducted;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public int getStreaks() {
        return streaks;
    }

    public void setStreaks(int streaks) {
        this.streaks = streaks;
    }

    public int getAll_matched() {
        return all_matched;
    }

    public void setAll_matched(int all_matched) {
        this.all_matched = all_matched;
    }

    public void setSender_by(boolean sender_by) {
        this.sender_by = sender_by;
    }

    public String getLives() {
        return lives;
    }

    public void setLives(String lives) {
        this.lives = lives;
    }



    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public String getSenderMessage() {
        return SenderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        SenderMessage = senderMessage;
    }

    public String getReceiverMessage() {
        return ReceiverMessage;
    }

    public void setReceiverMessage(String receiverMessage) {
        ReceiverMessage = receiverMessage;
    }

    public boolean isSender_by() {
        return sender_by;
    }


    public boolean isReceiver_by() {
        return receiver_by;
    }

    public void setReceiver_by(boolean receiver_by) {
        this.receiver_by = receiver_by;
    }

    public String getEncryped() {
        return encryped;
    }

    public void setEncryped(String encryped) {
        String text=encryped;
        String duplicate="";
        char character[]= text.toCharArray();
        for(int i=0;i<character.length;i++)
        {
            String c= String.valueOf(character[i]);
            if(c.equalsIgnoreCase(" ")) duplicate+=c;
            else if (c.equalsIgnoreCase("\n")) duplicate+=c;
            else duplicate+="\u2022";
        }
        Log.e("encyted",duplicate);
        this.encryped=duplicate;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @NonNull
    @Override
    public String toString() {
        return "isMatched"+isMatched+"\n"+"message:"+message;
    }
}
