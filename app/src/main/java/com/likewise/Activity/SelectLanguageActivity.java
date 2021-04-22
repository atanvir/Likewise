package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.likewise.Adapter.GameFragmentPagerAdapter;
import com.likewise.Adapter.OthersCountryAdapter;
import com.likewise.Adapter.PopularCountryAdapter;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.ResponseBean;
import com.likewise.Model.SignupModel;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SPrefrenceValues;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;
import com.likewise.Utility.SelectCountry;
import com.likewise.databinding.ActivitySelectLanguageBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hari.bounceview.BounceView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectLanguageActivity extends AppCompatActivity implements View.OnClickListener,TabLayout.OnTabSelectedListener {
    private ActivitySelectLanguageBinding binding;
    private String cameFrom="";
    private List<String> countryCode = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_select_language);
        BounceView.addAnimTo(binding.okBtn);
        BounceView.addAnimTo(binding.backIv);
        binding.okBtn.setOnClickListener(this);
        binding.backIv.setOnClickListener(this);
        binding.tabLayout.addOnTabSelectedListener(this);

        GameFragmentPagerAdapter adapter =new GameFragmentPagerAdapter(getSupportFragmentManager(),"Language",null);
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        getIntents();
    }



    private void getIntents() {
        cameFrom = getIntent().getStringExtra(ParamEnum.CameFrom.theValue());
    }



    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.okBtn:
                if(SelectCountry.list!=null) {
                    if (SelectCountry.list.size() > 0) {
                        if (cameFrom.equalsIgnoreCase(ParamEnum.Register.theValue())) signupApi();
                        else if (cameFrom.equalsIgnoreCase(ParamEnum.Facebook.theValue()) || cameFrom.equalsIgnoreCase(ParamEnum.Instagram.theValue()))
                            socialLoginApi(cameFrom);
                        else if (cameFrom.equalsIgnoreCase(ParamEnum.Guest.theValue()))
                            guestLogin();
                    } else {
                        CommonUtils.showSnackBar(this, getString(R.string.at_least_one_language), ParamEnum.Failure.theValue());
                    }
                }
                else {
                    CommonUtils.showSnackBar(this, getString(R.string.at_least_one_language), ParamEnum.Failure.theValue());
                }



                break;

            case R.id.backIv:
                onBackPressed();
                break;

        }
    }

    private void guestLogin() {
        if(CommonUtils.networkConnectionCheck(SelectLanguageActivity.this))
        {
            SignupModel model = new SignupModel();
            model.setDeviceType(ParamEnum.DeviceType.theValue());
            model.setDeviceToken(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.TOKEN));
            model.setLanguageCode(getCountryCodeList());
            model.setType(CommonUtils.getUserType(ParamEnum.Guest.theValue()));

            try {
                ServicesInterface anInterface=ServicesConnection.getInstance().createService(SelectLanguageActivity.this);
                Call<CommonModelDataObject> call=anInterface.guestLogin(model);
                ServicesConnection.getInstance().enqueueWithoutRetry(call, SelectLanguageActivity.this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse = response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                SelectCountry.removeAll(SelectCountry.list);
                                SPrefrenceValues.setPrefrences(SelectLanguageActivity.this,serverResponse);
                                intent();

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                if(serverResponse.getMessage().equalsIgnoreCase(""))

                                    CommonUtils.showSnackBar(SelectLanguageActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
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
            CommonUtils.showSnackBar(SelectLanguageActivity.this, getString(R.string.no_internet),ParamEnum.Failure.theValue());

        }


    }

    private void intent() {
        Intent intent=new Intent(SelectLanguageActivity.this,MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void socialLoginApi(String cameFrom) {
        if(CommonUtils.networkConnectionCheck(SelectLanguageActivity.this))
        {
            try {
                ServicesInterface anInterface=ServicesConnection.getInstance().createService(SelectLanguageActivity.this);
                Call<CommonModelDataObject> call=anInterface.socialLogin(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID),
                                                                getIntent().getStringExtra("id"),
                                                               cameFrom,
                                                               SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.TOKEN),
                                                               ParamEnum.DeviceType.theValue(),
                                                               getIntent().getStringExtra("name"),
                                                               getIntent().getStringExtra("image"),
                                                               getIntent().getStringExtra("email"),
                                                                "",getCountryCodeList().toArray(),
                                                               CommonUtils.getUserType(cameFrom),"2");

               ServicesConnection.getInstance().enqueueWithoutRetry(call, SelectLanguageActivity.this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse = response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                SelectCountry.removeAll(SelectCountry.list);
                                SPrefrenceValues.setPrefrences(SelectLanguageActivity.this,serverResponse);
                                intent();

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                if(serverResponse.getMessage().equalsIgnoreCase(""))

                                CommonUtils.showSnackBar(SelectLanguageActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
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
            CommonUtils.showSnackBar(SelectLanguageActivity.this, getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    private List<String> getCountryCodeList() {
        countryCode.clear();
        for(int i=0;i<SelectCountry.list.size();i++)
        {
            countryCode.add(SelectCountry.list.get(i).getCode());
        }
        return countryCode;
    }

    private void signupApi() {

        if(CommonUtils.networkConnectionCheck(SelectLanguageActivity.this))
        {
            try {
                ServicesInterface anInterface=ServicesConnection.getInstance().createService(SelectLanguageActivity.this);
                SignupModel model=getIntent().getParcelableExtra(ParamEnum.Register.theValue());
                model.setLanguageCode(getCountryCodeList());
                model.setDeviceToken(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.TOKEN));
                model.setDeviceType(ParamEnum.DeviceType.theValue());
                model.setType(CommonUtils.getUserType(ParamEnum.Register.theValue()));

                Call<CommonModelDataObject> call=anInterface.signup(model);
                ServicesConnection.getInstance().enqueueWithoutRetry(call, SelectLanguageActivity.this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse = response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                SelectCountry.removeAll(SelectCountry.list);
                                SPrefrenceValues.setPrefrences(SelectLanguageActivity.this,serverResponse);
                                intent();


                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(SelectLanguageActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
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
            CommonUtils.showSnackBar(SelectLanguageActivity.this, getString(R.string.no_internet),ParamEnum.Failure.theValue());

        }

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

//        if(tab.getPosition()==0) binding.tabLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_selected_left_purple));
//        else binding.tabLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_selected_right_purple));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
