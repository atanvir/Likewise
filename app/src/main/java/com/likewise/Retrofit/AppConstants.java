package com.likewise.Retrofit;

import java.io.Serializable;

public interface AppConstants extends Serializable {
    String LOGIN= "login";
    String SIGNUP="signup";
    String SOCIAL_LOGIN="socialLogin";
    String FORGOT_PASSWORD="forgetpassword";
    String CHECK_EMAIL="checkEmail";
    String LIST_LANGUAGE="listLanguage";
    String GUEST_USER="guestLogin";
    String VIEW_PROFILE="getUserDetails";
    String UPDATE_PROFILE="updateUserProfile";
    String CHECK_SOCIAL="checkSocalId";
    String LIST_FAQ="listFaq";
    String LIST_PRIVACY_POLICY="listPrivacyPolicy";
    String ADD_FEEDBACK="addFeedback";
    String LOGOUT="logout";
    String DELETE_ACCOUNT="deleteUserAcount";
    String NOTIFICATION_UPDATE="notificationUpdate";
    String CREATE_GAME_DATA="listExplanation";
    String PUBLIC_GALLERY_IMAGES ="listImage" ;
    String GAME_CREATE ="gameCreate" ;
    String ROOM_CREATE ="roomCreate" ;
    String GAME_DETAILS ="gameDetails" ;
    String CHAT_HISTORY ="chatData" ;
    String GAME_OVER ="gameOver" ;
    String GAME_LIST ="chatUserList" ;
    String NOTIFICATION_LIST ="notificationListWithToken" ;
    String FIND_PATNER ="getFindPatner" ;
    String RANDOM_CARD = "countCardValue";
    String MY_MATCHES = "myMatch";
    String OTHER_USER_DETAILS = "getOtherUserDetails/{id}";
    String LEADERBOARD = "leaderboard";
    String GET_COINS_LIST = "listMobileCoin";
    String DAILY_COINS = "dailyCheckCoin";
    String ADD_COINS = "addCoins";
    String CHECK_COIN_DEDUCT = "checkCoinRemove";
    String DAILY_COIN_COLLECT="addDailyCoin";
    String INVITE_FRIEND = "inviteFriend";
    String INSTAGRAM_ACCESS_TOKEN = "access_token";
    String INSTAGRAM_BASIC_DISPLAY = "{socialId}";
    String INSTAGRAM_USER_DATA = "{username}/?__a=1";
    String INSTAGRAM_FOLLOWERS = "follows";
    String SEARCH_USERS = "searchUser";
}
