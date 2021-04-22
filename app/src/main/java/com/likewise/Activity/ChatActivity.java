package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.gson.Gson;
import com.likewise.Adapter.ChatAdapter;


import com.likewise.Interface.SocketCallbacks;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.CreateGameResponse;
import com.likewise.Model.CreateRoomResponse;
import com.likewise.Model.MessageResponse;
import com.likewise.Model.ResponseBean;
import com.likewise.Model.SocketModel;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.SocketHelper.SocketConnection;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.CustomSorting;
import com.likewise.Utility.GameAnimationHelper;
import com.likewise.Utility.GameCalculation;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityChatBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import hari.bounceview.BounceView;
import io.github.hyuwah.draggableviewlib.Draggable;
import io.github.hyuwah.draggableviewlib.DraggableUtils;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements SocketCallbacks, View.OnClickListener, TextWatcher, Animation.AnimationListener,View.OnLayoutChangeListener {

    //Main layout
    private ActivityChatBinding binding;

    //footer layout
    ImageView ivSend,ivView;
    EditText chatBoxEd;
    private SocketModel userDataModel;
    private Socket mSocket;
    private String room_id;
    TextView tvSec;
    int pos=1;
    boolean isFromBroadcast=false;
    private String senderCardPassMessage="Waiting for your partner to also pass";
    private String recieverCardPassMessage="Do you also wants to pass ?";

    //Layout main Toolbar
    ImageView ivprofilePic,ivcountry,backIv,ivBack;
    TextView tvName,tvcoin,tvlangCode,tvPoints;
    ProgressBar progressBar,progressBarCountryIv;
    private List<MessageResponse> messageResponseList = new ArrayList<>();
    private ResponseBean data;
    private String receiver_by="false";
    private boolean isReceiverTyping=false;
    private Animation msgAnim,passAnim;
    boolean isCalled=false,isTyping;
    private CountDownTimer timerModeCountDown;
    boolean isTimerModeRunning=false;
    private MediaPlayer mMediaPlayer;
    boolean timerFinsish=false;
    int clipLevel=9990;

    //Game Mode
    //Live Mode
    LinearLayout liveLL;
    LinearLayout oneLL,twoLL,threeLL,fourLL,fiveLL,sixLL,sevenLL,eightLL;
    TextView tvOne,tvTwo,tvThree,tvFour,tvFive,tvSix,tvSeven,tvEight;

    //Time Mode
    ConstraintLayout clTimer;
    ImageView ivClip;
    int live=8;
    int bonus;
    long points;
    int showStreak=0;
    String sender_id;
    boolean isHavingRoomId=false;
    boolean isMessageReceived=true;
    int match=0;
    int perPoints;
    int preCoins;
    int basePoints;
    int coins;
    int copiedQuestioned = 0;
    long millisUntilFinisheds;
    private boolean sound;
    private int alreadyGivenStreak=0;
    private boolean soundOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding=DataBindingUtil.setContentView(this,R.layout.activity_chat);
    init();
    sound=SharedPreferenceWriter.getInstance(this).getBoolean(SPreferenceKey.SOUND);
    getIntents();
    try {
        mSocket= SocketConnection.connect(this);
        if(!mSocket.connected())
        {
            mSocket.connect();
        }
        millisUntilFinisheds=90000;
        CommonUtils.showLoadingDialog(ChatActivity.this);
        gameDetailApi();
    }
       catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void getIntents() {
        if(getIntent().getStringExtra("cameFrom")!=null)
        {
            if(getIntent().getStringExtra("cameFrom").equalsIgnoreCase(CreateGameActivity.class.getSimpleName()))
            {
                CreateGameResponse.Data data =getIntent().getParcelableExtra("userData");
                SocketModel model=new SocketModel();
                model.setDataTosave(new SocketModel.DataTosave(data.getReceiver_id(),data.getGame_details().getId(),"2"));
                userDataModel=model;
            }
            else if(getIntent().getStringExtra("cameFrom").equalsIgnoreCase(MainActivity.class.getSimpleName()))
            {
                SocketModel model=new SocketModel();
                model.setDataTosave(new SocketModel.DataTosave(getIntent().getStringExtra(ParamEnum.RECIVER_ID.theValue()),
                        getIntent().getStringExtra(ParamEnum.GAME_ID.theValue()),
                        getIntent().getStringExtra(ParamEnum.MODE.theValue())));

                userDataModel=model;
                room_id=getIntent().getStringExtra(ParamEnum.ROOM_ID.theValue());
                isHavingRoomId=true;
            }
        }
        else
        {
            userDataModel = getIntent().getParcelableExtra("userData");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(timerFinsish) {
            if(sound){
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                }
            }

            gameOverIntent();
        }
    }

    private void init() {

        // Toolbar
        ivprofilePic=findViewById(R.id.ivprofilePic);
        tvName=findViewById(R.id.tvName);
        tvcoin=findViewById(R.id.tvcoin);
        tvPoints=findViewById(R.id.tvPoints);
        ivcountry=findViewById(R.id.ivcountry);
        tvlangCode=findViewById(R.id.tvlangCode);
        progressBar=findViewById(R.id.progressBar);
        ivBack=findViewById(R.id.ivBack);
        progressBarCountryIv=findViewById(R.id.progressBarCountryIv);
        ivBack.setOnClickListener(this);

        // Footer layout
        ivSend=findViewById(R.id.ivSend);
        ivView=findViewById(R.id.ivView);
        chatBoxEd=findViewById(R.id.chatBoxEd);
        chatBoxEd.setOnClickListener(this);
        chatBoxEd.addTextChangedListener(this);

        // Main Layout
        ivView=findViewById(R.id.ivView);
        backIv=findViewById(R.id.backIv);
        backIv.setOnClickListener(this);
        binding.chatRv.setOnClickListener(this);
        binding.subCl.setOnClickListener(this);

        // Layout footer
        CommonUtils.showAnimation(binding.mainCl);
        ivSend.setOnClickListener(this);
        ivView.setOnClickListener(this);
        binding.closeIv.setOnClickListener(this);
        binding.ivObjective2.setOnClickListener(this);
        binding.ivObjective.setOnClickListener(this);
        binding.nextText.setOnClickListener(this);
        BounceView.addAnimTo(ivSend);
        BounceView.addAnimTo(ivView);
        BounceView.addAnimTo(binding.nextText);
        BounceView.addAnimTo(binding.ivObjective);
        BounceView.addAnimTo(binding.closeIv);
        DraggableUtils.makeDraggable(binding.clObjective, Draggable.STICKY.NONE,true);


        // Game Mode
        // Live
        oneLL=findViewById(R.id.oneLL);
        twoLL=findViewById(R.id.twoLL);
        threeLL=findViewById(R.id.threeLL);
        fourLL=findViewById(R.id.fourLL);
        fiveLL=findViewById(R.id.fiveLL);
        sixLL=findViewById(R.id.sixLL);
        sevenLL=findViewById(R.id.sevenLL);
        eightLL=findViewById(R.id.eightLL);
        liveLL=findViewById(R.id.liveLL);
        tvOne=findViewById(R.id.tvOne);
        tvTwo=findViewById(R.id.tvTwo);
        tvThree=findViewById(R.id.tvThree);
        tvFour=findViewById(R.id.tvFour);
        tvFive=findViewById(R.id.tvFive);
        tvSix=findViewById(R.id.tvSix);
        tvSeven=findViewById(R.id.tvSeven);
        tvEight=findViewById(R.id.tvEight);

        // Time
        ivClip=findViewById(R.id.ivClip);
        tvSec=findViewById(R.id.tvSec);
        clTimer=findViewById(R.id.clTimer);
        points=0;


    }

    private void gameDetailApi() {
        if(CommonUtils.networkConnectionCheck(this))
        {
            try {
                binding.chatRv.addOnLayoutChangeListener(this);
                ServicesInterface anInterface= ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call=anInterface.gameDetail(userDataModel.getDataTosave().getGameId(),userDataModel.getDataTosave().getReceiverId());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse = response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                setCardBlinking(serverResponse.getData().getGameDetails().getCard_pass_user_id());
                                data=serverResponse.getData();
                                getPoints();
                            }
                            else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.dismissLoadingDialog();
                                CommonUtils.showSnackBar(ChatActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
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
            CommonUtils.showSnackBar(ChatActivity.this, getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private void setCardBlinking(String card_pass_user_id) {
        if(card_pass_user_id!=null)
        {
            if(card_pass_user_id.equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)))
            {
                receiver_by = "false";
                showMessage(senderCardPassMessage);
            }else if(card_pass_user_id.equalsIgnoreCase(userDataModel.getDataTosave().getReceiverId()))
            {
                receiver_by = "true";
                showMessage(recieverCardPassMessage);
            }
        }
    }

    private void getPoints() {
        if (CommonUtils.networkConnectionCheck(ChatActivity.this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(ChatActivity.this);
                Call<CommonModelDataObject> call = anInterface.getCoins();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, ChatActivity.this, false, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if (response.isSuccessful()) {
                            CommonModelDataObject serverResponse = response.body();
                            if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue())) {
                                perPoints=serverResponse.getData().getCoinModel().get(0).getPoint();
                                preCoins=serverResponse.getData().getCoinModel().get(0).getCoin();
                                basePoints=serverResponse.getData().getCoinModel().get(0).getEarnCoinsEvery();
                                if(!isHavingRoomId) { roomCreateApi(); }
                                else {roomJoinEvent(); chatHistoryApi(); }

                                if(!SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.POINTS).equalsIgnoreCase("")) {

                                    if (points>0)
                                    {
                                        int ques= (int) (points/perPoints);
                                        if(ques>=1){
                                            copiedQuestioned=ques;
                                        }
                                    }
                                }
                            } else if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue())) {
                                CommonUtils.dismissLoadingDialog();
                                CommonUtils.showSnackBar(ChatActivity.this, serverResponse.getMessage(), ParamEnum.Failure.theValue());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
//                        Log.e("failure", t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        } else {
            CommonUtils.showSnackBar(ChatActivity.this, getString(R.string.no_internet), ParamEnum.Failure.theValue());
        }
    }

    private void chatHistoryApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.getChatData(room_id,userDataModel.getReceiverId());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        CommonUtils.dismissLoadingDialog();
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                setUpData(data);
                                settingChatData(getData(serverResponse.getData()));

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(ChatActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                    }
                });
            } catch (Exception e) {
                Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            CommonUtils.showSnackBar(ChatActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    private Map<String, List<MessageResponse>> getData(ResponseBean data) {
        Map<String,List<MessageResponse>> chatData = new HashMap<>();
        List<MessageResponse> firstCardList = new ArrayList<>();
        List<MessageResponse> secoundCardList = new ArrayList<>();
        List<MessageResponse> thirdCardList = new ArrayList<>();
        List<MessageResponse> fourthCardList = new ArrayList<>();
        List<MessageResponse> fifthCardList = new ArrayList<>();
        List<MessageResponse> sixCardList = new ArrayList<>();
        for(int i=0;i<data.getChatData().size();i++)
        {
            if(!data.getChatData().get(i).isMatched())
            {
                if(!data.getChatData().get(i).getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)))
                {
                    data.getChatData().get(i).setEncrypted(true);
                    data.getChatData().get(i).setEncryped(data.getChatData().get(i).getMessage());

                }
            }
            if(data.getChatData().get(i).getCard().equalsIgnoreCase("0"))
            {
                firstCardList.add(data.getChatData().get(i));
                chatData.put(data.getChatData().get(i).getImage(), firstCardList);

            }
            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("1"))
            {
                secoundCardList.add(data.getChatData().get(i));
                chatData.put(data.getChatData().get(i).getImage(), secoundCardList);
            }
            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("2"))
            {
                thirdCardList.add(data.getChatData().get(i));
                chatData.put(data.getChatData().get(i).getImage(), thirdCardList);

            }
            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("3"))
            {
                fourthCardList.add(data.getChatData().get(i));
                chatData.put(data.getChatData().get(i).getImage(), fourthCardList);

            }
            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("4"))
            {
                fifthCardList.add(data.getChatData().get(i));
                chatData.put(data.getChatData().get(i).getImage(), fifthCardList);

            }
            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("5"))
            {
                sixCardList.add(data.getChatData().get(i));
                chatData.put(data.getChatData().get(i).getImage(), sixCardList);
            }
        }
        return chatData;
    }

    private void settingChatData(Map<String, List<MessageResponse>> data) {
        CommonUtils.playSound("start_game", this);
        int card = getIntent().getIntExtra(ParamEnum.CURRENT_CARD.theValue(), -1);
        Log.e("current_card", ""+card);
        pos = card + 1;
        messageResponseList.clear();

        // Newly Added
        if (card < 0) {
            card = 0;
            pos = card + 1;
        }
        if (data.keySet().toArray().length > 0) {
            for (int i = 0; i < data.keySet().toArray().length; i++) {
                for (int j = 0; j < data.get(data.keySet().toArray()[i]).size(); j++) {
                    if (data.get(data.keySet().toArray()[i]).get(j).getCard().equalsIgnoreCase(card + "")) {
                        if (this.data.getGameDetails().getImages().size() == pos) binding.nextText.setText((pos) + "/" + this.data.getGameDetails().getImages().size() + " END");
                        else binding.nextText.setText((pos) + "/" + this.data.getGameDetails().getImages().size() + " PASS");
                        messageResponseList.addAll(checkMatch(data.get(data.keySet().toArray()[i])));
                        break;
                    }
                }
            }
        }
        else {
            if (card == this.data.getGameDetails().getImages().size()) {
                Toast.makeText(this, "Patner has already ended this game", Toast.LENGTH_SHORT).show();
                gameOverIntent();
            }
        }

        ImageGlider.setNormalImage(this, backIv, binding.mainProgress, this.data.getGameDetails().getImages().get(card));
        if (this.data.getGameDetails().getImages().size() == pos) binding.nextText.setText((pos) + "/" + this.data.getGameDetails().getImages().size() + " END");
        else binding.nextText.setText((pos) + "/" + this.data.getGameDetails().getImages().size() + " PASS");
        if (getIntent().getStringExtra(ParamEnum.MODE.theValue()) != null) {
            if (getIntent().getStringExtra(ParamEnum.MODE.theValue()).equalsIgnoreCase("2")) {
                live = Integer.parseInt(getIntent().getStringExtra(ParamEnum.LIVES.theValue()));
                checkLives(getIntent().getStringExtra(ParamEnum.LIVES.theValue()));
            }
        }
        if (messageResponseList.size() > 0) {
            ChatAdapter adapter = new ChatAdapter(this, checkMatchedChatLive(null));
            binding.chatRv.setLayoutManager(new LinearLayoutManager(this));
            binding.chatRv.setAdapter(adapter);
            binding.chatRv.scrollToPosition(binding.chatRv.getAdapter().getItemCount() - 1);
            try {
                binding.chatRv.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            binding.chatRv.scheduleLayoutAnimation();

        }
    }

    private Collection<? extends MessageResponse> checkMatch(List<MessageResponse> list) {
        List<MessageResponse> listMessage = new ArrayList<>();
        List<MessageResponse> senderList = new ArrayList<>();
        List<MessageResponse> reciverList = new ArrayList<>();

        for (int i=0;i<list.size();i++) {
         if (list.get(i).getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID))) senderList.add(list.get(i));
         else reciverList.add(list.get(i));
        }

        for(int i=0;i<senderList.size();i++) {
            for(int j=0;j<reciverList.size();j++) {
                if(!senderList.get(i).isMatched() && !reciverList.get(j).isMatched()){
                    if(senderList.get(i).getMessage().trim().equalsIgnoreCase(reciverList.get(j).getMessage().trim())){
                        senderList.get(i).setMatched(true);
                        reciverList.get(j).setMatched(true);
                        break;
                    }
                }
            }
        }

        listMessage.clear();
        listMessage.addAll(senderList);
        listMessage.addAll(reciverList);
        Collections.sort(listMessage,new CustomSorting());
        return listMessage;
    }



    private void roomCreateApi() {
        if(CommonUtils.networkConnectionCheck(this))
        {
            try {
                ServicesInterface anInterface= ServicesConnection.getInstance().createService(this);
                Call<CreateRoomResponse> call=anInterface.roomCreate(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.TOKEN),
                        userDataModel.getDataTosave().getReceiverId(),
                        SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID),
                        userDataModel.getDataTosave().getGameId(),
                        userDataModel.getDataTosave().getType(),
                        data.getLanguageDetails().getCode(),
                        data.getGameDetails().getGameType());

                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CreateRoomResponse>() {
                    @Override
                    public void onResponse(Call<CreateRoomResponse> call, Response<CreateRoomResponse> response) {
                        if(response.isSuccessful())
                        {
                            CreateRoomResponse serverResponse = response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue())) {
                            setUpData(data);
                            room_id = serverResponse.getData().getRoomId();
                            if(!isHavingRoomId) {chatHistoryApi();}
                            roomJoinEvent();
                            }
                            else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.dismissLoadingDialog();
                                CommonUtils.showSnackBar(ChatActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<CreateRoomResponse> call, Throwable t) {
                        Log.e("failure",t.getMessage());
                    }
                });

            }
            catch (Exception e) {
             e.printStackTrace();
            }
        }else {
            CommonUtils.showSnackBar(this,this.getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private void roomJoinEvent() {
        try {
            JSONObject object = new JSONObject();
            object.put("room_id", room_id);
            object.put("gameType",data.getGameDetails().getGameType());
            mSocket.emit("room_join", object);
            Log.e("room_join",object.toString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setUpData(ResponseBean data) {
        binding.tvObjective.setText(data.getGameCriteriaExplanations().getExplanation());
        binding.tvcustomInst.setText(data.getGameDetails().getCustomInstructions());
        binding.tvPrecisouly.setText(data.getMorePrecisely().getConverse());
        ImageGlider.setRoundImage(this,ivprofilePic,progressBar, data.getReceiverDetails().getProfilePic());
        if(data.getReceiverDetails().getName()!=null) {
            if(data.getReceiverDetails().getName().trim().contains(" ")) {
                tvName.setText(data.getReceiverDetails().getName().trim().split(" ")[0]);
            }else
            {
                tvName.setText(data.getReceiverDetails().getName().trim());
            }
        }
        if(getIntent().getStringExtra("cameFrom")!=null) {
            Log.e("cameFrom",getIntent().getStringExtra("cameFrom"));
            if (getIntent().getStringExtra("cameFrom").equalsIgnoreCase(MainActivity.class.getSimpleName())) {
                points= getIntent().getIntExtra(ParamEnum.POINTS.theValue(),0);
                tvPoints.setText(""+points);
                if (points>0)
                {
                    int ques= (int) (points/perPoints);
                    if(ques>=1){
                        copiedQuestioned=ques;
//                        Log.e("copiedQuestioned",""+copiedQuestioned);
                    }
                }
            }
            else
            {
                points=0;
                tvPoints.setText("0");
            }
        }else {
            points=0;
            tvPoints.setText("0");
        }
        tvcoin.setText(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.COINS));
        if(data.getLanguageDetails().getCode().contains("-"))
        {
            tvlangCode.setText(data.getLanguageDetails().getCode().split("-")[0]);
        }
        else {
            tvlangCode.setText(data.getLanguageDetails().getCode());
        }

        ImageGlider.setNormalImage(this,ivcountry,progressBarCountryIv,data.getLanguageDetails().getPicture());

        if(data.getGameDetails().getImages().size()>0) {
            ImageGlider.setNormalImage(this, backIv, binding.mainProgress, data.getGameDetails().getImages().get(0));
        }

        if(data.getGameDetails().getImages().size()==1) {
            binding.nextText.setText("" + (1) + "/" + data.getGameDetails().getImages().size() + " PASS");
        }else
        {
            binding.nextText.setText("" + (1) + "/" + data.getGameDetails().getImages().size() + " END");
        }
        ImageGlider.setNormalImage(this,binding.ivObjective2,binding.progr,data.getGameCriteriaExplanations().getPicture().get(1).getPicture());
        if(data.getGameDetails().getMode().equalsIgnoreCase("1"))
        {
            clTimer.setVisibility(View.VISIBLE);
            liveLL.setVisibility(View.GONE);
            ClipDrawable clipDrawable=(ClipDrawable)ivClip.getDrawable();
            clipDrawable.setLevel(9990);
            ivBack.setVisibility(View.GONE);
        }
        else if(data.getGameDetails().getMode().equalsIgnoreCase("2"))
        {
            clTimer.setVisibility(View.GONE);
            liveLL.setVisibility(View.VISIBLE);

        }
    }

    private void setTimer() {
            ClipDrawable clipDrawable = (ClipDrawable) ivClip.getDrawable();
            clipDrawable.setLevel(clipLevel);
            timerModeCountDown = new CountDownTimer(millisUntilFinisheds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    isTimerModeRunning = true;
                     millisUntilFinisheds=millisUntilFinished;
                     clipLevel=clipDrawable.getLevel()-111;
                     clipDrawable.setLevel(clipLevel);
                    int sec = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 90;
                    tvSec.setText(sec + "s");
                    Log.e("secoundsLeft", ""+sec);
                    if (tvSec.getText().toString().equalsIgnoreCase("10s")) {
                        if(sound) {
                            mMediaPlayer = MediaPlayer.create(ChatActivity.this, R.raw.count_down_timer);
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mMediaPlayer.setLooping(true);
                            mMediaPlayer.start();
                        }
                    }
                }

                @Override
                public void onFinish() {
                    if (isTimerModeRunning) {
                        timerFinsish=true;
                        if(sound){
                            if (mMediaPlayer != null) {
                                if (mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.stop();
                                }
                            }
                        }
                        gameOverIntent();
                    }
                }
            }.start();
    }

    private void gameOverIntent() {
        try {
            JSONObject object = new JSONObject();
            object.put("updates", getUpdatedData());
            mSocket.emit("game_back", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sound){
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
            }
        }
        Intent intent = new Intent(ChatActivity.this, GameOverActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("receiver_id", userDataModel.getDataTosave().getReceiverId());
        intent.putExtra("game_id", userDataModel.getDataTosave().getGameId());
        intent.putExtra("points", points);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

            case R.id.backIv:
            CommonUtils.hideKeyboardFrom(this,v);
            break;

            case R.id.chatRv:
            CommonUtils.hideKeyboardFrom(this,v);
            break;

            case R.id.subCl:
            CommonUtils.hideKeyboardFrom(this,v);
            break;

            case R.id.closeIv:
            binding.clObjective.setVisibility(View.GONE);
            break;

            case R.id.chatBoxEd:
            break;

            case R.id.ivView:
            if(((BitmapDrawable)ivView.getDrawable()).getBitmap()==((BitmapDrawable) this.getResources().getDrawable(R.drawable.eye_grey)).getBitmap()) {
                binding.subCl.setVisibility(View.GONE);
                Bitmap bitmap = ((BitmapDrawable)this.getResources().getDrawable(R.drawable.hide)).getBitmap();
                ivView.setImageBitmap(bitmap);
            }
            else
            {
                Bitmap bitmap = ((BitmapDrawable)this.getResources().getDrawable(R.drawable.eye_grey)).getBitmap();
                binding.subCl.setVisibility(View.VISIBLE);
                ivView.setImageBitmap(bitmap);
            }
            break;

            case R.id.nextText:
            try {
                if(!mSocket.connected())
                {
                    mSocket.connect();
                }

                if(checkModeValidation()) {
                    isCalled = true;
                    JSONObject object = new JSONObject();
                    object.put("sender_id", SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID));
                    object.put("receiver_id", userDataModel.getDataTosave().getReceiverId());
                    object.put("game_id", userDataModel.getDataTosave().getGameId());
                    object.put("room_id", room_id);
                    object.put("status", "true");
                    object.put("sender_by", "true");
                    object.put("receiver_by", receiver_by);
                    object.put("updates", getUpdatedData());
                    object.put("card", Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1);
                    mSocket.emit("card_pass", object);
                }
                else
                {
                    CommonUtils.showSnackBar(ChatActivity.this,"Please start game first",ParamEnum.Failure.theValue());
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            break;

            case R.id.ivObjective:
            binding.ivObjective2.performClick();
            break;


            case R.id.ivObjective2:
            binding.clObjective.setVisibility(View.VISIBLE);
            break;

            case R.id.ivSend:
            if(chatBoxEd.getText().toString().length()>0) {
                if (!mSocket.connected()) {
                    mSocket.connect();
                }
                if (isMessageReceived) {
                    if (!chatBoxEd.getText().toString().equalsIgnoreCase("Duplicate!") && chatBoxEd.getText().toString().trim().length() > 0) {
                        isMessageReceived = false;
                        stopTypingJsonSend();
                        sendMessage();
                    }
                }
            }else
            {
                CommonUtils.showSnackBar(this,"Make a Suggestion",ParamEnum.Failure.theValue());
            }
            break;

            case R.id.ivBack:
            onBackPressed();
            break;
        }

    }

    private boolean checkModeValidation() {
        boolean ret=true;

        if(userDataModel.getDataTosave().getType().equalsIgnoreCase("1"))
        {
            if(!isTimerModeRunning) ret=false;
        }
        return ret;
    }

    @Override
    public void onBackPressed() {
        if(userDataModel.getDataTosave().getType().equalsIgnoreCase("2")) {
            if(sound){
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                }
            }
                Intent intent = new Intent(this, MainActivity.class);
                finish();
                startActivity(intent);
        }else
        {
            CommonUtils.showSnackBar(this,"You cannot go back ,Wait for game to finish.",ParamEnum.Failure.theValue());
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        try
        {
            JSONObject object = new JSONObject();
            object.put("updates",getUpdatedData());
            mSocket.emit("game_back",object);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(sound){
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
            }
        }
//        if(userDataModel.getDataTosave().getType().equalsIgnoreCase("1"))
//        {
//            if(isTimerModeRunning)
//            {
//                isTimerModeRunning=false;
//                timerModeCountDown.cancel();
//                timerModeCountDown.onFinish();
//            }
//        }

    }

    private void sendMessage() {
        int length;
        if(chatBoxEd.getText().toString().trim().contains(" "))
        {
            length=chatBoxEd.getText().toString().split(" ").length;
        }
        else
        {
            length=1;
        }
        if(!checkDuplicateMessage(chatBoxEd.getText().toString())) {
            if (!TextUtils.isEmpty(chatBoxEd.getText().toString())) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("room_id", room_id);
                    object.put("sender_id", SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID));
                    object.put("receiver_id", userDataModel.getDataTosave().getReceiverId());
                    object.put("message", chatBoxEd.getText().toString().trim());
                    object.put("game_id", userDataModel.getDataTosave().getGameId());
                    object.put("type", Integer.parseInt(userDataModel.getDataTosave().getType()));
                    object.put("Image",data.getGameDetails().getImages().get(pos - 1));
                    object.put("card", Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1);
                    object.put("streaks","");
                    object.put("point","");
                    object.put("bonus","");
                    object.put("score","");
                    object.put("isMatched",false);
                    object.put("isStreak",false);
                    object.put("all_matched","");
                    object.put("languageCode",tvlangCode.getText().toString());
                    object.put("messageWordCount",length);
                    mSocket.emit("message", object);
                    Log.e("messageData",object.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                CommonUtils.showSnackBar(ChatActivity.this,"Make a Suggestion",ParamEnum.Failure.theValue());
            }
        }else
        {
            chatBoxEd.setText("Duplicate!");
            CommonUtils.setVibrate(ChatActivity.this);
            chatBoxEd.setBackground(this.getResources().getDrawable(R.drawable.drawable_red_corners_chatbox));
            chatBoxEd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_animation);
            chatBoxEd.startAnimation(shake);
            shake.setAnimationListener(this);
        }
    }

    private JSONArray getUpdatedData() {
    JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < messageResponseList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("pointId",messageResponseList.get(i).getPointId());
                object.put("score",messageResponseList.get(i).getScore());
                object.put("point",points);
                object.put("bonus",messageResponseList.get(i).getBonus());
                object.put("streaks",messageResponseList.get(i).getStreaks());
                object.put("isMatched",messageResponseList.get(i).isMatched());
                object.put("all_matched",messageResponseList.get(i).getAll_matched());
                object.put("isStreak",messageResponseList.get(i).isStreak());
                array.put(i,object);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
//        Log.e("Updated",""+array.toString());
        return array;
    }

    private boolean checkDuplicateMessage(String message) {
        boolean ret=false;
        if(messageResponseList.size()>0)
        {
            for(MessageResponse response: messageResponseList)
            {
                if(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID).equalsIgnoreCase(response.getSenderId())) {
                    if (response.getMessage().equalsIgnoreCase(message)) {
                        ret = true;
                    }
                }
            }
        }
        return ret;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnect(Object... args) {
        Log.e("chat-screen", "onConnect");
    }

    @Override
    public void onDisconnect(Object... args) {
        Log.e("chat-screen", "onDisconnect");
    }

    @Override
    public void onConnectError(Object... args) {
        Log.e("chat-screen", "onConnectError");
    }

    @Override
    public void onNotificationCount(Object... args) {
    }

    @Override
    public void onRequestAccept(Object... args) {
        Log.e("chat-screen", "onRequestAccept");
        try {
            runOnThread("accept",args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestDecline(Object... args) {
    }

    @Override
    public void onRoomJoin(Object... args) {
        Log.e("room_join", "yes");

    }

    @Override
    public void onMessage(Object... args) {
        runOnThread("message",args);
    }

    @Override
    public void onRoomUpdate(Object... args) {
    }

    @Override
    public void onCardPass(Object... args) {
        runOnThread("card_pass",args);
    }

    @Override
    public void onLivePass(Object... args) {
        runOnThread("lives_pass",args);
    }

    @Override
    public void onTyping(Object... args) {
        runOnThread("typing_in",args);
    }

    @Override
    public void onStopTyping(Object... args) {
        runOnThread("stop_typing",args);
    }

    @Override
    public void onGameDataSave(Object... args) {
        runOnThread("game_back",args);
    }

    @Override
    public void onCreateRoom(Object... args) {
    }

    @Override
    public void onCoinDeducted(Object... args) {

    }

    @Override
    public void onBroadcast(Object... args) {
        runOnThread("onBroadcast",args);
    }


    @Override
    public void onInviteAccept(Object... args) {

    }

    private void runOnThread(String evetName, Object[] args) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.wtf(""+evetName, args[0].toString());
                if(evetName.equalsIgnoreCase("accept"))
                {
                    SocketModel model = new Gson().fromJson(args[0].toString(),SocketModel.class);
                    if(checkUserData(model))
                    {
//                        Log.e("hahaha", "ayaya");
                        roomJoinEvent();
                    }
                }

                MessageResponse response = new Gson().fromJson(args[0].toString(), MessageResponse.class);
                if (evetName.equalsIgnoreCase("message")) {
                    Log.e("isCurrentUser", ""+checkUserData(response));
                    if(checkUserData(response)) {
                        CommonUtils.playSound("enter_chat", ChatActivity.this);
                        if (response.getSenderId().equalsIgnoreCase(userDataModel.getDataTosave().getReceiverId())) {
                            response.setEncrypted(true);
                            response.setEncryped(response.getMessage());
                            updateRecycleView(response);
                        } else {
                            updateRecycleView(response);
                        }
                    }
                }
                if (evetName.equalsIgnoreCase("typing_in")) {
                    if (checkUserData(response)) {
                        if (!response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID))) {
                            binding.typingCl.setVisibility(View.VISIBLE);
                            if (binding.chatRv.getAdapter() != null) {
                                if (binding.chatRv.getAdapter().getItemCount() > 0) {
                                    binding.chatRv.scrollToPosition(binding.chatRv.getAdapter().getItemCount() - 1);
                                }
                            }

                        } else {
                            binding.typingCl.setVisibility(View.GONE);
                        }
                    }
                }
                if (evetName.equalsIgnoreCase("stop_typing")) {
                    if(checkUserData(response)) {
                        binding.typingCl.setVisibility(View.GONE);
                    }
                }
                else if (evetName.equalsIgnoreCase("card_pass")) {
                       if (checkUserData(response)) {
                           if (response.getMessage() == null) {
                               if (response.getSenderId().equalsIgnoreCase(userDataModel.getDataTosave().getReceiverId())) {
                                   receiver_by = "true";
                                   showMessage(response.getReceiverMessage());
                               } else if (response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID))) {
                                   receiver_by = "false";
                                   showMessage(response.getSenderMessage());
                               }
                           } else {
                               CommonUtils.playSound("card_pass", ChatActivity.this);
                               showStreak = 0;
                               alreadyGivenStreak = 0;
                               if (isCalled) {
                                   isCalled = false;
                                   receiver_by = "false";
                                   msgAnim.cancel();
                                   passAnim.cancel();
                                   binding.passText.setVisibility(View.GONE);
                                   binding.nextText.setTextColor(ChatActivity.this.getResources().getColor(R.color.white));
                                   passAnim = null;
                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                       binding.nextText.setBackgroundTintList(getColorStateList(R.color.green));
                                   }
                                   passAnim = CommonUtils.makeMeBlink(binding.nextText, ContextCompat.getDrawable(ChatActivity.this, R.drawable.drawable_green_corners_two_side));
                                   new CountDownTimer(1000, 1000) {
                                       @Override
                                       public void onTick(long millisUntilFinished) {
                                       }

                                       @Override
                                       public void onFinish() {
                                           passAnim.cancel();
                                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                               binding.nextText.setBackgroundTintList(getColorStateList(R.color.grey));
                                           }
                                           binding.nextText.setBackground(ChatActivity.this.getResources().getDrawable(R.drawable.bg_const_grey_two_side_corner));
                                           binding.nextText.setTextColor(ChatActivity.this.getResources().getColor(R.color.black));
                                           pos = pos + 1;
                                           Log.e("pos", "" + pos);
                                           if (pos <= data.getGameDetails().getImages().size()) {
                                               messageResponseList.clear();
                                               binding.chatRv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                                               binding.chatRv.setAdapter(new ChatAdapter(ChatActivity.this, messageResponseList));
                                               if (data.getGameDetails().getImages().size() == pos) {
                                                   binding.nextText.setText("" + (pos) + "/" + data.getGameDetails().getImages().size() + " END");
                                               } else {
                                                   binding.nextText.setText("" + (pos) + "/" + data.getGameDetails().getImages().size() + " PASS");
                                               }
                                               Glide.with(ChatActivity.this).load(data.getGameDetails().getImages().get(pos - 1)).into(binding.backIv);
                                           } else {
                                                if(timerModeCountDown!=null) {
                                                    timerFinsish=true;
                                                    isTimerModeRunning=false;
                                                    timerModeCountDown.cancel();
                                                    timerModeCountDown.onFinish();
                                                    if(sound) {
                                                        if (mMediaPlayer != null) {
                                                            if (mMediaPlayer.isPlaying()) {
                                                                mMediaPlayer.stop();
                                                            }
                                                        }

                                                    }
                                               }
                                               gameOverIntent();
                                           }

                                       }
                                   }.start();
                               }
                           }
                       }
                   }
                else if (evetName.equalsIgnoreCase("lives_pass")) {
                    if(checkUserData(response)) {
                        checkLives(response.getLives());
                    }
                }
            }
        });
    }

    private boolean checkUserData(MessageResponse response) {
        boolean ret=false;
        if(response.getReceiverId()==null)
        {
            if(response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)) || response.getSenderId().equalsIgnoreCase(userDataModel.getDataTosave().getReceiverId()) && response.getRoomId().equalsIgnoreCase(room_id))
            {
                ret=true;
            }

        }
        else if(response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)) || response.getReceiverId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)) &&
                response.getReceiverId().equalsIgnoreCase(userDataModel.getDataTosave().getReceiverId()) || response.getSenderId().equalsIgnoreCase(userDataModel.getDataTosave().getReceiverId()) && response.getRoomId().equalsIgnoreCase(room_id))
        {
            ret=true;
        }


        return ret;
    }

    private void showMessage(String message) {
        binding.passText.setVisibility(View.VISIBLE);
        binding.passText.setText(message);
        msgAnim = CommonUtils.makeMeBlink(binding.passText, ContextCompat.getDrawable(this,R.drawable.drawable_red_corners_two_side),800);
        passAnim= CommonUtils.makeMeBlink(binding.nextText, ContextCompat.getDrawable(this,R.drawable.drawable_red_corners_two_side),800);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.nextText.setTextColor(getColor(R.color.white));
            binding.nextText.setBackgroundTintList(getColorStateList(R.color.bgRed));
        }
    }

    private void updateRecycleView(MessageResponse response) {
        if(response.getType()!=null) {
            if (messageResponseList.size() > 0) {
                if (response.getType().equalsIgnoreCase("1")) {
                    messageResponseList.add(checkMatchedChatTime(response));
                    if(!isTimerModeRunning) {setTimer();}
                } else if (response.getType().equalsIgnoreCase("2")) {
                    messageResponseList=checkMatchedChatLive(response);
                }
                binding.chatRv.getAdapter().notifyDataSetChanged();

            } else {
                if (response.getType().equalsIgnoreCase("1")) {
                    messageResponseList.add(checkMatchedChatTime(response));
                    if(!isTimerModeRunning) {setTimer();}
                } else if (response.getType().equalsIgnoreCase("2")) {
                    messageResponseList=checkMatchedChatLive(response);
                }
                binding.chatRv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                binding.chatRv.setAdapter(new ChatAdapter(ChatActivity.this, messageResponseList));
            }
            if(response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)))
            {
                chatBoxEd.setText("");
                isMessageReceived=true;
            }

            binding.chatRv.scrollToPosition(binding.chatRv.getAdapter().getItemCount() - 1);
        }
    }

    private List<MessageResponse> checkMatchedChatLive(MessageResponse response)
    {
        boolean deductLives=false;
        boolean isSender=false;
        boolean isReceiver=false;
        boolean receiverStatusUnknown=false;
        boolean senderStatusUnknow=false;
        for(int i=0;i<messageResponseList.size();i++)
        {
            if(response!=null) {
                if (!messageResponseList.get(i).isMatched()) {
                    if (response.getMessage().equalsIgnoreCase(messageResponseList.get(i).getMessage())) {
                        if (response != null) {
                            match=match+1;
                            CommonUtils.playSound("match", ChatActivity.this);
                        }
                        messageResponseList.get(i).setMatched(true);
                        response.setMatched(true);
                        points = points + GameCalculation.points(basePoints, getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1));
                        messageResponseList.get(i).setPoint(points);
                        response.setPoint(points);
                        Log.e("points=","---------------------->"+points);
                        messageResponseList.get(i).setAll_matched(getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1));
                        response.setAll_matched(getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1));
                        if (messageResponseList.get(i).getMessage().trim().contains(" ")) {
                            String count[] = messageResponseList.get(i).getMessage().split(" ");
                            bonus = count.length;
                        } else {
                            bonus = 1;
                        }
                        showMatchAndBonusLive(bonus,GameCalculation.points(basePoints, getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1)), messageResponseList.get(i), response);
                        break;
                    } else {
//                        bonus=0;
//                        messageResponseList.get(i).setBonus(0);
                        deductLives = true;
                    }
                }
            }
        }



        for(int i=0;i<messageResponseList.size();i++)
        {
            if(messageResponseList.get(i).getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)))
            {
                isSender=true;
                if(!messageResponseList.get(i).isMatched())
                {
                    senderStatusUnknow=true;
                }
            }else if(!messageResponseList.get(i).getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)))
            {
                isReceiver=true;
                if(!messageResponseList.get(i).isMatched())
                {
                    receiverStatusUnknown=true;

                }
            }

            if(isSender && isReceiver && senderStatusUnknow && receiverStatusUnknown)
            {
                break;
            }
        }

        if(response!=null)
        {
            if(response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)))
            {
                isSender=true;
                if(!response.isMatched())
                {
                    senderStatusUnknow=true;
                }
            }else if(!response.getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)))
            {
                isReceiver=true;
                if(!response.isMatched())
                {
                    receiverStatusUnknown=true;
                }

            }
        }

        Log.e("CurrentLives:", ""+live);

        if(response!=null) {
            if (deductLives && isSender && isReceiver) {
                if(!response.isMatched() && senderStatusUnknow && receiverStatusUnknown) {
                    Log.e("Lives", "Deducted"+live);
                    live = live - 1;
                    CommonUtils.playSound("life_lost", ChatActivity.this);
                    deductLivesFromBoth(live);
                }
            }

        }
        if(response!=null) {
            messageResponseList.add(response);
        }

        return messageResponseList;
        }

    private int getAllMatch(int card) {
        int suggestion = 50;

        for (int i=0;i<data.getAllmatchpercent().size();i++)
        {
           if(data.getAllmatchpercent().get(i).getImage().equalsIgnoreCase(data.getGameDetails().getImages().get(card)))
            {
                suggestion=data.getAllmatchpercent().get(i).getSuggestion();
                if(suggestion ==0) suggestion=1;
                break;
            }
        }
        return suggestion;
    }

//    private List<MessageResponse> checkMatchedChatLive(MessageResponse response) {
//        boolean deductLives=false;
//        List<MessageResponse> senderList = new ArrayList<>();
//        List<MessageResponse> reciverList = new ArrayList<>();
//        if(response!=null) {
//            messageResponseList.add(response);
//        }
//        for(int i=0;i<messageResponseList.size();i++)
//        {
//            if(messageResponseList.get(i).getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)))
//                senderList.add(messageResponseList.get(i));
//            else
//                reciverList.add(messageResponseList.get(i));
//        }
//
//        for(int i=0;i<senderList.size();i++)
//        {
//            for(int j=0;j<reciverList.size();j++)
//            {
//                if(senderList.get(i).getMessage().equalsIgnoreCase(reciverList.get(j).getMessage()))
//                {
//                    if(!senderList.get(i).isMatched() && !reciverList.get(j).isMatched()) {
//                        if(response!=null) {
//                            CommonUtils.playSound("match", ChatActivity.this);
//                        }
//                        senderList.get(i).setMatched(true);
//                        reciverList.get(j).setMatched(true);
//                        points = points + GameCalculation.points(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()), 1, 0);
//                        senderList.get(i).setPoint(points);
//                        reciverList.get(j).setPoint(points);
//                        tvPoints.setText("" + points);
//                        senderList.get(i).setAll_matched(50);
//                        reciverList.get(j).setAll_matched(50);
//                        if (senderList.get(i).getMessage().contains(" ")) {
//                            String count[] = senderList.get(i).getMessage().split(" ");
//                            bonus = count.length;
//                        } else {
//                            bonus = 1;
//                        }
//
//                            senderList.get(i).setBonus(bonus);
//                            reciverList.get(j).setBonus(bonus);
//                            showMatchAndBonusLive(bonus, GameCalculation.points(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()), 1, 0), messageResponseList.get(i), response);
//
//                    }
//                }
//                else
//                {
//                    boolean isDeducted=reciverList.get(j).isLiveDeducted();
//                    Log.e("isDeducted","----->>>>> "+isDeducted);
//
//                    if(!senderList.get(i).isMatched() && !reciverList.get(j).isMatched()){
//                    if(!senderList.get(i).isLiveDeducted() && !reciverList.get(j).isLiveDeducted()) {
//                        senderList.get(i).setLiveDeducted(true);
//                        reciverList.get(j).setLiveDeducted(true);
//                        deductLives = true;
//                        if(response!=null) {
////                            CommonUtils.playSound("no_match", ChatActivity.this);
//                        }
//                    }
//                    }
//                }
//
//            }
//        }
//        if(response!=null) {
//            if (deductLives) {
//                live = live - 1;
//                CommonUtils.playSound("life_lost",ChatActivity.this);
//                deductLivesFromBoth(live);
//            }
//
//        }
//        messageResponseList.clear();
//        messageResponseList.addAll(senderList);
//        messageResponseList.addAll(reciverList);
//        Collections.sort(messageResponseList,new CustomSorting());
//        return messageResponseList;
//    }

    private void deductLivesFromBoth(int live) {
        try {
            JSONObject object = new JSONObject();
            object.put("sender_id",SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID));
            object.put("receiver_id",userDataModel.getDataTosave().getReceiverId());
            object.put("game_id",userDataModel.getDataTosave().getGameId());
            object.put("room_id",room_id);
            object.put("card",Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1);
            object.put("lives",""+live);
            mSocket.emit("lives_pass",object);
            Log.i("lives_pass",object.toString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    private void showMatchAndBonusLive(int bonus, long point, MessageResponse senderResponse, MessageResponse receiverResponse) {
        new GameAnimationHelper(binding.getRoot(),this,point).showMatchAnimation(GameCalculation.points(basePoints, getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1)));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvPoints.setText(""+points);
                senderResponse.setBonus(bonus);
                receiverResponse.setBonus(bonus);
                if(bonus>1) {
                    new GameAnimationHelper(binding.getRoot(), ChatActivity.this).showBonusAnimation(bonus, point);}
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("bonus=", ""+bonus);
                            if(bonus>1)
                            {
                                points=points+(point*bonus);
                            }
                            tvPoints.setText(""+points);
                            senderResponse.setPoint(points);
                            receiverResponse.setPoint(points);
                            messageResponseList=checkScoreAndStreak(messageResponseList);
                        }
                    },1100);


            }
        },1100);
    }
    private void calculateCoins(int points) {
        int previousCoins= Integer.parseInt(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.COINS));
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
                   int quest=points/perPoints;
                   if(quest>0)
                   {
                       if(quest!=copiedQuestioned && quest>copiedQuestioned) {
                           copiedQuestioned = quest;
                           coins = preCoins * copiedQuestioned;
                           if (copiedQuestioned > 1) {
                               coins = coins - (preCoins * (copiedQuestioned - 1));
                           }
                           if (coins > 0) {

                               new GameAnimationHelper(binding.getRoot(), ChatActivity.this).showCoinAnimation(perPoints, preCoins);
                               new Handler().postDelayed(new Runnable() {
                                   @Override
                                   public void run() {
                                       long coin = previousCoins + coins;
                                       SharedPreferenceWriter.getInstance(ChatActivity.this).writeStringValue(SPreferenceKey.COINS, "" + coin);
                                       tvcoin.setText("" + coin);
                                       binding.clCoins.setVisibility(View.GONE);
                                   }
                               }, 1100);
                           }
                       }
       }}},1000);
    }
    private List<MessageResponse> checkScoreAndStreak(List<MessageResponse> list) {
        int streak=0;
        boolean streakDown=false;
        List<MessageResponse> senderList = new ArrayList<>();
        List<MessageResponse> reciverList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ID)))
                senderList.add(list.get(i));
            else
                reciverList.add(list.get(i));
        }

        for (int x = 0; x < senderList.size(); x++)
        {
            for(int y=0;y<reciverList.size();y++)
            {

                if(senderList.get(x).isMatched()==reciverList.get(y).isMatched() && senderList.get(x).getMessage().equalsIgnoreCase(reciverList.get(y).getMessage()))
                {
                    streak=streak+1;
                    senderList.get(x).setStreaks(streak);
                    reciverList.get(y).setStreaks(streak);
                    senderList.get(x).setScore(GameCalculation.score(streak,senderList.get(x).getPoint(),senderList.get(y).getBonus()));
                    reciverList.get(y).setScore(GameCalculation.score(streak,reciverList.get(y).getPoint(),reciverList.get(y).getBonus()));
                    long senderPoint=0,receverPoint=0;
                    if(streak>1){
                        senderPoint= GameCalculation.points(basePoints, getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1))*streak*senderList.get(x).getBonus();
                    }else
                    {
                        //senderList.get(x).setStreaks(0);
                    }
                    if(streak>1)
                    {
                        receverPoint= GameCalculation.points(basePoints, getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1))*streak*reciverList.get(y).getBonus();
                    }else
                    {
                        //reciverList.get(y).setStreaks(0);
                    }
                    senderList.get(x).setPoint(senderList.get(x).getPoint()+senderPoint);
                    reciverList.get(y).setPoint(reciverList.get(y).getPoint()+receverPoint);
                    senderList.get(x).setStreak(true);
                    reciverList.get(y).setStreak(true);
                    break;
                }else
                {
                    if(!senderList.get(x).isStreak() && !reciverList.get(y).isStreak()) {
                        senderList.get(x).setStreak(true);
                        reciverList.get(y).setStreak(true);
                        streak = 0;
                        streakDown=true;
                        senderList.get(x).setStreaks(streak);
                        reciverList.get(y).setStreaks(streak);
                        senderList.get(x).setScore(GameCalculation.score(1, senderList.get(x).getPoint(), senderList.get(x).getBonus()));
                        reciverList.get(y).setScore(GameCalculation.score(1, reciverList.get(y).getPoint(), reciverList.get(y).getBonus()));
                    }
                }
            }
        }

        list.clear();
        list.addAll(senderList);
        list.addAll(reciverList);
        Collections.sort(list,new CustomSorting());

        if(streakDown)
        {
            Log.e("StreakDown", "true");
            alreadyGivenStreak=showStreak;
        }
        Log.e("alreadyGiven", ""+alreadyGivenStreak);
        Log.e("totalStreak", ""+streak);
        if(alreadyGivenStreak>0 && streak>1)
        {
            streak=streak-alreadyGivenStreak;
            showStreak=0;
        }

        Log.e("streak=",""+streak);
        Log.e("previousSaved", ""+showStreak);
//        getUpdatedData();

           if(streak>1) {
               if (showStreak != streak && showStreak<streak) {
                   showStreak = streak;
                   long po=GameCalculation.points(basePoints,getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1))*showStreak*bonus;
                   points=points+po;
                   new GameAnimationHelper(binding.getRoot(),this).showStreakAnimation(showStreak,po);
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           tvPoints.setText(""+points);
                           if(data.getGameDetails().getGameType().equalsIgnoreCase("2")){
                               calculateCoins(Integer.parseInt(tvPoints.getText().toString()));
                           }
                       }
                   },1100);
               }
               else
               {
                   tvPoints.setText(""+points);
                   if(data.getGameDetails().getGameType().equalsIgnoreCase("2")){
                       calculateCoins(Integer.parseInt(tvPoints.getText().toString()));
                   }
               }
           }
           else
           {
               tvPoints.setText(""+points);
               if(data.getGameDetails().getGameType().equalsIgnoreCase("2")){
                   calculateCoins(Integer.parseInt(tvPoints.getText().toString()));
               }
           }

         return list;
    }



    private void checkLives(String lives) {
        int live=Integer.parseInt(lives);
        if(live!=1)
        {
            CommonUtils.playSound("life_remaining",ChatActivity.this);
        }
        if(live==8) {
            eightLL.setVisibility(View.VISIBLE);
            tvSeven.setVisibility(View.GONE);
        }
        else if(live==7) {
            eightLL.setVisibility(View.GONE);
            tvSeven.setVisibility(View.VISIBLE);
        }

        else if(live==6) {
            eightLL.setVisibility(View.GONE);
            sevenLL.setVisibility(View.GONE);
            tvSix.setVisibility(View.VISIBLE);
        }
        else if(live==5) {
            eightLL.setVisibility(View.GONE);
            sevenLL.setVisibility(View.GONE);
            sixLL.setVisibility(View.GONE);
            tvFive.setVisibility(View.VISIBLE);
        }
        else if(live==4) {
            eightLL.setVisibility(View.GONE);
            sevenLL.setVisibility(View.GONE);
            sixLL.setVisibility(View.GONE);
            fiveLL.setVisibility(View.GONE);
            tvFour.setVisibility(View.VISIBLE);
        }
        else if(live==3) {
            eightLL.setVisibility(View.GONE);
            sevenLL.setVisibility(View.GONE);
            sixLL.setVisibility(View.GONE);
            fiveLL.setVisibility(View.GONE);
            fourLL.setVisibility(View.GONE);
            tvThree.setVisibility(View.VISIBLE);
        }
        else if(live==2) {
            eightLL.setVisibility(View.GONE);
            sevenLL.setVisibility(View.GONE);
            sixLL.setVisibility(View.GONE);
            fiveLL.setVisibility(View.GONE);
            fourLL.setVisibility(View.GONE);
            threeLL.setVisibility(View.GONE);
            tvTwo.setVisibility(View.VISIBLE);
        }
        else if(live==1) {
            eightLL.setVisibility(View.GONE);
            sevenLL.setVisibility(View.GONE);
            sixLL.setVisibility(View.GONE);
            fiveLL.setVisibility(View.GONE);
            fourLL.setVisibility(View.GONE);
            threeLL.setVisibility(View.GONE);
            twoLL.setVisibility(View.GONE);
            tvOne.setVisibility(View.VISIBLE);
        }
        else if(live==0) {
            gameOverIntent();
        }
    }
    private MessageResponse checkMatchedChatTime(MessageResponse response) {
        for(int i= 0;i<messageResponseList.size();i++) {
            if(!messageResponseList.get(i).isMatched()){
                if (messageResponseList.get(i).getMessage().equalsIgnoreCase(response.getMessage())) {
                    CommonUtils.playSound("match",ChatActivity.this);
                    messageResponseList.get(i).setMatched(true);
                    response.setMatched(true);
                    match=match+1;
                    points = points + GameCalculation.points(basePoints,getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1));
                    Log.e("points=", ""+points);
                    messageResponseList.get(i).setPoint(points);
                    response.setPoint(points);
                    messageResponseList.get(i).setAll_matched(getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1));
                    response.setAll_matched(getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1));
                    if(messageResponseList.get(i).getMessage().contains(" "))
                    {
                       String count[]= messageResponseList.get(i).getMessage().split(" ");
                       bonus=count.length;
                    }
                    else
                    {
                       bonus=1;
                    }
                    Log.e("bouns=", ""+bonus);
                    showMatchAndBonusTime(bonus, GameCalculation.points(basePoints,  getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1)), i, response);
                }
                else
                {
//                    bonus=0;
//                    messageResponseList.get(i).setBonus(0);
                }
            }
        }

        if(!response.isMatched())
        {
//            response.setBonus(0);
            CommonUtils.playSound("no_match",ChatActivity.this);
        }

        return  response;
    }
    private void showMatchAndBonusTime(int bonus,long point,int pos,MessageResponse response) {
        new GameAnimationHelper(binding.getRoot(),this,point).showMatchAnimation(GameCalculation.points(basePoints, getAllMatch(Integer.parseInt(binding.nextText.getText().toString().split("/")[0].trim()) - 1)));
           new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               tvPoints.setText("" + points);
               messageResponseList.get(pos).setBonus(bonus);
               response.setBonus(bonus);
               if(bonus>1){
                   new GameAnimationHelper(binding.getRoot(), ChatActivity.this).showBonusAnimation(bonus, point);
               }

               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       if(bonus>1)
                       {
                           points=points+(point*bonus);
                       }
                       tvPoints.setText(""+points);
                       response.setPoint(points);
                       messageResponseList.get(pos).setPoint(points);
                       messageResponseList= checkScoreAndStreak(messageResponseList);
                   }
               },1100);

           }
       },1100);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.toString().length()>0)
        {
            if(!isTyping) typingJsonSend();
        }
        else
        {
            stopTypingJsonSend();
        }

    }

    private void typingJsonSend() {
        try {
            JSONObject object = new JSONObject();
            object.put("sender_id",SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID));
            object.put("room_id",room_id);
            mSocket.emit("typing_in",object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void stopTypingJsonSend() {
        try {
            JSONObject object = new JSONObject();
            object.put("sender_id",SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID));
            object.put("room_id",room_id);
            mSocket.emit("stop_typing",object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        isMessageReceived=true;
        chatBoxEd.setText("");
        chatBoxEd.setTextSize(14);
        chatBoxEd.setBackground(ChatActivity.this.getResources().getDrawable(R.drawable.bg_const_white_edittext));
        chatBoxEd.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (bottom < oldBottom) {
            binding.chatRv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(binding.chatRv.getAdapter()!=null) {
                        if (binding.chatRv.getAdapter().getItemCount() > 0) {
                            binding.chatRv.smoothScrollToPosition(binding.chatRv.getAdapter().getItemCount() - 1);
                        }
                    }
                }
            }, 1);
        }
    }

    private boolean checkUserData(SocketModel response) {
        boolean ret=false;
        if(response.getDataTosave().getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)) || response.getDataTosave().getReceiverId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)) &&
          response.getDataTosave().getReceiverId().equalsIgnoreCase(userDataModel.getDataTosave().getReceiverId()) || response.getDataTosave().getSenderId().equalsIgnoreCase(userDataModel.getDataTosave().getReceiverId()) && response.getDataTosave().getGameId().equalsIgnoreCase(userDataModel.getDataTosave().getGameId()))
        {
            ret=true;
        }

        return ret;
    }



}

