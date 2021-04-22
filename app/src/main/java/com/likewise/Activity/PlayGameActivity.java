package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.likewise.Adapter.SelectLanguageAdapter;
import com.likewise.Interface.OnItemClickListner;
import com.likewise.Model.CommonLanguageModel;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.Laguage;
import com.likewise.Model.LanguageModel;
import com.likewise.Model.Morerecisely;
import com.likewise.Model.Objective;
import com.likewise.Model.PlayGameResponseModel;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.HugeDataHelper;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityPlayGameBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayGameActivity extends AppCompatActivity implements View.OnClickListener, OnItemClickListner {
    ActivityPlayGameBinding binding;
    //layout header
    ImageView closeIv,qstIv;
    String type="";
    private String langCode="",langauge="",flagImage="",description="",objectiveId="",morePreciouslyId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_play_game);
        init();
        languagesApi(SharedPreferenceWriter.getInstance(PlayGameActivity.this).getString(SPreferenceKey.DEFAULT_LANGUAGE_CODE));
        type="2";
        settingBackground(type);
    }

    private void init() {
        closeIv=findViewById(R.id.closeIv);
        qstIv=findViewById(R.id.qstIv);
        closeIv.setOnClickListener(this);
        binding.liveIv.setOnClickListener(this);
        binding.timeIv.setOnClickListener(this);
        binding.Gobtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView headerText = findViewById(R.id.headerText);
        headerText.setText("Play with a Random Partner");
    }

    private void languagesApi(String defaultLangCode) {
        if(CommonUtils.networkConnectionCheck(this))
        {
            try {
                ServicesInterface anInterface= ServicesConnection.getInstance().createService(this);
                Call<CommonLanguageModel> call=anInterface.listLanguage(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.LANGUAGE_CODE));
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonLanguageModel>() {
                    @Override
                    public void onResponse(Call<CommonLanguageModel> call, Response<CommonLanguageModel> response) {
                        if(response.isSuccessful())
                        {
                            CommonLanguageModel serverResponse = response.body();
                            List<LanguageModel> listLanguages=serverResponse.getStatus();
                            LinearLayoutManager manager=new LinearLayoutManager(PlayGameActivity.this);
                            manager.setOrientation(RecyclerView.HORIZONTAL);
                            binding.countryRv.setLayoutManager(manager);
                            binding.countryRv.setAdapter(new SelectLanguageAdapter(PlayGameActivity.this,getUserSelectedLanguages(serverResponse.getData(),listLanguages,defaultLangCode),PlayGameActivity.this));
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonLanguageModel> call, Throwable t) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
        {
            CommonUtils.showSnackBar(PlayGameActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    private List<ResponseBean> getUserSelectedLanguages(List<ResponseBean> countryList,List<LanguageModel> playingLang,String nationality) {
        List<ResponseBean> lists=new ArrayList<>();
        List<String> langCodes=new ArrayList<>(Arrays.asList(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.LANGUAGE_CODE).split(",")));
        for(int i=0 ;i<countryList.size();i++)
        {
            for(int j=0;j<langCodes.size();j++)
            { if(countryList.get(i).getCode().equalsIgnoreCase(langCodes.get(j)))
            {
                lists.add(countryList.get(i));
            }
            }
        }

        for(int i=0;i<lists.size();i++)
        {
            if(lists.get(i).getCode().equalsIgnoreCase(SharedPreferenceWriter.getInstance(PlayGameActivity.this).getString(SPreferenceKey.DEFAULT_LANGUAGE_CODE)))
            {
                lists.get(i).setChecked(true);
                langCode=lists.get(i).getCode();
                langauge=lists.get(i).getLanguage();
                description=lists.get(i).getDescription();
                flagImage=lists.get(i).getPicture();
                break;
            }
        }

        for(int i=0;i<lists.size();i++)
        {
            for(int j=0;j<playingLang.size();j++)
            {
                if(playingLang.get(j).getLanguage().equalsIgnoreCase(lists.get(i).getCode()))
                {
                       lists.get(i).setEnabled(playingLang.get(j).isStatus());
                       break;
                }

            }
        }


        return lists;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.qstIv:
                break;

            case R.id.closeIv:
            onBackPressed();
            break;

            case R.id.Gobtn:
            if(checkValidation()) {
                getFindPatnerApi();
            }
            break;


            case R.id.liveIv:
            type="2";
            settingBackground(type);
            break;

            case R.id.timeIv:
            type="1";
            settingBackground(type);
            break;


        }
    }

    private void getFindPatnerApi() {
        try {
            ServicesInterface anInterface= ServicesConnection.getInstance().createService(this);
            Call<PlayGameResponseModel> call=anInterface.getFindPatner(langCode,type);
            ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<PlayGameResponseModel>() {
                @Override
                public void onResponse(Call<PlayGameResponseModel> call, Response<PlayGameResponseModel> response) {
                    if(response.isSuccessful())
                    {
                        PlayGameResponseModel serverResponse = response.body();
                        if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                        {
                            getRandomIds(serverResponse.getData());
                            removeMyData(serverResponse.getData().getUserDetails());
                            if(serverResponse.getData().getUserDetails().size()>0)
                            {
                                Intent intent = new Intent(PlayGameActivity.this, SearchingPatner.class);;
                                intent.putExtra(ParamEnum.MODE.theValue(), type);
                                HugeDataHelper.getInstance().setHugeData(serverResponse.getData());
                                intent.putExtra("flagImage",flagImage);
                                intent.putExtra("langCode",langCode);
                                intent.putExtra("langauge",langauge);
                                intent.putExtra("description",description);
                                intent.putExtra("objectiveId",objectiveId);
                                intent.putExtra("morePreciouslyId",morePreciouslyId);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else
                            {
                                Intent intent = new Intent(PlayGameActivity.this,NoPatnerFoundActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                        }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                        {
                            if(serverResponse.getMessage().equalsIgnoreCase("no partner found."))
                            {
                                Intent intent = new Intent(PlayGameActivity.this,NoPatnerFoundActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                CommonUtils.showSnackBar(PlayGameActivity.this, serverResponse.getMessage(), ParamEnum.Success.theValue());

                            }
                        }

                    }

                }

                @Override
                public void onFailure(Call<PlayGameResponseModel> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        }

    private void getRandomIds(PlayGameResponseModel.Data data) {
        Random random = new Random();
        Objective objective= data.getExplanationModel().get(random.nextInt(data.getExplanationModel().size()));
        objectiveId=objective.getId();
        List<Morerecisely> morereciselyList = new ArrayList<>();

        for(int i=0;i<data.getConverseModel().size();i++) {
            Log.e("pos",""+i);
            if(data.getConverseModel().get(i).getExplanationId()!= null) {
                if (data.getConverseModel().get(i).getExplanationId().equalsIgnoreCase(objectiveId)) {
                    morereciselyList.add(data.getConverseModel().get(i));
                }
            }
        }

       Morerecisely morerecisely= morereciselyList.get(random.nextInt(morereciselyList.size()));
       morePreciouslyId=morerecisely.getId();
    }

    private boolean checkValidation() {
        boolean ret=true;
        if(langCode.equalsIgnoreCase(""))
        {
            ret=false;
            CommonUtils.showSnackBar(this,"Please Select Language",ParamEnum.Failure.theValue());
        }

        return ret;
    }

    private void settingBackground(String type) {
        if(type.equalsIgnoreCase("2")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.liveIv.setBackground(this.getResources().getDrawable(R.drawable.drawable_live_selected));
                    binding.timeIv.setBackground(this.getResources().getDrawable(R.drawable.drawable_time_un_selected));
                }


            }
        else if(type.equalsIgnoreCase("1"))
        {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.timeIv.setBackground(this.getResources().getDrawable(R.drawable.drawable_time_selected));
                    binding.liveIv.setBackground(this.getResources().getDrawable(R.drawable.drawable_live_un_selected));
                }
            }

        }

    @Override
    public void onSocialPatnerSelected(String socialId) {

    }

    @Override
    public void onLanguageSelected(String langCode,String languge,String description,String flagImage) {
        this.langCode=langCode;
        this.langauge=languge;
        this.flagImage=flagImage;
        this.description=description;
    }

    @Override
    public void onObjectiveSelected(String objectiveId) {

    }

    @Override
    public void onMorePrecislySelected(String morePreciouslyId) {

    }

    @Override
    public void onSelectedImagesCalled() {

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

}
