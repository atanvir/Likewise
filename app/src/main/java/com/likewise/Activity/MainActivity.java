package com.likewise.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ClipDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.likewise.Adapter.GameFragmentPagerAdapter;
import com.likewise.BottomSheet.DailyCoinsBottomSheet;
import com.likewise.BottomSheet.FacebookBottomSheet;
import com.likewise.BottomSheet.InsufficientCoinsBottomSheet;
import com.likewise.BottomSheet.LoginFirstBottomSheet;
import com.likewise.BottomSheet.NotificationBottomSheet;

import com.likewise.Firebase.MyFirebaseMessageService;
import com.likewise.Firebase.NotificationHelper;
import com.likewise.Fragment.GameFragment;
import com.likewise.Interface.SheetItemClickListner;
import com.likewise.Interface.SocketCallbacks;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.ResponseBean;
import com.likewise.Model.SocketModel;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SPrefrenceValues;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.SocketHelper.SocketConnection;

import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.FacebookLogin;
import com.likewise.Utility.InstagramLogin;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.Inet4Address;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hari.bounceview.BounceView;
import hari.bounceview.BuildConfig;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SocketCallbacks, SheetItemClickListner, DailyCoinsBottomSheet.OnCoinsData, MyFirebaseMessageService.INotificationCount, OnCompleteListener<AuthResult>, ValueEventListener, InstagramLogin.OAuthAuthenticationListener {

    ActivityMainBinding binding;

    //drawer Layout
    ImageView menuIv;
    ConstraintLayout profileandLangCl,gameCl,leaderCl,shareAppCl, moreCoinCl,settingCl,faqCl,feedbackCl;
    ImageView ivUser;
    TextView userNameTv,tvLabelBottom;
    ProgressBar progressBar;

    //bottom navigation
    ImageView playIv,matchIv,createGameIv;
    GameFragmentPagerAdapter gameFragmentPagerAdapter;
    int clickCoount=0;

    ClipDrawable clipDrawable;

    //Toolbar
    ImageView notiIv;
    TextView badgeText,tvCoins,tvPoints;
    LinearLayout coinLL,pointsLL;

    private static final int FACEBOOK_DATA = 27;
    private Socket mSocket;
    private NotificationBottomSheet bottomSheet;
    private static IRequest iRequest;
    private boolean isBack=false;
    private static coinDeducted listner;
    private static UnReadMessageListner unReadMessagelistner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main);
        MyFirebaseMessageService.setListner(this);
        init();
      //  tvLabelBottom.setText(R.string.my_games);
        binding.gameText.setText(R.string.my_games);

    }

    public static void setListnerCoin(coinDeducted listen)
    {
        listner=listen;
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
            if(!((Boolean) postSnapshot.child("isReceived").getValue()))
            {
                inviteFriendApi((String) postSnapshot.child("senderId").getValue(),(String) postSnapshot.child("coins").getValue());
            }
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Log.e("firebase DB", error.getMessage());
    }

    @Override
    public void onSuccess(String userName,String socialId,String fullName,String profilePic) {
        Intent data=new Intent();
        data.putExtra("id",socialId);
        data.putExtra("email", userName);
        data.putExtra("name", fullName);
        data.putExtra("image", profilePic);
        checkSocalIdApi(data,ParamEnum.Instagram.theValue());
    }

    @Override
    public void onFail(String error) {
        CommonUtils.showSnackBar(this,error,ParamEnum.Failure.theValue());
    }

    public interface UnReadMessageListner{
        void unReadMessageCount(long msgCount,String gameId);
    }

    public static void setUnreadMessageListner(UnReadMessageListner listn)
    {
        unReadMessagelistner=listn;
    }

    private void checkDailyCoinsApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.checkDailyCoins();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                              dailyCoinsBottomSheet();
                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue())) {
                                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                                    FirebaseDatabase.getInstance().getReference().child("users").orderByChild("_id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(MainActivity.this);
                                }
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            CommonUtils.showSnackBar(MainActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }


    private void inviteFriendApi(String receiverId,String coins) {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.inviteFriend(SharedPreferenceWriter.getInstance(MainActivity.this).getString(SPreferenceKey.ID),receiverId,coins);
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("isReceived").setValue(true);

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                  CommonUtils.showSnackBar(MainActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            CommonUtils.showSnackBar(MainActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    private void dailyCoinsBottomSheet() {
        if(!SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("3"))
        {
            CommonUtils.playSound("press_button",MainActivity.this);
            DailyCoinsBottomSheet sheet = new DailyCoinsBottomSheet(this);
            sheet.show(getSupportFragmentManager(),"");
        }
    }

    @Override
    protected void onResume() {
    super.onResume();
        matchIv.setImageResource(R.drawable.match);
        clickCoount=0;
            try {
            ImageGlider.setRoundImage(this,ivUser,progressBar,SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.PROFILE_PIC));
            userNameTv.setText(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.NAME));
            mSocket=SocketConnection.connect(this);
            gameFragmentPagerAdapter =new GameFragmentPagerAdapter(getSupportFragmentManager(),"UnMatched",mSocket);
            binding.viewPager.setAdapter(gameFragmentPagerAdapter);
            binding.tabLayout.setupWithViewPager(binding.viewPager);
            tvCoins.setText(""+SharedPreferenceWriter.getInstance(MainActivity.this).getString(SPreferenceKey.COINS));
            if(getIntent().getStringExtra("invite")!=null)
            {
                notiIv.performClick();
            }
             else {
                checkDailyCoinsApi();
            }
        } catch (URISyntaxException e) {
            }
    }

    private void init() {
        //drawer
        menuIv = findViewById(R.id.menuIv);
        menuIv.setOnClickListener(this);
        profileandLangCl = findViewById(R.id.profileandLangCl);
        gameCl = findViewById(R.id.gameCl);
        leaderCl = findViewById(R.id.leaderCl);
        moreCoinCl = findViewById(R.id.moreCoinCl);
        shareAppCl = findViewById(R.id.shareAppCl);
        settingCl = findViewById(R.id.settingCl);
        faqCl = findViewById(R.id.faqCl);
        feedbackCl = findViewById(R.id.feedbackCl);
        ivUser = findViewById(R.id.ivUser);
        userNameTv = findViewById(R.id.userNameTv);
        progressBar=findViewById(R.id.progressBar);
        tvLabelBottom=findViewById(R.id.tvLabelBottom);


        profileandLangCl.setOnClickListener(this);
        gameCl.setOnClickListener(this);
        leaderCl.setOnClickListener(this);
        moreCoinCl.setOnClickListener(this);
        shareAppCl.setOnClickListener(this);
        settingCl.setOnClickListener(this);
        faqCl.setOnClickListener(this);
        feedbackCl.setOnClickListener(this);
        ivUser.setOnClickListener(this);
        userNameTv.setOnClickListener(this);

        //bottom navigation
        createGameIv = findViewById(R.id.createGameIv);
        matchIv = findViewById(R.id.matchIv);
        playIv = findViewById(R.id.playIv);
        createGameIv.setOnClickListener(this);
        matchIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
        BounceView.addAnimTo(createGameIv);
        BounceView.addAnimTo(playIv);
        BounceView.addAnimTo(matchIv);


        //Toolbar
        notiIv = findViewById(R.id.notiIv);
        badgeText = findViewById(R.id.badgeText);
        coinLL = findViewById(R.id.coinLL);
        pointsLL = findViewById(R.id.pointsLL);
        tvCoins=findViewById(R.id.tvCoins);
        tvPoints=findViewById(R.id.tvPoints);
        tvCoins.setText(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.COINS));
        tvPoints.setText(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.POINTS));
        notiIv.setOnClickListener(this);
        coinLL.setOnClickListener(this);
        pointsLL.setOnClickListener(this);
        ImageGlider.setRoundImage(this,ivUser,progressBar,SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.PROFILE_PIC));
        userNameTv.setText(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.NAME));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            //drawer layout
            case R.id.ivUser:
            Intent pointsIntent = new Intent(MainActivity.this, UserProfileActivity.class);
            pointsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(pointsIntent);
            break;

            case R.id.userNameTv:
            ivUser.performClick();
            break;

            case R.id.profileandLangCl:
            ivUser.performClick();
            break;


            case  R.id.gameCl:
            Intent intent1=new Intent(this,MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            break;

            case  R.id.leaderCl:
            intent(LeaderboardActivity.class);
            break;

           case  R.id.moreCoinCl:
           intent(GetCoinsActivity.class);
           break;

           case  R.id.shareAppCl:
           try {
               Intent shareIntent = new Intent(Intent.ACTION_SEND);
               shareIntent.setType("text/plain");
               shareIntent.putExtra(Intent.EXTRA_SUBJECT, "LikeWise");
               String shareMessage= "\nLet me recommend you this application\n\n";
               shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.likewise";
               shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
               startActivity(Intent.createChooser(shareIntent, "choose one"));
           } catch(Exception e) {
               //e.toString();
           }
           break;

           case  R.id.settingCl:
           intent(SettingActivity.class);
           break;


           case  R.id.faqCl:
            Intent faqIntent=new Intent(this,FAQActivity.class);
            faqIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(faqIntent);
            break;

           case  R.id.feedbackCl:
           Intent feediIntent = new Intent(this, FeedbackActivity.class);
           feediIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(feediIntent);
           break;


           //bottom navigation
            case R.id.playIv:
            Intent intent2=new Intent(this,PlayGameActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
            break;

            case R.id.matchIv:
            if(clickCoount%2==0) {
                clickCoount=clickCoount+1;
              //  tvLabelBottom.setText(R.string.my_matches);
                binding.gameText.setText(R.string.my_matches);
              //  tvLabelBottom.setTextColor(ContextCompat.getColor(this,R.color.lightPurple));
                matchIv.setImageResource(R.drawable.match_selected);
                gameFragmentPagerAdapter.setData("Matched");

            }else
            {
                clickCoount=clickCoount+1;
               // tvLabelBottom.setText(R.string.my_games);
                binding.gameText.setText(R.string.my_games);
               // tvLabelBottom.setTextColor(ContextCompat.getColor(this,R.color.black));
                matchIv.setImageResource(R.drawable.match);
                gameFragmentPagerAdapter.setData("UnMatched");
            }
            break;


            case R.id.createGameIv:
                if(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("1") || SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("4")) {
                    Intent gameIntent = new Intent(MainActivity.this, CreateGameActivity.class);
                    gameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(gameIntent);
                }else
                {
                    CommonUtils.playSound("press_button",MainActivity.this);
                    FacebookBottomSheet bottomSheet = new FacebookBottomSheet(this);
                    bottomSheet.show(getSupportFragmentManager(),"");
                }

                break;

           //Toolbar
            case R.id.menuIv:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                CommonUtils.playSound(ParamEnum.DRAWER.theValue(),this);
                break;

            case R.id.notiIv:
                if(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("3")){
                    showLoginFirstBottomSheet();
                }else
                {
                    notificationListApi();
                }
                break;

            case R.id.coinLL:
            intent(GetCoinsActivity.class);
            break;

            case R.id.pointsLL:
            intent(LeaderboardActivity.class);
            break;
        }
    }

    private void intent(Class<? extends Object> className) {
        if(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("3")) {
            showLoginFirstBottomSheet();
        }else {
            Intent pointsIntent = new Intent(MainActivity.this, className);
            pointsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(pointsIntent);
        }

    }

    private void notificationListApi() {
            if(CommonUtils.networkConnectionCheck(this)) {

                try {
                    ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                    Call<CommonModelDataObject> call = anInterface.getNotifiactionList();
                    ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                        @Override
                        public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                            if(response.isSuccessful())
                            {
                                CommonModelDataObject serverResponse=response.body();
                                if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                                {
                                    badgeText.setVisibility(View.GONE);
                                    bottomSheet= notificationBottomSheet(serverResponse.getData());

                                }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                                {
                                    CommonUtils.showSnackBar(MainActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                            Log.e("failure",t.getMessage());

                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else
            {
                CommonUtils.showSnackBar(MainActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
            }

        }

    private void showLoginFirstBottomSheet() {
        CommonUtils.playSound("press_button",MainActivity.this);
        LoginFirstBottomSheet bottomSheet = new LoginFirstBottomSheet(this);
        bottomSheet.show(getSupportFragmentManager(),"");
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            isBack=false;
            return;
        }
        else
        {
            if(!isBack) {
                isBack = true;
                CommonUtils.showSnackBar(MainActivity.this,"Press Again to exit",ParamEnum.Success.theValue());
            }
            else
            {
                finishAffinity();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case FACEBOOK_DATA:
                if (resultCode == 121 && data != null){
                    checkSocalIdApi(data,ParamEnum.Facebook.theValue());
                }
                break;
        }

    }

    private void checkSocalIdApi(final Intent data,String cameFrom) {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.checkSocalId(data.getStringExtra("id"),SharedPreferenceWriter.getInstance(MainActivity.this).getString(SPreferenceKey.TOKEN),ParamEnum.DeviceType.theValue());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful()) {
                            CommonModelDataObject serverResponse = response.body();

                            if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue())) {

                                if (serverResponse.getMessage().equalsIgnoreCase("User details found")) {
                                    SPrefrenceValues.setPrefrences(MainActivity.this, serverResponse);
                                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                    finish();
                                    startActivity(intent);
                                } else if (serverResponse.getMessage().equalsIgnoreCase("User does not exist")) {
                                    socialLoginApi(data,cameFrom);
                                }
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure","yes");

                    }
                });
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(MainActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private void socialLoginApi(Intent data,String cameFrom) {
        if(CommonUtils.networkConnectionCheck(MainActivity.this))
        {
            try {
                ServicesInterface anInterface= ServicesConnection.getInstance().createService(MainActivity.this);
                Call<CommonModelDataObject> call=anInterface.socialLogin(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID),
                        data.getStringExtra("id"),
                        cameFrom,
                        SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.TOKEN),
                        ParamEnum.DeviceType.theValue(),
                        data.getStringExtra("name"),
                        data.getStringExtra("image"),
                        data.getStringExtra("email"),
                        "",getCountryCodeList().toArray(),
                        CommonUtils.getUserType(cameFrom),"1");

                ServicesConnection.getInstance().enqueueWithoutRetry(call, MainActivity.this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse = response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                SPrefrenceValues.setPrefrences(MainActivity.this,serverResponse);
                                Intent intent=new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                if(serverResponse.getMessage().equalsIgnoreCase(""))

                                    CommonUtils.showSnackBar(MainActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


        }else {
            CommonUtils.showSnackBar(MainActivity.this, getString(R.string.no_internet),ParamEnum.Failure.theValue());

        }




    }

    private List<String> getCountryCodeList() {
        String langCode[]=SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.LANGUAGE_CODE).split(",");
        return new ArrayList<>(Arrays.asList(langCode));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SocketConnection.disconnect();
    }

    @Override
    public void onConnect(Object... args)  {
        Log.e("onConnect","Yes");
        try {
            JSONObject object = new JSONObject();
            object.put("_id", SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID));
            mSocket.emit("count",object);
            Log.e("count", object.toString());

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnect(Object... args) {
        Log.e("onDisconnect","Yes");
        if(mSocket!=null) {
            mSocket.connect();
        }
    }


    @Override
    public void onConnectError(Object... args) {
        Log.e("onConnectError","Yes");

    }

    @Override
    public void onNotificationCount(Object... args) {
        Log.e("onNotificationCount",args.toString());

        try {
            runOnThread("count",args);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestAccept(Object... args) {
        try {
            runOnThread("accept",args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestDecline(Object... args) {
        Log.e("onRequestDecline",args.toString());
        try {
            runOnThread("reject",args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoomJoin(Object... args) {

    }

    @Override
    public void onMessage(Object... args) {
        try {
            runOnThread("message", args[0].toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        try {
            runOnThread("coin_dedicated",args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBroadcast(Object... args) {
        try {
            runOnThread("broadcast",args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInviteAccept(Object... args) {
    }

    public void runOnThread(String eventName,Object... args) throws Exception{
        Log.e(eventName,""+args[0].toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SocketModel model = new Gson().fromJson(args[0].toString(),SocketModel.class);
                // event count
                if(eventName.equalsIgnoreCase("count"))
                {
                    if(model.getResult()!=null) {
                        SharedPreferenceWriter.getInstance(MainActivity.this).writeStringValue(SPreferenceKey.COINS, "" + model.getResult().getCoins());
                        SharedPreferenceWriter.getInstance(MainActivity.this).writeStringValue(SPreferenceKey.POINTS, "" + model.getResult().getTotalPoints());
                        tvCoins.setText("" + model.getResult().getCoins());
                        tvPoints.setText("" + model.getResult().getTotalPoints());

                        if (model.getNotificationCount() > 0) {
                            badgeText.setVisibility(View.VISIBLE);
                            badgeText.setText("" + model.getNotificationCount());
                        } else {
                            badgeText.setVisibility(View.GONE);
                        }
                    }
                }

                //Notification
                //accept
                else if(eventName.equalsIgnoreCase("accept"))
                {
                    if(checkUserData(model)) {
                        if (bottomSheet != null) {
                            bottomSheet.dismiss();
                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("userData", model);
                            startActivity(intent);
                        }
                    }
                }
                //reject
                else if(eventName.equalsIgnoreCase("reject"))
                {
                    if(checkUserData2(model)) {
                        if (iRequest != null) {
                            iRequest.onDecline(model.getGame_id());
                        }
                    }
                }
                else if(eventName.equalsIgnoreCase("coin_dedicated"))
                {
                    if(model.getSenderId()==null)
                    {
                        if (model.getMessage().equalsIgnoreCase("Insufficient Coins"))
                        {
                            InsufficientCoinsBottomSheet bottomSheet = new InsufficientCoinsBottomSheet();
                            bottomSheet.show(getSupportFragmentManager(),"");
                        }
                    }
                    else if(model.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(MainActivity.this).getString(SPreferenceKey.ID))) {
                            SharedPreferenceWriter.getInstance(MainActivity.this).writeStringValue(SPreferenceKey.COINS, model.getCoins());
                            if (model.getMessage() == null) listner.onDeduct();
                            else if (model.getMessage().equalsIgnoreCase("Insufficient Coins"))
                            {
                                InsufficientCoinsBottomSheet bottomSheet = new InsufficientCoinsBottomSheet();
                                bottomSheet.show(getSupportFragmentManager(),"");
                            }

                    }
                }


            }
        });
    }

    @Override
    public void count(String msgCount,String roomId) {
        if(msgCount==null && roomId==null) {
            try {
                if (mSocket.connected()) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("_id", SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID));
                        mSocket.emit("count", object);
                        Log.e("count", object.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
        {
            if(unReadMessagelistner!=null){unReadMessagelistner.unReadMessageCount(Long.parseLong(msgCount),roomId);}
        }


    }

    public interface coinDeducted
    {
        void onDeduct();

    }

    @Override
    public void coins(String coins) {
        tvCoins.setText(""+coins);
    }

    public interface IRequest
    {
        void onDecline(String gameId);
    }

    public static void setListner(IRequest iReq)
    {
        iRequest=iReq;
    }

    private NotificationBottomSheet notificationBottomSheet(ResponseBean model) {
        CommonUtils.playSound("press_button",MainActivity.this);
        NotificationBottomSheet  bottomSheet = new NotificationBottomSheet(model,mSocket);
        bottomSheet.show(getSupportFragmentManager(),"");
        return bottomSheet;
    }

    @Override
    public void onFacebookLogin() {
        Intent fbIntent = new Intent(this, FacebookLogin.class);
        startActivityForResult(fbIntent, FACEBOOK_DATA);
    }

    @Override
    public void onInstagramLogin() {
        InstagramLogin instaObj = new InstagramLogin(this);
        instaObj.setListener(this);
        instaObj.authorize();
    }

    private boolean checkUserData(SocketModel response) {
        boolean ret=false;
        if(response.getDataTosave().getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)) || response.getDataTosave().getReceiverId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)))
        {
            ret=true;
        }

        return ret;
    }

    private boolean checkUserData2(SocketModel response) {
        boolean ret=false;
        if(response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)) || response.getReceiverId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)))
        {
            ret=true;
        }

        return ret;
    }

}
