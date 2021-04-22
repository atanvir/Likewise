package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.likewise.BottomSheet.FacebookBottomSheet;
import com.likewise.BottomSheet.InsufficientCoinsBottomSheet;
import com.likewise.Interface.SocketCallbacks;
import com.likewise.Model.CameraModel;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.CreateGameModel;
import com.likewise.Model.CreateGameResponse;
import com.likewise.Model.GalleryModel;
import com.likewise.Model.MessageResponse;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.SocketHelper.SocketConnection;
import com.likewise.Utility.AddRequestBody;
import com.likewise.Utility.AllImagesSingleton;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityGameOverBinding;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hari.bounceview.BounceView;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener, SocketCallbacks {

    ActivityGameOverBinding binding;
    //Toolbar
    TextView headerText;
    ImageView qstIv,closeIv;
    Socket socket;
    int revealCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_game_over);
        init();
        gameOverApi(getIntent().getStringExtra("room_id"),getIntent().getStringExtra("receiver_id"));
        try {
            socket= SocketConnection.connect(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.tvCoins.setText(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.COINS));

    }

    private void gameOverApi(String room_id, String receiver_id) {
//        room_id="88270419519693455f0863d69de4c5631f933658929346785f1bd2b8ee92383dfde4511c8583632039091462";
//        receiver_id="5f1bd2b8ee92383dfde4511c";
        if(CommonUtils.networkConnectionCheck(this)) {

            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.gameOver(room_id,receiver_id,SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID),SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.COINS));
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                if(serverResponse.getData()!=null)
                                {
                                    setUpData(serverResponse.getData());
                                }
                                CommonUtils.playSound("game_over",GameOverActivity.this);

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(GameOverActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(GameOverActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            CommonUtils.showSnackBar(GameOverActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }
    private void setUpData(ResponseBean data) {
        long points= data.getFollowerDetail().get(0).getPointMax()+Long.parseLong(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.POINTS));
        SharedPreferenceWriter.getInstance(GameOverActivity.this).writeStringValue(SPreferenceKey.POINTS,""+points);
        revealCoins=data.getCoinModel().get(0).getSpeedToRevealChat();
        if(data.getReceiverDetails().getName()!=null) {
            if (data.getReceiverDetails().getName().contains(" ")) {
                binding.btnViewChat.setText("Spend " + data.getCoinModel().get(0).getSpeedToRevealChat() + " coins to view " + data.getReceiverDetails().getName().split(" ")[0] + "\'s chat");
            } else {
                binding.btnViewChat.setText("Spend " + data.getCoinModel().get(0).getSpeedToRevealChat() + " coins to view " + data.getReceiverDetails().getName() + "\'s chat");

            }
        }
        binding.tvPoints.setText(""+data.getFollowerDetail().get(0).getPointMax());
        ImageGlider.setRoundImage(this,binding.ivUser,binding.pbUser, SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.PROFILE_PIC));
        binding.tvUsername.setText(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.NAME));
        ImageGlider.setRoundImage(this,binding.ivOpponent,binding.pbOpponent,data.getReceiverDetails().getProfilePic());
        if(data.getReceiverDetails().getName()!=null) {
            binding.tvOpponentName.setText(data.getReceiverDetails().getName());
        }
        new CommonUtils().setProgressbar(binding.ProgressBar,binding.progressPercText, (int) Math.round(data.getLikeWisePersantege()));
        binding.tvMatchedEnteries.setText(data.getTotalMessage()+"%");
        binding.tvMatchWords.setText(data.getTotalMatch()+"%");
        binding.tvStreak.setText("+"+data.getFollowerDetail().get(0).getSumStreaks()+"%");
        binding.tvUnicity.setText(""+data.getUnicity()+"%");
        binding.tvCoins.setText(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.COINS));
    }
    private void init() {
        headerText=findViewById(R.id.headerText);
        closeIv=findViewById(R.id.closeIv);
        closeIv.setOnClickListener(this);
        qstIv=findViewById(R.id.qstIv);
        qstIv.setVisibility(View.GONE);
        headerText.setText(getString(R.string.game_over));
        binding.coinsBtn.setOnClickListener(this);
        binding.btnViewChat.setOnClickListener(this);
        BounceView.addAnimTo(binding.coinsBtn);
        BounceView.addAnimTo(binding.btnViewChat);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.coinsBtn:
                intent(GetCoinsActivity.class) ;
                break;

            case R.id.closeIv:
                onBackPressed();
                break;


            case R.id.btnViewChat:
                int currentCoins= Integer.parseInt(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.COINS));
                if(!(currentCoins>=revealCoins))
                {
                    InsufficientCoinsBottomSheet sheet = new InsufficientCoinsBottomSheet();
                    sheet.show(getSupportFragmentManager(),"");

                }else {
                    CommonUtils.playSound("reveal_chat", GameOverActivity.this);
                    try {
                        JSONObject object = new JSONObject();
                        object.put("_id", SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID));
                        object.put("game_id", getIntent().getStringExtra("game_id"));
                        socket.emit("coin_dedicated", object);
                        Log.e("coin_deduct", object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;

        }
    }

    private void intent(Class<? extends Object> className) {
        Intent intent=new Intent(GameOverActivity.this,className);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("room_id",getIntent().getStringExtra("room_id"));
        intent.putExtra("_id",getIntent().getStringExtra("receiver_id"));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        finish();
        startActivity(intent);
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
    public void onRequestAccept(Object... args) {

    }

    @Override
    public void onRequestDecline(Object... args) {

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
        runOnThread("coin_dedicated",args);
    }

    @Override
    public void onBroadcast(Object... args) {

    }


    @Override
    public void onInviteAccept(Object... args) {

    }



    private void runOnThread(String eventName, Object... args) {
        Log.e(eventName,""+args[0].toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MessageResponse response =new Gson().fromJson(args[0].toString(),MessageResponse.class);
                if(eventName.equalsIgnoreCase("coin_dedicated"))
                {
                    if(response.getSenderId()==null)
                    {
                        if(response.getMessage().equalsIgnoreCase("Insufficient Coins"))
                        {
                            InsufficientCoinsBottomSheet bottomSheet = new InsufficientCoinsBottomSheet();
                            bottomSheet.show(getSupportFragmentManager(),"");
                        }

                    }

                    else if(response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(GameOverActivity.this).getString(SPreferenceKey.ID))) {
                        SharedPreferenceWriter.getInstance(GameOverActivity.this).writeStringValue(SPreferenceKey.COINS, response.getCoins());
                        if(response.getMessage()==null) intent(ViewChatActivity.class);
                        else if(response.getMessage().equalsIgnoreCase("Insufficient Coins"))
                        {
                            InsufficientCoinsBottomSheet bottomSheet = new InsufficientCoinsBottomSheet();
                            bottomSheet.show(getSupportFragmentManager(),"");
                        }
                    }
                }
            }
        });
    }

}
