package com.likewise.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
import com.likewise.databinding.ActivityEmailRegisterBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialRegisterActivity extends AppCompatActivity implements View.OnClickListener, InstagramLogin.OAuthAuthenticationListener {

    private ActivityEmailRegisterBinding binding;
    private static final int FACEBOOK_DATA = 27;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_email_register);
        binding.emailCl.setOnClickListener(this);
        binding.fbCl.setOnClickListener(this);
        binding.guestCl.setOnClickListener(this);
        binding.instagramCl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
           case R.id.emailCl:
           intent("Email");
           break;

           case R.id.fbCl:
           Intent intent = new Intent(this, FacebookLogin.class);
           startActivityForResult(intent, FACEBOOK_DATA);
           break;

           case R.id.guestCl:
           intent("Guest");
           break;

           case R.id.instagramCl:
           InstagramLogin instaObj = new InstagramLogin(this);
           instaObj.setListener(this);
           instaObj.authorize();
           break;
        }
    }

    private void intent(String comeFrom) {
        Intent intent=null;

        if(comeFrom.equalsIgnoreCase("Email"))
        {
            intent=new Intent(SocialRegisterActivity.this,LoginRegisterActivity.class);
        }
        else
        {
            intent=new Intent(SocialRegisterActivity.this,SelectLanguageActivity.class);
            intent.putExtra(ParamEnum.CameFrom.theValue(),ParamEnum.Guest.theValue());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
    public void onBackPressed() {
        finishAffinity();
    }

    private void checkSocalIdApi(final Intent data,String cameFrom) {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.checkSocalId(data.getStringExtra("id"), SharedPreferenceWriter.getInstance(SocialRegisterActivity.this).getString(SPreferenceKey.TOKEN),ParamEnum.DeviceType.theValue());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful()) {
                            CommonModelDataObject serverResponse = response.body();

                            if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue())) {
                                if (serverResponse.getMessage().equalsIgnoreCase("User details found")) {
                                    SPrefrenceValues.setPrefrences(SocialRegisterActivity.this, serverResponse);
                                    Intent intent = new Intent(SocialRegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (serverResponse.getMessage().equalsIgnoreCase("User does not exist")) {
                                    Intent langInt = new Intent(SocialRegisterActivity.this, SelectLanguageActivity.class);
                                    langInt.putExtra(ParamEnum.CameFrom.theValue(), cameFrom);
                                    langInt.putExtra("id", data.getStringExtra("id"));
                                    langInt.putExtra("email", data.getStringExtra("email"));
                                    langInt.putExtra("name", data.getStringExtra("name"));
                                    langInt.putExtra("image", data.getStringExtra("image"));
                                    langInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(langInt);
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
                Toast.makeText(SocialRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(SocialRegisterActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
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
