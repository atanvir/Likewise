package com.likewise.Firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.likewise.Activity.MainActivity;
import com.likewise.BottomSheet.NotificationBottomSheet;
import com.likewise.R;
import com.likewise.Utility.CommonUtils;

import java.util.Random;

public class MyFirebaseMessageService extends FirebaseMessagingService {
    String TAG=MyFirebaseMessageService.class.getSimpleName();
    public static INotificationCount listner;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG,"data:"+remoteMessage.getData());
        Log.e(TAG,"notification:"+remoteMessage.getNotification());
        if(remoteMessage.getData().size()>0) {
            handleMessage(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("msgCount"), remoteMessage.getData().get("roomId"));
        }
        else if(remoteMessage.getNotification()!=null) {
            handleMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), null, null);
        }

    }

    private void handleMessage(String title, String body,String msgCount,String roomId) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("cameFrom", MyFirebaseMessageService.class.getSimpleName());
        if(title.contains("sends you an invitation!")) {
            intent.putExtra("invite", "true");
        }
        if (listner != null) listner.count(msgCount, roomId);
        showNotificationMessage(getApplicationContext(), title, body, intent, new Random().nextInt());
        playNotificationSound();
    }

    public void showNotificationMessage(Context context, String title, String message, Intent intent, int id) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification(title, message, intent, id);
    }


    public void playNotificationSound() {
        try {
            MediaPlayer mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setListner(INotificationCount listen)
    {
        listner=listen;
    }

    public interface INotificationCount
    {
        void count(String msgCount,String roomId);
    }



}
