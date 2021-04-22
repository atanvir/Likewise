package com.likewise.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.likewise.Adapter.ImgeSliderAdapter;
import com.likewise.Model.CardImageModel;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.CreateGameModel;
import com.likewise.Model.CreateGameResponse;
import com.likewise.Model.Morerecisely;
import com.likewise.Model.Objective;
import com.likewise.Model.PlayGameResponseModel;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.AddRequestBody;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.HugeDataHelper;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivitySearchingPatnerBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import me.crosswall.lib.coverflow.CoverFlow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchingPatner extends AppCompatActivity {

    private ActivitySearchingPatnerBinding binding;
    private Timer timer = new Timer();
    private List<PlayGameResponseModel.UserDetail> listPatner= new ArrayList<>();
    private List<String> cardList= new ArrayList<>();
    private PlayGameResponseModel.UserDetail randomPatner;
    private PlayGameResponseModel.Data modelData;
    private MediaPlayer mediaPlayer;
    private List<String> duplicateCardsList = new ArrayList<>();
    private boolean sound;
    private boolean isBackPressed=true;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_searching_patner);
//        modelData=getIntent().getParcelableExtra("patners");
        modelData= HugeDataHelper.getInstance().getHugeData();
        ImageGlider.setRoundImage(this,binding.ivFlag,binding.progressBar,getIntent().getStringExtra("flagImage"));
        binding.tvLanguage.setText(getIntent().getStringExtra("langauge"));
        binding.tvDescription.setText(getIntent().getStringExtra("description"));
        List<PlayGameResponseModel.UserDetail> listData=modelData.getUserDetails();
        Collections.shuffle(listData);
        listPatner=getPatnerData(listData);
        binding.viewPager.setAdapter(new ImgeSliderAdapter(this,listPatner));
        sound=SharedPreferenceWriter.getInstance(this).getBoolean(SPreferenceKey.SOUND);
        setAutoSliding();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private List<PlayGameResponseModel.UserDetail> removeMyData(List<PlayGameResponseModel.UserDetail> userDetails) {
        List<PlayGameResponseModel.UserDetail> copiedData= new ArrayList<>();
        for(int i=0;i<userDetails.size();i++)
        {
            if(userDetails.get(i).getId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)))
            {
                userDetails.remove(i);
                break;
            }
        }

        copiedData=userDetails;

        return copiedData;
    }

    private List<PlayGameResponseModel.UserDetail> getPatnerData(List<PlayGameResponseModel.UserDetail> list) {
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i)==null)
            {
                list.remove(i);
            }
        }
        return list;

    }

    private void setAutoSliding() {
        new CoverFlow.Builder()
                .with(binding.viewPager)
                .pagerMargin(0f)
                .scale(0.3f)
                .spaceSize(0f)
                .rotationY(0f)
                .build();


        binding.viewPager.post(new Runnable() {
            @Override public void run() {
                ConstraintLayout imageCL = (ConstraintLayout) binding.viewPager.getAdapter().instantiateItem(binding.viewPager, 0);
                ViewCompat.setElevation(imageCL, 8.0f);
            }
        });
        if(sound) {
            mediaPlayer = MediaPlayer.create(this, R.raw.searching);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        countDownTimer= new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isBackPressed=true;
                        if (binding.viewPager.getCurrentItem() < binding.viewPager.getAdapter().getCount() - 1) {
                            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
                        }
                    }
                });
            }

            @Override
            public void onFinish() {
                if (isBackPressed) {
                    isBackPressed=false;
                    if (sound) {
                        mediaPlayer.stop();
                    }
                    CommonUtils.playSound("partner_found", SearchingPatner.this);
                    timer.cancel();
                    Random random = new Random();
                    randomPatner = listPatner.get(random.nextInt(listPatner.size()));
                    binding.matchCl.setVisibility(View.VISIBLE);
                    binding.pagerContainer.setVisibility(View.GONE);
                    ImageGlider.setRoundImage(SearchingPatner.this, binding.ivUser, binding.progressBar2, randomPatner.getProfilePic());
                    binding.tvName.setText(randomPatner.getName());
                    if (randomPatner.getGender() != null && randomPatner.getCountry() != null) {
                        binding.tvExtra.setText(randomPatner.getGender() + "," + randomPatner.getCountry());
                    }
                    binding.headerText.setText(getString(R.string.patner_found));
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < modelData.getFollowerDetail().size(); i++) {
                                cardList.add(modelData.getFollowerDetail().get(i).getId().getImage());
                            }

                            Collections.shuffle(cardList);
                            if (cardList.size() >= 6) countCardValueApi(0, 6);
                            else countCardValueApi(0, cardList.size());
                        }
                    });
                }
            }
        };
       countDownTimer.start();
   //    timer.scheduleAtFixedRate(new SliderTimer(), 1000, 1000);

    }

    private class SliderTimer extends TimerTask {
        @Override
        public void run() {

        }
    }

    private void countCardValueApi(int startOff,int endOff) {
        try {
            ServicesInterface anInterface= ServicesConnection.getInstance().createService(this);
            Call<CommonModelDataObject> call=anInterface.countCardValue(SharedPreferenceWriter.getInstance(SearchingPatner.this).getString(SPreferenceKey.ID),
                                                                        randomPatner.getId(),getDataByIndexes(cardList,startOff,endOff));
            ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataObject>() {
                @Override
                public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                    if(response.isSuccessful())
                    {
                        CommonModelDataObject serverResponse = response.body();
                        if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                        {
                            checkImage(serverResponse.getData().getCardImage());
                            if(endOff<=cardList.size()) {
                                if (duplicateCardsList.size() != 6) {
                                    if (cardList.size() >= endOff + 1) {
                                        if (cardList.size() >= endOff + 7)
                                            countCardValueApi(endOff + 1, endOff + 6);
                                        else countCardValueApi(endOff + 1, cardList.size());
                                    } else {
                                        gameCreateApi();
                                    }
                                } else {
                                    gameCreateApi();
                                }
                            }else
                            {
                                if(duplicateCardsList.size()>0)
                                {
                                    gameCreateApi();
                                }else
                                {
                                    Toast.makeText(SearchingPatner.this,"Sorry Currently we have no card for you",Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }

                        }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                        {
                            Log.e("failure",serverResponse.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonModelDataObject> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        };

    }

    private void checkImage(List<CardImageModel> list) {
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).isMatched())
            {
                if(duplicateCardsList.size()!=6) duplicateCardsList.add(list.get(i).getImage());
            }
        }

    }

    private List<String> getDataByIndexes(List<String> list,int startOff,int endOff) {
       List<String> stringList = new ArrayList<>();
        for(int i=startOff;i<endOff;i++)
        {
         stringList.add(list.get(i));
        }

        return stringList;
    }
    private void gameCreateApi() {
    try {
        Log.e("objectiveId", getIntent().getStringExtra("objectiveId"));
        Log.e("morePreciouslyId", getIntent().getStringExtra("morePreciouslyId"));
//        Collections.shuffle(duplicateCardsList);
        ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
        AddRequestBody body = new AddRequestBody(new CreateGameModel(getIntent().getStringExtra(ParamEnum.MODE.theValue()),
                                                                     getIntent().getStringExtra("langCode"),
                                                            "",
                                                                     "5e9c4c38fc5a391fb1d11a27",
                                                                     "5e8ada3529f207737e532bb7",
                                                                     randomPatner.getId(),duplicateCardsList,
                                                                "random_patner"));

        Call<CreateGameResponse> call = anInterface.gameCreate(null,body.getBody());
        ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CreateGameResponse>() {
            @Override
            public void onResponse(Call<CreateGameResponse> call, Response<CreateGameResponse> response) {
                if(response.isSuccessful())
                {
                    CreateGameResponse serverResponse=response.body();
                    if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                    {
//                        CommonUtils.showSnackBar(SearchingPatner.this,serverResponse.getMessage(),ParamEnum.Success.theValue());
                        //Mode Time
                        if(serverResponse.getData().getGame_details().getMode().equalsIgnoreCase("1")) {
                            Intent intent=new Intent(SearchingPatner.this,InvitationActivity.class);
                            intent.putExtra("name",randomPatner.getName());
                            intent.putExtra("profilepic",randomPatner.getProfilePic());
                            intent.putExtra("receiver_id",serverResponse.getData().getReceiver_id());
                            intent.putExtra("gameId",serverResponse.getData().getGame_details().getId());
                            intent.putExtra("notificationId",serverResponse.getNotificationId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        // Mode Live
                        else if(serverResponse.getData().getGame_details().getMode().equalsIgnoreCase("2"))
                        {
                            Intent intent=new Intent(SearchingPatner.this,ChatActivity.class);
                            intent.putExtra("cameFrom",CreateGameActivity.class.getSimpleName());
                            intent.putExtra("userData",response.body().getData());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                    {
                        CommonUtils.showSnackBar(SearchingPatner.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                    }
                }

            }

            @Override
            public void onFailure(Call<CreateGameResponse> call, Throwable t) {
                Log.e("failure",t.getMessage());

            }
        });
    } catch (Exception e) {
        Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

    }
    }

    private List<String> getRandomCard(List<String> list) {
        List<String> imgList = new ArrayList<>();
        int count=1;
        List<Integer> integerList= new ArrayList<>();
        for(int i=0;i<list.size();i++)
        {
            integerList.add(count);
            count++;
        }
        Random random = new Random();
        Integer integer=integerList.get(random.nextInt(integerList.size()));
        for(int i=0;i<integer;i++)
        {
            imgList.add(list.get(i));
        }

        Log.e("randomCard",""+integer);


        return imgList;
    }

    @Override
    public void onBackPressed() {
        if(mediaPlayer!=null)
        {
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
        }
        if(countDownTimer!=null)
        {
            isBackPressed=false;
            countDownTimer.cancel();
            countDownTimer.onFinish();
        }


        super.onBackPressed();
    }
}
