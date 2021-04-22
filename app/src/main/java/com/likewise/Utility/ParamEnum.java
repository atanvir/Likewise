package com.likewise.Utility;

public enum ParamEnum {
    Success("true"),
    Failure("false"),
    Register("register"),
    Facebook("facebook"),
    Guest("Guest"),
    CameFrom("cameFrom"),
    DeviceType("Android"),
    SENDER_ID("sender_id"),
    ROOM_ID("room_id"),
    GAME_ID("game_id"),
    MODE("mode"),
    CURRENT_CARD("current_card"),
    LIVES("lives"),
    APP_ID("ca-app-pub-1802088637385289~8549311072"),
    UNIT_ID("ca-app-pub-1802088637385289/2082268786"),
//    UNIT_ID("ca-app-pub-1802088637385289/1528581816"),
    DRAWER("drawer"),
    ENTER_MSG("enter_msg"),
    RECIVER_ID("receiver_id"),
    CLIENT_ID_INSTAGRAM("3420302034691143"),
    CLIENT_SECRET_INSTAGRAM("b96b2f8c2516e716997e385f2812fca5"),
    CALLBACK_URL_INSTAGRAM("https://likewise.chat/"),
    AUTHORIZATION_INSTAGRAM("https://api.instagram.com/oauth/authorize/"),
    GRANT_TYPE("authorization_code"),
    Instagram("Instagram"),
    POINTS("POINTS");
    private final String value;
    ParamEnum(String value) {
        this.value=value;
    }
    ParamEnum(){
        this.value=null;
    }
    public String theValue() {
        return this.value;
    }
}
