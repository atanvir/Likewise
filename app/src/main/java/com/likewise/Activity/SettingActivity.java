package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.likewise.Model.CommonModelDataObject;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SPrefrenceValues;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivitySettingBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_setting);
        init();

    }

    private void notificationUpdateApi(boolean notification) {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.notificationUpdate(notification);
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                SharedPreferenceWriter.getInstance(SettingActivity.this).writeStringValue(SPreferenceKey.NOTIFICATION, String.valueOf(notification));

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(SettingActivity.this,serverResponse.getMessage(),serverResponse.getStatus());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(SettingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(SettingActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    private void init() {
        binding.closeIv.setOnClickListener(this);
        binding.privacyPolicyText.setOnClickListener(this);
        binding.logoutText.setOnClickListener(this);
        binding.deleteText.setOnClickListener(this);
        binding.notiBtn.setOnCheckedChangeListener(this);
        binding.tgVibrate.setOnCheckedChangeListener(this);
        binding.tgSound.setOnCheckedChangeListener(this);
        binding.tgSound.setChecked(SharedPreferenceWriter.getInstance(this).getBoolean(SPreferenceKey.SOUND));
        binding.tgVibrate.setChecked(SharedPreferenceWriter.getInstance(this).getBoolean(SPreferenceKey.VIBRATE));

        setNotificationIcon(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.NOTIFICATION));

    }

    private void setNotificationIcon(String notification) {
        switch (notification)
        {
            case "true": binding.notiBtn.setChecked(true); break;
            case "false" : binding.notiBtn.setChecked(false); break;
            default: binding.notiBtn.setChecked(true); break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.closeIv:
            onBackPressed();
            break;

            case R.id.privacyPolicyText:
            Intent intent=new Intent(this,PrivacyPoliciesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            break;

            case R.id.logoutText:
            showAlearDailog("Logout","Do You really want to Log out?");
            break;

            case R.id.deleteText:
            showAlearDailog("Alert!",this.getString(R.string.delete_ac));
            break;

        }
    }

    private void showAlearDailog(String title, String desc) {


        final Dialog dialog=new Dialog(this,android.R.style.Theme_Black);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_common_aleart);
        TextView actionText=dialog.findViewById(R.id.actionText);
        TextView cancelText=dialog.findViewById(R.id.cancelText);
        TextView descText=dialog.findViewById(R.id.descText);
        TextView titleText=dialog.findViewById(R.id.titleText);
        titleText.setText(title);
        descText.setText(desc);
        if(title.equalsIgnoreCase("Logout"))
        {
            actionText.setText(title);

        }else if(title.equalsIgnoreCase("Alert!"))
        {
            actionText.setText("Deactivate");
        }



        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        actionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (actionText.getText().toString().equalsIgnoreCase("Logout")) {
                    logoutApi();
                } else if (actionText.getText().toString().equalsIgnoreCase("Deactivate")) {
                    deleteAccount();
                }
            }

        });


        dialog.show();



    }

    private void deleteAccount() {

        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.deleteUserAcount();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                Toast.makeText(SettingActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                SPrefrenceValues.removePrefrences(SettingActivity.this);
                                Intent intent = new Intent(SettingActivity.this,SocialRegisterActivity.class);
                                startActivity(intent);
                                finish();
                                CommonUtils.getDeviceToken(SettingActivity.this);

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(SettingActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(SettingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(SettingActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private void logoutApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.logout();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                Toast.makeText(SettingActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                SPrefrenceValues.removePrefrences(SettingActivity.this);
                                Intent intent = new Intent(SettingActivity.this,SocialRegisterActivity.class);
                                startActivity(intent);
                                finish();
                                CommonUtils.getDeviceToken(SettingActivity.this);

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(SettingActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(SettingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(SettingActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId())
        {
            case R.id.tgSound:
            SharedPreferenceWriter.getInstance(this).writeBooleanValue(SPreferenceKey.SOUND,b);
            break;

            case R.id.tgVibrate:
            SharedPreferenceWriter.getInstance(this).writeBooleanValue(SPreferenceKey.VIBRATE,b);
            break;

            case R.id.notiBtn:
            notificationUpdateApi(b);
            break;
        }


    }
}
