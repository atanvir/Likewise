package com.likewise.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseBean implements Parcelable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName(value = "_id",alternate = "id")
    @Expose
    private String id;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private long v;

    @SerializedName("profilePic")
    @Expose
    private String profilePic;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName(value = "coins",alternate = "coin")
    @Expose
    private String coins;
    @SerializedName("dollar")
    @Expose
    private String dollar;


    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("notificationStatus")
    @Expose
    private long notificationStatus;
    @SerializedName("totalPoints")
    @Expose
    private long totalPoints;
    @SerializedName("languageCode")
    @Expose
    private List<String> languageCode = null;
    @SerializedName("coinStatus")
    @Expose
    private long coinStatus;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("jwtToken")
    @Expose
    private String jwtToken;
    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;
    @SerializedName("deviceType")
    @Expose
    private String deviceType;

    @SerializedName("type")
    @Expose
    private String type;


    @SerializedName("notification")
    @Expose
    private boolean notification;
    @SerializedName("socialId")
    @Expose
    private String socialId;
    @SerializedName("socialType")
    @Expose
    private String socialType;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;

    @SerializedName("defaultLangCode")
    @Expose
    private String defaultLangCode;

    @SerializedName("nationalit")
    @Expose
    private String nationalit;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("interest")
    @Expose
    private String interest;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("aboutus")
    @Expose
    private String aboutus;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("question")
    @Expose
    private String question;

    @SerializedName("answer")
    @Expose
    private String answer;

    @SerializedName(value = "Objective",alternate = "explanation")
    @Expose
    private List<Objective> objective = null;
    @SerializedName(value = "Morerecisely",alternate = "converse")
    @Expose
    private List<Morerecisely> morerecisely = null;
    @SerializedName("fbFriends")
    @Expose
    private List<FbFriend> fbFriends = null;
    @SerializedName("laguage")
    @Expose
    private List<Laguage> laguage = null;


    @SerializedName("themeName")
    @Expose
    private List<String> themeName = null;
    @SerializedName("images")
    @Expose
    private List<String> images = null;
    @SerializedName("room_id")
    @Expose
    private String room_id;

    private boolean isChecked=false;

    @SerializedName("game_details")
    @Expose
    private GameDetails gameDetails;
    @SerializedName("morePrecisely")
    @Expose
    private GameDetails.MorePrecisely morePrecisely;
    @SerializedName("gameCriteriaExplanations")
    @Expose
    private GameDetails.GameCriteriaExplanations gameCriteriaExplanations;
    @SerializedName("languageDetails")
    @Expose
    private GameDetails.LanguageDetails languageDetails;

    @SerializedName("followerDetail")
    @Expose
    private List<FollowerDetail> followerDetail = null;
    @SerializedName("TotalMessage")
    @Expose
    private int totalMessage;
    @SerializedName("TotalMatch")
    @Expose
    private int totalMatch;
    @SerializedName("LikeWisePersantege")
    @Expose
    private double likeWisePersantege;
    @SerializedName("Unicity")
    @Expose
    private double unicity;
    @SerializedName(value = "receiver_details" ,alternate = "userDetails" )
    @Expose
    private ReceiverDetails receiverDetails;

    @SerializedName("chatData")
    @Expose
    private List<MessageResponse> chatData;

    @SerializedName("coinLeft")
    @Expose
    private String coinLeft;

    @SerializedName("pointDetails")
    @Expose
    private PointDetails pointDetails;
    @SerializedName("mode")
    @Expose
    private String mode;

    @SerializedName("objectives")
    @Expose
    private String objectives;
    @SerializedName("morePreciselys")
    @Expose
    private String morePreciselys;

    @SerializedName("user_type")
    @Expose
    private String user_type;

    @SerializedName("sender_id")
    @Expose
    private String sender_id;

    @SerializedName("receiver_id")
    @Expose
    private String receiver_id;

    @SerializedName("game_id")
    @Expose
    private String game_id;

    @SerializedName("current_card")
    @Expose
    private int current_card;

    @SerializedName("path")
    @Expose
    private String path;

    @SerializedName("list")
    @Expose
    private List<SocketModel.List> list;

    @SerializedName("lives")
    @Expose
    private String lives;

    @SerializedName("messageCount")
    @Expose
    private long messageCount;

    boolean isTaken;

    @SerializedName("getFindPatner")
    @Expose
    private List<FindPatner> findPatners;

    @SerializedName("languageCodes")
    @Expose
    private String Code;

    @SerializedName("cardImage")
    @Expose
    private List<CardImageModel> cardImage;

    @SerializedName("gameType")
    @Expose
    private String gameType;

    @SerializedName("details")
    @Expose
    private PointDetails details;

    @SerializedName("gameCounts")
    @Expose
    private String gameCounts;

    @SerializedName("scores")
    @Expose
    private String scores;

    @SerializedName(value = "countryList",alternate = "result1")
    @Expose
    private List<CommonModelDataObject.CountryList> countryList = null;

    @SerializedName("totalMatchFind")
    @Expose
    private Integer totalMatchFind;
    @SerializedName("createMatchFind")
    @Expose
    private Integer createMatchFind;
    @SerializedName("commonMatchFind")
    @Expose
    private Integer commonMatchFind;
    @SerializedName("wordCountAlls")
    @Expose
    private Integer wordCountAlls;
    @SerializedName("finalFavoriteModes")
    @Expose
    private Integer finalFavoriteModes;
    @SerializedName("accuracy")
    @Expose
    private Double accuracy;
    @SerializedName("userData")
    @Expose
    private ReceiverDetails userData;

    @SerializedName("coinPay")
    @Expose
    private List<CoinPay> coinPay = null;
    @SerializedName("coinModel")
    @Expose
    private List<CoinModel> coinModel = null;

    @SerializedName("receiver")
    @Expose
    private ReceiverDetails receiver;

    @SerializedName("timeUpdate")
    @Expose
    private String timeUpdate;

    @SerializedName("likewisePer")
    @Expose
    private Double likewisePer;

    @SerializedName("userValue")
    @Expose
    private String userValue;

    @SerializedName("allmatchpercent")
    @Expose
    private List<AllMatched> allmatchpercent = null;

    @SerializedName("value")
    @Expose
    private String value;

    @SerializedName("likewisePersantegess")
    @Expose
    private Double likewisePersantegess;


    @SerializedName("roomVerification")
    @Expose
    private String roomVerification;

    @SerializedName("isEnabled")
    @Expose
    private boolean isEnabled;

    @SerializedName("user")
    @Expose
    private InstagramModel user;

    @SerializedName("greyImage")
    @Expose
    private String greyImage;


    public String getGreyImage() {
        return greyImage;
    }

    public void setGreyImage(String greyImage) {
        this.greyImage = greyImage;
    }

    public InstagramModel getUser() {
        return user;
    }

    public void setUser(InstagramModel user) {
        this.user = user;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(long messageCount) {
        this.messageCount = messageCount;
    }

    public String getRoomVerification() {
        return roomVerification;
    }

    public void setRoomVerification(String roomVerification) {
        this.roomVerification = roomVerification;
    }

    public Double getLikewisePersantegess() {
        return likewisePersantegess;
    }

    public void setLikewisePersantegess(Double likewisePersantegess) {
        this.likewisePersantegess = likewisePersantegess;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<AllMatched> getAllmatchpercent() {
        return allmatchpercent;
    }

    public void setAllmatchpercent(List<AllMatched> allmatchpercent) {
        this.allmatchpercent = allmatchpercent;
    }

    protected ResponseBean(Parcel in) {
        status = in.readString();
        id = in.readString();
        language = in.readString();
        code = in.readString();
        description = in.readString();
        picture = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        v = in.readLong();
        profilePic = in.readString();
        name = in.readString();
        dob = in.readString();
        coins = in.readString();
        dollar = in.readString();
        email = in.readString();
        notificationStatus = in.readLong();
        totalPoints = in.readLong();
        languageCode = in.createStringArrayList();
        coinStatus = in.readLong();
        password = in.readString();
        jwtToken = in.readString();
        deviceToken = in.readString();
        deviceType = in.readString();
        type = in.readString();
        notification = in.readByte() != 0;
        socialId = in.readString();
        socialType = in.readString();
        mobileNumber = in.readString();
        defaultLangCode = in.readString();
        nationalit = in.readString();
        state = in.readString();
        city = in.readString();
        interest = in.readString();
        country = in.readString();
        aboutus = in.readString();
        gender = in.readString();
        username = in.readString();
        message = in.readString();
        question = in.readString();
        answer = in.readString();
        themeName = in.createStringArrayList();
        images = in.createStringArrayList();
        room_id = in.readString();
        isChecked = in.readByte() != 0;
        gameDetails = in.readParcelable(GameDetails.class.getClassLoader());
        totalMessage = in.readInt();
        totalMatch = in.readInt();
        likeWisePersantege = in.readDouble();
        unicity = in.readDouble();
        coinLeft = in.readString();
        mode = in.readString();
        objectives = in.readString();
        morePreciselys = in.readString();
        user_type = in.readString();
        sender_id = in.readString();
        receiver_id = in.readString();
        game_id = in.readString();
        current_card = in.readInt();
        path = in.readString();
        list = in.createTypedArrayList(SocketModel.List.CREATOR);
        lives = in.readString();
        messageCount = in.readLong();
        isTaken = in.readByte() != 0;
        Code = in.readString();
        gameType = in.readString();
        gameCounts = in.readString();
        scores = in.readString();
        if (in.readByte() == 0) {
            totalMatchFind = null;
        } else {
            totalMatchFind = in.readInt();
        }
        if (in.readByte() == 0) {
            createMatchFind = null;
        } else {
            createMatchFind = in.readInt();
        }
        if (in.readByte() == 0) {
            commonMatchFind = null;
        } else {
            commonMatchFind = in.readInt();
        }
        if (in.readByte() == 0) {
            wordCountAlls = null;
        } else {
            wordCountAlls = in.readInt();
        }
        if (in.readByte() == 0) {
            finalFavoriteModes = null;
        } else {
            finalFavoriteModes = in.readInt();
        }
        if (in.readByte() == 0) {
            accuracy = null;
        } else {
            accuracy = in.readDouble();
        }
        timeUpdate = in.readString();
        if (in.readByte() == 0) {
            likewisePer = null;
        } else {
            likewisePer = in.readDouble();
        }
        userValue = in.readString();
    }

    public static final Creator<ResponseBean> CREATOR = new Creator<ResponseBean>() {
        @Override
        public ResponseBean createFromParcel(Parcel in) {
            return new ResponseBean(in);
        }

        @Override
        public ResponseBean[] newArray(int size) {
            return new ResponseBean[size];
        }
    };

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    public Double getLikewisePer() {
        return likewisePer;
    }

    public void setLikewisePer(Double likewisePer) {
        this.likewisePer = likewisePer;
    }

    public String getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(String timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    public ReceiverDetails getReceiver() {
        return receiver;
    }

    public void setReceiver(ReceiverDetails receiver) {
        this.receiver = receiver;
    }

    public List<CoinPay> getCoinPay() {
        return coinPay;
    }

    public void setCoinPay(List<CoinPay> coinPay) {
        this.coinPay = coinPay;
    }

    public List<CoinModel> getCoinModel() {
        return coinModel;
    }

    public void setCoinModel(List<CoinModel> coinModel) {
        this.coinModel = coinModel;
    }

    public ReceiverDetails getUserData() {
        return userData;
    }

    public void setUserData(ReceiverDetails userData) {
        this.userData = userData;
    }

    public String getDollar() {
        return dollar;
    }

    public void setDollar(String dollar) {
        this.dollar = dollar;
    }

    public Integer getTotalMatchFind() {
        return totalMatchFind;
    }

    public void setTotalMatchFind(Integer totalMatchFind) {
        this.totalMatchFind = totalMatchFind;
    }

    public Integer getCreateMatchFind() {
        return createMatchFind;
    }

    public void setCreateMatchFind(Integer createMatchFind) {
        this.createMatchFind = createMatchFind;
    }

    public Integer getCommonMatchFind() {
        return commonMatchFind;
    }

    public void setCommonMatchFind(Integer commonMatchFind) {
        this.commonMatchFind = commonMatchFind;
    }

    public Integer getWordCountAlls() {
        return wordCountAlls;
    }

    public void setWordCountAlls(Integer wordCountAlls) {
        this.wordCountAlls = wordCountAlls;
    }

    public Integer getFinalFavoriteModes() {
        return finalFavoriteModes;
    }

    public void setFinalFavoriteModes(Integer finalFavoriteModes) {
        this.finalFavoriteModes = finalFavoriteModes;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public List<CommonModelDataObject.CountryList> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CommonModelDataObject.CountryList> countryList) {
        this.countryList = countryList;
    }

    public String getGameCounts() {
        return gameCounts;
    }

    public void setGameCounts(String gameCounts) {
        this.gameCounts = gameCounts;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public PointDetails getDetails() {
        return details;
    }

    public void setDetails(PointDetails details) {
        this.details = details;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public List<CardImageModel> getCardImage() {
        return cardImage;
    }

    public void setCardImage(List<CardImageModel> cardImage) {
        this.cardImage = cardImage;
    }

    public List<FindPatner> getFindPatners() {
        return findPatners;
    }

    public void setFindPatners(List<FindPatner> findPatners) {
        this.findPatners = findPatners;
    }

    public List<FindPatner> getFindPatner() {
        return findPatners;
    }

    public void setFindPatner(List<FindPatner> getFindPatner) {
        this.findPatners = getFindPatner;
    }

    public ResponseBean()
    {

    }


    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public String getLives() {
        return lives;
    }

    public void setLives(String lives) {
        this.lives = lives;
    }

    public List<SocketModel.List> getList() {
        return list;
    }

    public void setList(List<SocketModel.List> list) {
        this.list = list;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCurrent_card() {
        return current_card;
    }

    public void setCurrent_card(int current_card) {
        this.current_card = current_card;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public PointDetails getPointDetails() {
        return pointDetails;
    }

    public void setPointDetails(PointDetails pointDetails) {
        this.pointDetails = pointDetails;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getMorePreciselys() {
        return morePreciselys;
    }

    public void setMorePreciselys(String morePreciselys) {
        this.morePreciselys = morePreciselys;
    }

    public String getCoinLeft() {
        return coinLeft;
    }

    public void setCoinLeft(String coinLeft) {
        this.coinLeft = coinLeft;
    }

    public List<MessageResponse> getChatData() {
        return chatData;
    }

    public void setChatData(List<MessageResponse> chatData) {
        this.chatData = chatData;
    }

    public List<FollowerDetail> getFollowerDetail() {
        return followerDetail;
    }

    public void setFollowerDetail(List<FollowerDetail> followerDetail) {
        this.followerDetail = followerDetail;
    }

    public int getTotalMessage() {
        return totalMessage;
    }

    public void setTotalMessage(int totalMessage) {
        this.totalMessage = totalMessage;
    }

    public int getTotalMatch() {
        return totalMatch;
    }

    public void setTotalMatch(int totalMatch) {
        this.totalMatch = totalMatch;
    }

    public double getLikeWisePersantege() {
        return likeWisePersantege;
    }

    public void setLikeWisePersantege(double likeWisePersantege) {
        this.likeWisePersantege = likeWisePersantege;
    }

    public double getUnicity() {
        return unicity;
    }

    public void setUnicity(double unicity) {
        this.unicity = unicity;
    }

    public ReceiverDetails getReceiverDetails() {
        return receiverDetails;
    }

    public void setReceiverDetails(ReceiverDetails receiverDetails) {
        this.receiverDetails = receiverDetails;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public GameDetails getGameDetails() {
        return gameDetails;
    }

    public void setGameDetails(GameDetails gameDetails) {
        this.gameDetails = gameDetails;
    }

    public GameDetails.MorePrecisely getMorePrecisely() {
        return morePrecisely;
    }

    public void setMorePrecisely(GameDetails.MorePrecisely morePrecisely) {
        this.morePrecisely = morePrecisely;
    }

    public GameDetails.GameCriteriaExplanations getGameCriteriaExplanations() {
        return gameCriteriaExplanations;
    }

    public void setGameCriteriaExplanations(GameDetails.GameCriteriaExplanations gameCriteriaExplanations) {
        this.gameCriteriaExplanations = gameCriteriaExplanations;
    }

    public GameDetails.LanguageDetails getLanguageDetails() {
        return languageDetails;
    }

    public void setLanguageDetails(GameDetails.LanguageDetails languageDetails) {
        this.languageDetails = languageDetails;
    }


    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public List<String> getThemeName() {
        return themeName;
    }

    public void setThemeName(List<String> themeName) {
        this.themeName = themeName;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Objective> getObjective() {
        return objective;
    }

    public List<Laguage> getLaguage() {
        return laguage;
    }

    public void setLaguage(List<Laguage> laguage) {
        this.laguage = laguage;
    }

    public void setObjective(List<Objective> objective) {
        this.objective = objective;
    }

    public List<Morerecisely> getMorerecisely() {
        return morerecisely;
    }

    public void setMorerecisely(List<Morerecisely> morerecisely) {
        this.morerecisely = morerecisely;
    }

    public List<FbFriend> getFbFriends() {
        return fbFriends;
    }

    public void setFbFriends(List<FbFriend> fbFriends) {
        this.fbFriends = fbFriends;
    }



    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAboutus() {
        return aboutus;
    }

    public void setAboutus(String aboutus) {
        this.aboutus = aboutus;
    }


    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getNationalit() {
        return nationalit;
    }

    public void setNationalit(String nationalit) {
        this.nationalit = nationalit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public String getDefaultLangCode() {
        return defaultLangCode;
    }

    public void setDefaultLangCode(String defaultLangCode) {
        this.defaultLangCode = defaultLangCode;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(long notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public List<String> getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(List<String> languageCode) {
        this.languageCode = languageCode;
    }

    public long getCoinStatus() {
        return coinStatus;
    }

    public void setCoinStatus(long coinStatus) {
        this.coinStatus = coinStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
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



    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public long getV() {
        return v;
    }

    public void setV(long v) {
        this.v = v;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(id);
        dest.writeString(language);
        dest.writeString(code);
        dest.writeString(description);
        dest.writeString(picture);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeLong(v);
        dest.writeString(profilePic);
        dest.writeString(name);
        dest.writeString(dob);
        dest.writeString(coins);
        dest.writeString(dollar);
        dest.writeString(email);
        dest.writeLong(notificationStatus);
        dest.writeLong(totalPoints);
        dest.writeStringList(languageCode);
        dest.writeLong(coinStatus);
        dest.writeString(password);
        dest.writeString(jwtToken);
        dest.writeString(deviceToken);
        dest.writeString(deviceType);
        dest.writeString(type);
        dest.writeByte((byte) (notification ? 1 : 0));
        dest.writeString(socialId);
        dest.writeString(socialType);
        dest.writeString(mobileNumber);
        dest.writeString(defaultLangCode);
        dest.writeString(nationalit);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeString(interest);
        dest.writeString(country);
        dest.writeString(aboutus);
        dest.writeString(gender);
        dest.writeString(username);
        dest.writeString(message);
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeStringList(themeName);
        dest.writeStringList(images);
        dest.writeString(room_id);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeParcelable(gameDetails, flags);
        dest.writeInt(totalMessage);
        dest.writeInt(totalMatch);
        dest.writeDouble(likeWisePersantege);
        dest.writeDouble(unicity);
        dest.writeString(coinLeft);
        dest.writeString(mode);
        dest.writeString(objectives);
        dest.writeString(morePreciselys);
        dest.writeString(user_type);
        dest.writeString(sender_id);
        dest.writeString(receiver_id);
        dest.writeString(game_id);
        dest.writeInt(current_card);
        dest.writeString(path);
        dest.writeTypedList(list);
        dest.writeString(lives);
        dest.writeLong(messageCount);
        dest.writeByte((byte) (isTaken ? 1 : 0));
        dest.writeString(Code);
        dest.writeString(gameType);
        dest.writeString(gameCounts);
        dest.writeString(scores);
        if (totalMatchFind == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalMatchFind);
        }
        if (createMatchFind == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(createMatchFind);
        }
        if (commonMatchFind == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(commonMatchFind);
        }
        if (wordCountAlls == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(wordCountAlls);
        }
        if (finalFavoriteModes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(finalFavoriteModes);
        }
        if (accuracy == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(accuracy);
        }
        dest.writeString(timeUpdate);
        if (likewisePer == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(likewisePer);
        }
        dest.writeString(userValue);
    }
}

