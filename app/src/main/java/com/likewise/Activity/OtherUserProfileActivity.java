package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.likewise.Adapter.LeaderBoardAdapter;
import com.likewise.Adapter.SelectLanguageAdapter;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityProfileDetailBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hari.bounceview.BounceView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OtherUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityProfileDetailBinding binding;
    //layout below about me
    RecyclerView languageRV;
    ImageView ivAddLanguage;

    //Interest
    private EditText edOneInterest,edTwoInterest,edThreeInterest;

    //Residence
    private EditText countryEd,stateEd,cityEd;

    //Nationality
    private ImageView ivNationality;
    private TextView tvNationality;

    //Variable & Objects
    private CommonModelDataObject serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_profile_detail);
        init();
        CommonUtils.showLoadingDialog(this);
        getOtherUserDetailApi();
    }

    private void getOtherUserDetailApi() {
     try {
         ServicesInterface anInterface= ServicesConnection.getInstance().createService(this);
         Call<CommonModelDataObject> call=anInterface.getOtherUserDatils(getIntent().getStringExtra("_id"));
//         Call<CommonModelDataObject> call=anInterface.getOtherUserDatils("5fbb69151230db25b5391ec6");
         ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataObject>() {
             @Override
             public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                 if(response.isSuccessful())
                 {
                     CommonModelDataObject serverResponse=response.body();
                     if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                     {
                         CommonUtils.dismissLoadingDialog();
                         setUserProfile(serverResponse);

                     } else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                     {
                         CommonUtils.dismissLoadingDialog();
                         CommonUtils.showSnackBar(OtherUserProfileActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                     }
                 }

             }

             @Override
             public void onFailure(Call<CommonModelDataObject> call, Throwable t) {

             }
         });

     }catch (Exception e)
     {
         e.printStackTrace();
     }
    }

    private void userFriendFacbookApi(CommonModelDataObject serverResponse) {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this,"https://graph.facebook.com/v2.3/");
                Call<CommonModelDataList> call = anInterface.userFriendFacbook(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.SOCIAL_ID));
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataList>() {
                    @Override
                    public void onResponse(Call<CommonModelDataList> call, Response<CommonModelDataList> response) {
                        CommonUtils.dismissLoadingDialog();
                        if(response.isSuccessful())
                        {
                            setUserProfile(getRealFacebookFriends(serverResponse,response.body()));
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataList> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private CommonModelDataObject getRealFacebookFriends(CommonModelDataObject serverResponse, CommonModelDataList fbFriendList) {
        serverResponse.getData().getReceiverDetails().setType(0);
        for(int i=0;i<fbFriendList.getData().size();i++)
        {
            if(serverResponse.getData().getReceiverDetails().getSocialId().equalsIgnoreCase(fbFriendList.getData().get(i).getId()))
            {
                serverResponse.getData().getReceiverDetails().setType(1);
                break;
            }
        }

        return serverResponse;
    }

    private void setUserProfile(CommonModelDataObject serverResponse) {
        this.serverResponse=serverResponse;
        binding.tvRank.setText(""+Math.round(Double.parseDouble(serverResponse.getData().getReceiverDetails().getUserValue())));
        binding.tvActivity.setText(""+serverResponse.getData().getWordCountAlls()+" words");
        binding.tvAccuracy.setText(""+Math.round(serverResponse.getData().getAccuracy())+"%");
        if(serverResponse.getMode()!=null) {
            binding.mode.setImageResource(getModeType(serverResponse.getData().getFinalFavoriteModes()));
        }
        binding.tvPlayed.setText(""+serverResponse.getData().getTotalMatchFind());
        binding.tvCreated.setText(""+serverResponse.getData().getCreateMatchFind());
        binding.tvPatners.setText(""+serverResponse.getData().getCommonMatchFind());
        binding.userFullNameText.setText(serverResponse.getData().getReceiverDetails().getName());
        ImageGlider.setRoundImage(this,binding.ivprofile,binding.progressBar,serverResponse.getData().getReceiverDetails().getProfilePic());
        // Personal Information
        binding.nameEd.setText(serverResponse.getData().getReceiverDetails().getName());
        binding.unameEd.setText(serverResponse.getData().getReceiverDetails().getUsername());
        binding.genderEd.setText(serverResponse.getData().getReceiverDetails().getGender());
        binding.dobEd.setText(CommonUtils.changeFormatDate(serverResponse.getData().getReceiverDetails().getDob()));
        binding.aboutEd.setText(serverResponse.getData().getReceiverDetails().getAboutus());
        binding.coinsText.setText(""+serverResponse.getData().getReceiverDetails().getCoins());
        binding.tvtotalPoints.setText(""+serverResponse.getData().getReceiverDetails().getTotalPoints());

//        if(serverResponse.getData().getReceiverDetails().getType()==1)
//        {
//            binding.btnPlay.setVisibility(View.VISIBLE);
//            binding.abtHeadText.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.fb_home),null);
//        }

        new CommonUtils().setProgressbar(binding.ProgressBar,binding.progressPercText, (int) Math.round(serverResponse.getData().getReceiverDetails().getAll_LikeWise_Persantege()));

        if(serverResponse.getData().getLikewisePersantegess()!=0.0)
        {
            binding.ProgressBar.setVisibility(View.VISIBLE);
            binding.progressPercText.setVisibility(View.VISIBLE);
            binding.tvperct.setVisibility(View.VISIBLE);

        }
        binding.btnPlay.setVisibility(View.VISIBLE);
        new CommonUtils().setProgressbar(binding.ProgressBar,binding.progressPercText, (int) Math.round(serverResponse.getData().getLikewisePersantegess()));
        tvNationality.setText(serverResponse.getData().getNationalit());
        ImageGlider.setRoundImage(this,ivNationality,null,getCountryImage(serverResponse.getData().getReceiverDetails().getNationalit(),serverResponse.getData().getCountryList()));


        LinearLayoutManager manager =new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        languageRV.setLayoutManager(manager);
        languageRV.setAdapter(new SelectLanguageAdapter(this,getUserLangCodes(serverResponse.getData().getReceiverDetails().getLanguageCode(),serverResponse.getData().getCountryList(),serverResponse.getData().getReceiverDetails().getDefaultLangCode()),null));
        tvNationality.setText(serverResponse.getData().getReceiverDetails().getNationalit());
        countryEd.setText(serverResponse.getData().getReceiverDetails().getCountry());
        stateEd.setText(serverResponse.getData().getReceiverDetails().getState());
        cityEd.setText(serverResponse.getData().getReceiverDetails().getCity());
        String interest[] = null;
        if(serverResponse.getData().getReceiverDetails().getInterest().contains(","))
        {
            interest=serverResponse.getData().getReceiverDetails().getInterest().split(",");
            if(interest.length==1) {
                edOneInterest.setText(interest[0]);
            }else if(interest.length==2)
            {
                edOneInterest.setText(interest[0]);
                edTwoInterest.setText(interest[1]);
            }else if(interest.length==3)
            {
                edOneInterest.setText(interest[0]);
                edTwoInterest.setText(interest[1]);
                edThreeInterest.setText(interest[2]);
            }
        }


    }

    private String getCountryImage(String nationalit,List<CommonModelDataObject.CountryList> lists) {
        String image = "";
        for(int i=0;i<lists.size();i++) {
            if (lists.get(i).getDescription().equalsIgnoreCase(nationalit)) {
                image= lists.get(i).getPicture();
                break;
            }
        }

        return image;
    }

    private int getModeType(Integer mode) {
        int imageId;
        switch (mode)
        {

            case 1: imageId=R.drawable.time_h; break;

            case 2: imageId=R.drawable.life_un; break;

            default: imageId=R.drawable.life_un; break;
        }

        return imageId;
    }

    private List<CommonModelDataObject.CountryList> getUserLangCodes(List<String> langCodes,List<CommonModelDataObject.CountryList> countryList,String nationality) {
        List<String> langList= langCodes;
        List<CommonModelDataObject.CountryList> lists=new ArrayList<>();
        for(int i=0 ;i<countryList.size();i++)
        {
            for(int j=0;j<langList.size();j++)
            {

                if(countryList.get(i).getCode().equalsIgnoreCase(langList.get(j)))
                {
                    lists.add(countryList.get(i));
                }
            }
        }

        for(int i=0;i<lists.size();i++)
        {
            if(lists.get(i).getCode().equalsIgnoreCase(nationality))
            {
                lists.get(i).setChecked(true);
                break;
            }
        }

        return lists;
    }

    private void init() {

        binding.btnPlay.setOnClickListener(this);
        BounceView.addAnimTo(binding.btnPlay);

        //layout below about me
        languageRV=findViewById(R.id.languageRV);

        //Nationality

        ivNationality=findViewById(R.id.ivNationality);
        tvNationality=findViewById(R.id.tvNationality);

        //Residence
        countryEd=findViewById(R.id.countryEd);
        stateEd=findViewById(R.id.stateEd);
        cityEd=findViewById(R.id.cityEd);

        //Interests
        edOneInterest=findViewById(R.id.edOneInterest);
        edTwoInterest=findViewById(R.id.edTwoInterest);
        edThreeInterest=findViewById(R.id.edThreeInterest);

        //layout below about me
        languageRV=findViewById(R.id.languageRV);
        ivAddLanguage=findViewById(R.id.ivAddLanguage);
        ivAddLanguage.setVisibility(View.GONE);

        //Main layout
        binding.closeIv.setOnClickListener(this);
        binding.tvtotalPoints.setOnClickListener(this);
        binding.coinsText.setOnClickListener(this);


        LinearLayoutManager manager =new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        languageRV.setLayoutManager(manager);
        languageRV.setAdapter(new SelectLanguageAdapter(this,null,null));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnPlay:
            Log.e("defaultLangCode","-->>>"+serverResponse.getData().getReceiverDetails().getDefaultLangCode());
            Intent intent = new Intent(this,CreateGameActivity.class);
            intent.putExtra("socialId", serverResponse.getData().getReceiverDetails().getId());
            intent.putExtra("isFromInvite", true);
            intent.putExtra("name", serverResponse.getData().getReceiverDetails().getName());
            intent.putExtra("defaultLangCode",serverResponse.getData().getReceiverDetails().getDefaultLangCode());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            break;

            case R.id.closeIv:
            onBackPressed();
            break;

            case R.id.coinsText:
            Intent coinIntent=new Intent(this,GetCoinsActivity.class);
            coinIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(coinIntent);
            break;

            case R.id.tvtotalPoints:
            Intent pointIntent=new Intent(this,LeaderboardActivity.class);
            pointIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(pointIntent);
            break;
        }
    }
}
