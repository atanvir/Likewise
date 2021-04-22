package com.likewise.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.likewise.BottomSheet.FacebookBottomSheet;
import com.likewise.BottomSheet.LoginFirstBottomSheet;
import com.likewise.BuildConfig;
import com.likewise.Interface.SheetItemClickListner;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SPrefrenceValues;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.FacebookLogin;
import com.likewise.Utility.InstagramLogin;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityNoPatnerFoundBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoPatnerFoundActivity extends AppCompatActivity implements View.OnClickListener, SheetItemClickListner, InstagramLogin.OAuthAuthenticationListener {

    ActivityNoPatnerFoundBinding  binding;
    private static final int FACEBOOK_DATA = 27;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_no_patner_found);
        binding.homeCl.setOnClickListener(this);
        binding.inviteCL.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.inviteCL:
            if(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("1") || SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("4")){
                Intent createIntent = new Intent(this, CreateGameActivity.class);
                createIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(createIntent);
            }else {
                CommonUtils.playSound("press_button",this);
                FacebookBottomSheet bottomSheet = new FacebookBottomSheet(this);
                bottomSheet.show(getSupportFragmentManager(),"");
            }
            break;

            case R.id.homeCl:
            Intent intent=new Intent(this,MainActivity.class);
            finish();
            startActivity(intent);
            break;
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void checkSocalIdApi(final Intent data,String cameFrom) {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.checkSocalId(data.getStringExtra("id"),SharedPreferenceWriter.getInstance(NoPatnerFoundActivity.this).getString(SPreferenceKey.TOKEN),ParamEnum.DeviceType.theValue());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful()) {
                            CommonModelDataObject serverResponse = response.body();

                            if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue())) {

                                if (serverResponse.getMessage().equalsIgnoreCase("User details found")) {
                                    SPrefrenceValues.setPrefrences(NoPatnerFoundActivity.this, serverResponse);
                                    Intent intent = new Intent(NoPatnerFoundActivity.this,NoPatnerFoundActivity.class);
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
                Toast.makeText(NoPatnerFoundActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(NoPatnerFoundActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private List<String> getCountryCodeList() {
        String langCode[]=SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.LANGUAGE_CODE).split(",");
        return new ArrayList<>(Arrays.asList(langCode));
    }



    private void socialLoginApi(Intent data,String cameFrom) {
        if(CommonUtils.networkConnectionCheck(this))
        {
            try {
                ServicesInterface anInterface= ServicesConnection.getInstance().createService(this);
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

                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse = response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                SPrefrenceValues.setPrefrences(NoPatnerFoundActivity.this,serverResponse);
                                Intent intent=new Intent(NoPatnerFoundActivity.this, NoPatnerFoundActivity.class);
                                startActivity(intent);
                                finish();

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                if(serverResponse.getMessage().equalsIgnoreCase(""))

                                    CommonUtils.showSnackBar(NoPatnerFoundActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
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
            CommonUtils.showSnackBar(NoPatnerFoundActivity.this, getString(R.string.no_internet),ParamEnum.Failure.theValue());

        }




    }



    @Override
    public void onBackPressed() {
        Intent intent= new Intent(this,MainActivity.class);
        finish();
        startActivity(intent);
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
        CommonUtils.showSnackBar(this,error+"",ParamEnum.Failure.theValue());

    }
}
