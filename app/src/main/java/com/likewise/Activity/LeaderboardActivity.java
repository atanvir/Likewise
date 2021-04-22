package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.likewise.Adapter.LeaderBoardAdapter;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityLeaderboardBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {
    //layout header
    TextView headerText,footerText;
    ImageView closeIv,qstIv;
    ActivityLeaderboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this, R.layout.activity_leaderboard);
        binding.profileCl.setOnClickListener(this);
        init();
        CommonUtils.showLoadingDialog(this);
        leaderBoardApi();
    }

    private void leaderBoardApi() {
        if(CommonUtils.networkConnectionCheck(this))
        {
            try {
                ServicesInterface anInterface= ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataList> call=anInterface.leaderboard(SharedPreferenceWriter.getInstance(LeaderboardActivity.this).getString(SPreferenceKey.ID));
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataList>() {
                   @Override
                    public void onResponse(Call<CommonModelDataList> call, Response<CommonModelDataList> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataList serverResponse = response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                if(SharedPreferenceWriter.getInstance(LeaderboardActivity.this).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("1")) {
                                    userFriendFacbookApi(serverResponse);
                                }else
                                {
                                    CommonUtils.dismissLoadingDialog();
                                    List<ResponseBean> allDataList =response.body().getData();
                                    for(int i=0;i<allDataList.size();i++)
                                    {
                                        allDataList.get(i).getReceiverDetails().setType(0);
                                    }
                                    List<ResponseBean> userDataList=getUserData(allDataList);

                                    ImageGlider.setRoundImage(LeaderboardActivity.this,binding.userIv,binding.progressBar,userDataList.get(0).getReceiverDetails().getProfilePic());
                                    if(userDataList.get(0).getReceiverDetails().getType()==1)
                                    {
                                        binding.tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,LeaderboardActivity.this.getResources().getDrawable(R.drawable.fb_home),null);
                                    }
                                    binding.tvName.setText(userDataList.get(0).getReceiverDetails().getName());
                                    binding.tvScore.setText(""+userDataList.get(0).getReceiverDetails().getTotalPoints()+" points");
                                    binding.tvCommonGames.setText(""+userDataList.get(0).getGameCounts()+" games");
                                    binding.tvRank.setText(""+Math.round(Double.parseDouble(userDataList.get(0).getReceiverDetails().getUserValue())));
                                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(LeaderboardActivity.this));
                                    binding.recyclerView.setAdapter(new LeaderBoardAdapter(LeaderboardActivity.this,getOthersData(allDataList,userDataList.get(0).getReceiverDetails().getTotalPoints())));
                                }
                            }
                            else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.dismissLoadingDialog();
                                CommonUtils.showSnackBar(LeaderboardActivity.this,serverResponse.getMessage(),ParamEnum.Success.theValue());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModelDataList> call, Throwable t) {
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }else
        {
            CommonUtils.showSnackBar(this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    private void userFriendFacbookApi(CommonModelDataList serverResponse) {
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
                            CommonModelDataList fbfreindsresponse=response.body();
                            List<ResponseBean> allDataList =getRealFacebookFriends(serverResponse,fbfreindsresponse);
                            List<ResponseBean> userDataList=getUserData(allDataList);

                            ImageGlider.setRoundImage(LeaderboardActivity.this,binding.userIv,binding.progressBar,userDataList.get(0).getReceiverDetails().getProfilePic());
                            if(userDataList.get(0).getReceiverDetails().getType()==1)
                            {
                                binding.tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,LeaderboardActivity.this.getResources().getDrawable(R.drawable.fb_home),null);
                            }
                            binding.tvName.setText(userDataList.get(0).getReceiverDetails().getName());
                            binding.tvScore.setText(""+userDataList.get(0).getReceiverDetails().getTotalPoints()+" points");
                            binding.tvCommonGames.setText(""+userDataList.get(0).getGameCounts()+" games");
                            binding.tvRank.setText(""+Math.round(Double.parseDouble(userDataList.get(0).getReceiverDetails().getUserValue())));
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(LeaderboardActivity.this));
                            binding.recyclerView.setAdapter(new LeaderBoardAdapter(LeaderboardActivity.this,getOthersData(allDataList,userDataList.get(0).getReceiverDetails().getTotalPoints())));
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
            CommonUtils.dismissLoadingDialog();
            CommonUtils.showSnackBar(this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    private List<ResponseBean> getRealFacebookFriends(CommonModelDataList serverResponse, CommonModelDataList fbfreindsresponse) {

        for(int l=0;l<serverResponse.getData().size();l++)
        {
            serverResponse.getData().get(l).getReceiverDetails().setType(0);
        }

        for(int i=0;i<serverResponse.getData().size();i++)
        {
            for( int j=0;j<fbfreindsresponse.getData().size();j++)
            {
                if(serverResponse.getData().get(i).getReceiverDetails().getSocialId().equalsIgnoreCase(fbfreindsresponse.getData().get(j).getId())) {
                    serverResponse.getData().get(i).getReceiverDetails().setType(1);
                    break;
                }
            }
        }


        return serverResponse.getData();

    }

    private List<ResponseBean> getUserData(List<ResponseBean> allUserDataList) {
        List<ResponseBean> bean = new ArrayList<>();
        for (int i=0;i<allUserDataList.size();i++)
        {
            if(allUserDataList.get(i).getReceiverDetails().getId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)))
            {
                bean.add(allUserDataList.get(i));
                break;

            }
        }

        return bean;
    }


    private List<ResponseBean> getOthersData(List<ResponseBean> data,int totalPoints) {
        List<ResponseBean> list= new ArrayList<>();
        for(int i=0;i<data.size();i++){
            if(!data.get(i).getReceiverDetails().getId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)))
            {
                if(data.get(i).getRoomVerification().equalsIgnoreCase("2")) {
                    if(data.get(i).getGameCounts()!=null) {
                        if (!data.get(i).getGameCounts().equalsIgnoreCase("") || !data.get(i).getGameCounts().equalsIgnoreCase("0")) {
                            list.add(data.get(i));
                        }
                    }
                }
            }
        }
        if(list.size()==0)
        {
            binding.tvRank.setText(""+1);
        }


        boolean isValueSet=false;
        Collections.sort(list,new CustomSorting());
        int heighiestPoint=0;
        long lowestPoint;
        if(list.size()>0)
        {
            lowestPoint = list.get(list.size()-1).getReceiverDetails().getTotalPoints();


        }else
        {
            lowestPoint = Long.parseLong(binding.tvScore.getText().toString().trim().split("points")[0].trim());

        }
        int rank=0;
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).getReceiverDetails().getTotalPoints()>heighiestPoint)
            {
                heighiestPoint=list.get(i).getReceiverDetails().getTotalPoints();
            }
                if(list.get(i).getReceiverDetails().getTotalPoints()<lowestPoint)
                {
                    lowestPoint=list.get(i).getReceiverDetails().getTotalPoints();
                }
            if(i>0) {
                if (list.get(i).getReceiverDetails().getTotalPoints()!=list.get(i-1).getReceiverDetails().getTotalPoints())
                {
                    rank=rank+1;
                }
            }else
            {
                rank=rank+1;
            }
            list.get(i).getReceiverDetails().setUserValue(""+rank);
            if(list.get(i).getReceiverDetails().getTotalPoints()>totalPoints)
            {
                if(!isValueSet) {
                    binding.tvRank.setText("" + (rank + 1));
                }
            }else if(list.get(i).getReceiverDetails().getTotalPoints()==totalPoints)
            {
                if(!isValueSet) {
                    binding.tvRank.setText("" + rank);
                }
            }else if(list.get(i).getReceiverDetails().getTotalPoints()<totalPoints)
            {
                if(!isValueSet) {
                    binding.tvRank.setText("" + (rank));
                    isValueSet=true;
                }
                list.get(i).getReceiverDetails().setUserValue(""+(rank+1));
            }
        }

        //Log.e("lowest", ""+lowestPoint);

        if(totalPoints>=heighiestPoint)
        {
            Log.e("heighiestPoint", ""+heighiestPoint);
            binding.tvRank.setText(""+1);
        }
//        Log.e("lowestPoint", ""+lowestPoint);

        if(Long.parseLong(binding.tvScore.getText().toString().trim().split("points")[0].trim())==lowestPoint)
        {
            binding.tvRank.setText(""+rank);

        } else if(lowestPoint>Long.parseLong(binding.tvScore.getText().toString().trim().split("points")[0].trim()))
        {
            binding.tvRank.setText(""+(rank+1));
        }

        return list;
    }

    public  class CustomSorting implements Comparator<ResponseBean>
    {

        @Override
        public int compare(ResponseBean o1, ResponseBean o2) {
            if(o1.getReceiverDetails().getTotalPoints()>o2.getReceiverDetails().getTotalPoints()) return -1;
            else if(o1.getReceiverDetails().getTotalPoints()>o2.getReceiverDetails().getTotalPoints()) return +1;
            else return 0;
        }
    }


    private void init() {
        //layout header
        headerText=findViewById(R.id.headerText);
        footerText=findViewById(R.id.footerText);
        closeIv=findViewById(R.id.closeIv);
        qstIv=findViewById(R.id.qstIv);
        headerText.setText(getString(R.string.leaderboard));
        closeIv.setOnClickListener(this);
        qstIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.closeIv:
            onBackPressed();
            break;

            case R.id.qstIv:
            Intent intent=new Intent(this, OtherUserProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            break;

            case R.id.profileCl:
            Intent editIntent=new Intent(this, UserProfileActivity.class);
            editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(editIntent);
            break;

        }
    }
}
