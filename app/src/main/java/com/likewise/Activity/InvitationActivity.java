package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.likewise.Adapter.NotificationAdapter;
import com.likewise.Interface.SocketCallbacks;
import com.likewise.Model.MessageResponse;
import com.likewise.Model.SocketModel;
import com.likewise.R;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.SocketHelper.SocketConnection;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.databinding.ActivityInvitationBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

import io.socket.client.Socket;

public class InvitationActivity extends AppCompatActivity implements View.OnClickListener, SocketCallbacks {

    ActivityInvitationBinding binding;
    String has = getColoredSpanned("has ", "#000000");
    String rest = getColoredSpanned(" seconds left <br> to accept the game and play online","#000000");
    CountDownTimer countDownTimer;
    boolean isTimerRunning=false;
    Socket socket;
    boolean responseStatus=false;
    private boolean isRequestCancel=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_invitation);
        ImageGlider.setRoundImage(this,binding.ivprofilePic,binding.progresBar,getIntent().getStringExtra("profilepic"));
        binding.tvName.setText(getIntent().getStringExtra("name"));

        try {
            socket=SocketConnection.connect(this);
            if(!socket.connected())
            {
                socket.connect();
            }
        } catch (URISyntaxException  e) {
            e.printStackTrace();
        }
        binding.closeIv.setOnClickListener(this);

            countDownTimer = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    isTimerRunning=true;
                    binding.timerText.setText(Html.fromHtml("<div style=\"text-align: center\">" + has + "<big><b>" + getColoredSpanned("" + (int) (millisUntilFinished / 1000), "#FF0E25") + "</b></big>" + rest + "</div>"));
                }

                public void onFinish() {
                    if(isTimerRunning) {
                        isTimerRunning = false;
                        Intent intent = new Intent(InvitationActivity.this, NoPatnerFoundActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }

            };
            countDownTimer.start();

    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.closeIv:
            showAlearDailog("Are you sure?","Do you really want to cancel the invitation?");
            break;

        }
    }

    private void showAlearDailog(String title, String desc) {
        final Dialog dialog=new Dialog(this,android.R.style.Theme_Black);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_common_aleart);
        TextView actionText=dialog.findViewById(R.id.actionText);
        TextView cancelText=dialog.findViewById(R.id.cancelText);
        TextView descText=dialog.findViewById(R.id.descText);
        TextView titleText=dialog.findViewById(R.id.titleText);
        titleText.setText(title);
        descText.setText(desc);
        dialog.setCancelable(true);
        cancelText.setText("No");
        actionText.setText("Yes");
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        actionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                CommonUtils.showLoadingDialog(InvitationActivity.this);
                if(!socket.connected())
                {
                    socket.connected();
                }
                try {
                    isRequestCancel=true;
                    JSONObject object = new JSONObject();
                    object.put("_id", getIntent().getStringExtra("notificationId"));
                    object.put("sender_id", SharedPreferenceWriter.getInstance(InvitationActivity.this).getString(SPreferenceKey.ID));
                    object.put("receiver_id", getIntent().getStringExtra("receiver_id"));
                    object.put("game_id", getIntent().getStringExtra("gameId"));
                    object.put("type", "1");
                    socket.emit("reject", object);
                    Log.e("reject", "yes");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {
//        if(isTimerRunning)
//        {
//            isTimerRunning=false;
//            countDownTimer.cancel();
//            countDownTimer.onFinish();
//        }
//        Intent intent= new Intent(this,MainActivity.class);
//        finish();
//        startActivity(intent);
    }

    @Override
    public void onConnect(Object... args) {

    }

    @Override
    public void onDisconnect(Object... args) {

    }

    @Override
    public void onConnectError(Object... args) {

    }

    @Override
    public void onNotificationCount(Object... args) {

    }


    @Override
    public void onRequestAccept(Object... args) { runOnThread("accept",args[0].toString()); }

    @Override
    public void onRequestDecline(Object... args) {
        runOnThread("reject",args[0].toString());
    }

    @Override
    public void onRoomJoin(Object... args) {

    }

    @Override
    public void onMessage(Object... args) {

    }

    @Override
    public void onRoomUpdate(Object... args) {

    }

    @Override
    public void onCardPass(Object... args) {

    }

    @Override
    public void onLivePass(Object... args) {

    }

    @Override
    public void onTyping(Object... args) {

    }

    @Override
    public void onStopTyping(Object... args) {

    }

    @Override
    public void onGameDataSave(Object... args) {

    }

    @Override
    public void onCreateRoom(Object... args) {

    }

    @Override
    public void onCoinDeducted(Object... args) {

    }

    @Override
    public void onBroadcast(Object... args) {

    }

    @Override
    public void onInviteAccept(Object... args) {
        runOnThread("acceptInvite",args);
    }


    private void runOnThread(String eventName,Object... args) {
        Log.e(eventName,args[0].toString());
        SocketModel model = new Gson().fromJson(args[0].toString(), SocketModel.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    if (eventName.equalsIgnoreCase("accept")) {
                        if (checkUserData(model)) {
                            if (isTimerRunning) {
                                isTimerRunning = false;
                                countDownTimer.cancel();
                                countDownTimer.onFinish();
                            }
                            if (model.getDataTosave().getSenderId().equalsIgnoreCase(getIntent().getStringExtra("receiver_id")) &&
                                    model.getDataTosave().getReceiverId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(InvitationActivity.this).getString(SPreferenceKey.ID))) {
                                model.getDataTosave().setReceiverId(model.getDataTosave().getSenderId());
                                Intent intent = new Intent(InvitationActivity.this, ChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("userData", model);
                                startActivity(intent);
                            }
                        }
                    } else if (eventName.equalsIgnoreCase("reject")) {
                        if (checkUserData2(model)) {
                            if (isTimerRunning) {
                                isTimerRunning = false;
                                countDownTimer.cancel();
                                countDownTimer.onFinish();
                            }
                            if(isRequestCancel)
                            {
                                CommonUtils.dismissLoadingDialog();
                            }
                                Toast.makeText(InvitationActivity.this, "Request Declined", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InvitationActivity.this, MainActivity.class);
                                startActivity(intent);
                        }
                    }
                }
        });

    }

    private boolean checkUserData(SocketModel response) {
        boolean ret=false;
        if(response.getDataTosave().getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)) || response.getDataTosave().getReceiverId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)) &&
           response.getDataTosave().getReceiverId().equalsIgnoreCase(getIntent().getStringExtra("receiver_id")) || response.getDataTosave().getSenderId().equalsIgnoreCase(getIntent().getStringExtra("receiver_id"))
           && response.getDataTosave().getGameId().equalsIgnoreCase(getIntent().getStringExtra("gameId")))
        {
            ret=true;
        }

        return ret;
    }
    private boolean checkUserData2(SocketModel response) {
        boolean ret=false;
        if(response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)) || response.getReceiverId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)) &&
           response.getReceiverId().equalsIgnoreCase(getIntent().getStringExtra("receiver_id")) || response.getSenderId().equalsIgnoreCase(getIntent().getStringExtra("receiver_id"))
           && response.getGame_id().equalsIgnoreCase(getIntent().getStringExtra("gameId")))
        {
            ret=true;
        }

        return ret;
    }


}
