package com.likewise.Interface;

import org.json.JSONException;

 public  interface SocketCallbacks {
     //Common
    void onConnect(Object... args) ;
    void onDisconnect(Object... args);
    void onConnectError(Object... args);

    //Notification Specifice
    void onNotificationCount(Object... args);
    void onRequestAccept(Object... args);
    void onRequestDecline(Object... args);

    //Chat Specifice
    void onRoomJoin(Object... args);
    void onMessage(Object... args);
    void onRoomUpdate(Object... args);
    void onCardPass(Object... args);
    void onLivePass(Object... args);
    void onTyping(Object... args);
    void onStopTyping(Object... args);
    void onGameDataSave(Object... args);
    void onCreateRoom(Object... args);
    void onCoinDeducted(Object... args);
    void onBroadcast(Object... args);


    //Invite Accept
    void onInviteAccept(Object... args);
}
