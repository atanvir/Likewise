package com.likewise.SocketHelper;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.likewise.Activity.ChatActivity;
import com.likewise.Activity.InvitationActivity;
import com.likewise.Activity.MainActivity;
import com.likewise.Interface.SocketCallbacks;

import org.json.JSONException;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketConnection extends AppCompatActivity {
    private static Socket mSocket;
    private static SocketCallbacks listners;

    public static Socket connect(SocketCallbacks listner) throws URISyntaxException {
         listners=listner;
         if(mSocket!=null)
         {
             disconnect();
         }
//         mSocket= IO.socket("http://18.224.114.141:8001");
  //       mSocket= IO.socket("http://3.128.167.18:8001");
         mSocket= IO.socket("http://18.224.247.81:8001");
         mSocket.on(Socket.EVENT_CONNECT, onConnect);
         mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
         mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
         mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
         mSocket.on("count", onNotificationCount);
         mSocket.on("accept",onRequestAccept);
         mSocket.on("reject",onRequestDecline);
         mSocket.on("room_join",onRoomJoin);
         mSocket.on("message",onMessage);
         mSocket.on("room_update",onRoomUpdate);
//         mSocket.on("acceptInvite",onAcceptInvite);
         mSocket.on("card_pass",onCardPass);
         mSocket.on("typing_in",onTyping);
         mSocket.on("stop_typing",onStopTyping);
         mSocket.on("game_back",onGameDataSave);
         mSocket.on("create_room",onCreateRoom);
         mSocket.on("lives_pass",onLivePass);
         mSocket.on("coin_dedicated",onCoinDeducted);
         mSocket.on("broadcast",onBroadcast);
         mSocket.connect();
        return mSocket;
    }


    private static Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            listners.onDisconnect(args);
        }
    };

    private static Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(listners!=null) {
                listners.onConnect(args);
            }
        }
    };


    private static Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(listners!=null) {
                listners.onConnectError(args);
            }
        }
    };

    private static Emitter.Listener onNotificationCount = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(listners!=null) { listners.onNotificationCount(args);
            }
        }
    };


    private static Emitter.Listener onRequestAccept = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(listners!=null)  listners.onRequestAccept(args);
        }
    };

    private static Emitter.Listener onRequestDecline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onRequestDecline(args);
        }
    };

    private static Emitter.Listener onRoomJoin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onRoomJoin(args);
        }
    };

    private static Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onMessage(args);
        }
    };

    private static Emitter.Listener onRoomUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onRoomUpdate(args);
        }
    };

    private static Emitter.Listener onAcceptInvite = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onInviteAccept(args);
        }
    };

  private static Emitter.Listener onCardPass = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onCardPass(args);
        }
    };


  private static Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onTyping(args);
        }
    };

  private static Emitter.Listener onGameDataSave = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onGameDataSave(args);
        }
    };

  private static Emitter.Listener onCreateRoom = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onCreateRoom(args);
        }
    };

  private static Emitter.Listener onLivePass = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onLivePass(args);
        }
    };

  private static Emitter.Listener onCoinDeducted = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onCoinDeducted(args);
        }
    };

  private static Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onStopTyping(args);
        }
    };

  private static Emitter.Listener onBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if(listners!=null) listners.onBroadcast(args);
        }
    };

    public static void disconnect()
    {
        if(mSocket!=null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("count", onNotificationCount);
            //Notification_list
            mSocket.off("accept",onRequestAccept);
            mSocket.off("reject",onRequestDecline);
            mSocket.off("room_join",onRequestDecline);
            mSocket.off("message",onMessage);
            mSocket.off("room_update",onRoomUpdate);
            mSocket.off("card_pass",onCardPass);
            mSocket.off("typing_in",onTyping);
            mSocket.off("game_back",onGameDataSave);
            mSocket.off("create_room",onCreateRoom);
            mSocket.off("lives_pass",onLivePass);
            mSocket.off("coin_dedicated",onCoinDeducted);
            mSocket.off("stop_typing",onStopTyping);
            mSocket.off("broadcast",onBroadcast);
            mSocket=null;
        }
    }



}
