package com.likewise.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.likewise.Fragment.RegisterFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.likewise.Fragment.LoginFragment;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.LoginModel;
import com.likewise.Model.SignupModel;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SPrefrenceValues;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.FacebookLogin;
import com.likewise.Utility.ILoginRegister;
import com.likewise.Utility.InstagramLogin;
import com.likewise.Utility.ParamEnum;
import com.likewise.Utility.SwitchFragment;
import com.likewise.databinding.ActivityRegisterLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRegisterActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener, ILoginRegister, InstagramLogin.OAuthAuthenticationListener {

    ActivityRegisterLoginBinding binding;
    private LoginModel loginModel;
    private SignupModel signupModel = new SignupModel();
    private String errorMsg = "";
    private Dialog dialog;
    private static final int FACEBOOK_DATA = 27;
    //Forgot Password
    private BottomSheetDialog bottomSheetDialog;
    EditText emailEd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_login);
        init();


        if(getIntent().getBooleanExtra("isRegister", false))
        {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));
            RegisterFragment fragment = new RegisterFragment();
            fragment.setListner(LoginRegisterActivity.this);
            loadFragment(fragment);
            binding.forgotPassswordText.setVisibility(View.GONE);
            binding.loginBtn.setText(getString(R.string.register));

        }else {
            LoginFragment fragment = new LoginFragment();
            fragment.setListner(LoginRegisterActivity.this);
            loadFragment(fragment);
        }



    }

    private void init() {
        binding.tabLayout.addOnTabSelectedListener(this);
        binding.loginBtn.setOnClickListener(this);
        binding.forgotPassswordText.setOnClickListener(this);
        binding.guestIv.setOnClickListener(this);
        binding.facebookIv.setOnClickListener(this);
        binding.instragramIv.setOnClickListener(this);
    }

    private void loadFragment(Fragment fragment) {
        SwitchFragment.changeFragment(getSupportFragmentManager(), fragment, false, true);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            LoginFragment fragment = new LoginFragment();
            fragment.setListner(LoginRegisterActivity.this);
            loadFragment(fragment);
            binding.forgotPassswordText.setVisibility(View.VISIBLE);
            binding.loginBtn.setVisibility(View.VISIBLE);
            binding.loginBtn.setText(getString(R.string.login));


        } else {
            RegisterFragment fragment = new RegisterFragment();
            fragment.setListner(LoginRegisterActivity.this);
            loadFragment(fragment);
            binding.forgotPassswordText.setVisibility(View.GONE);
            binding.loginBtn.setText(getString(R.string.register));

        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.forgotPassswordText:
            forgotPasswordBottomSheet();
            break;

            case R.id.loginBtn:
            loginRegister();
            break;

            case R.id.instragramIv:
            InstagramLogin instaObj = new InstagramLogin(this);
            instaObj.setListener(this);
            instaObj.authorize();
            break;

            case R.id.guestIv:
            Intent intent=new Intent(LoginRegisterActivity.this,SelectLanguageActivity.class);
            intent.putExtra(ParamEnum.CameFrom.theValue(),ParamEnum.Guest.theValue());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            break;

            case R.id.facebookIv:
            Intent fbIntent = new Intent(this, FacebookLogin.class);
            startActivityForResult(fbIntent, FACEBOOK_DATA);
            break;


                
            //forgot Password
            case R.id.submitBtn:
            if(checkValidation("forgot")) forgotPasswordApi();
            else Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            break;


    }



}

    private void forgotPasswordApi() {

        if(CommonUtils.networkConnectionCheck(LoginRegisterActivity.this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(LoginRegisterActivity.this);
                Call<CommonModelDataObject> call = anInterface.forgotPassword(emailEd.getText().toString().trim());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, LoginRegisterActivity.this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                if(bottomSheetDialog.isShowing())
                                {
                                    bottomSheetDialog.dismiss();
                                }
                                Toast.makeText(LoginRegisterActivity.this, ""+serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                Toast.makeText(LoginRegisterActivity.this, ""+serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(LoginRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(LoginRegisterActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }



    }

    private void forgotPasswordBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View sheetView = inflater.inflate(R.layout.bottom_sheet_forgot_password, null);
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        Button submitBtn=bottomSheetDialog.findViewById(R.id.submitBtn);
        emailEd=bottomSheetDialog.findViewById(R.id.emailEd);
        submitBtn.setOnClickListener(this);
        bottomSheetDialog.show();
    }

    private void loginRegister() {
        //Register
        if (binding.loginBtn.getText().toString().equalsIgnoreCase(getString(R.string.register))) {
            if (checkValidation("signup")) {
                checkEmailApi();
            } else {
                CommonUtils.showSnackBar( LoginRegisterActivity.this, errorMsg,ParamEnum.Failure.theValue());
            }
        }
        //login
        else {

            if (checkValidation("login")){
                loginApi();

            }
            else
            {
                CommonUtils.showSnackBar( LoginRegisterActivity.this, errorMsg,ParamEnum.Failure.theValue());
            }

        }
    }

    private void loginApi() {
        if(CommonUtils.networkConnectionCheck(LoginRegisterActivity.this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(LoginRegisterActivity.this);
                Call<CommonModelDataObject> call = anInterface.login(loginModel.getEmail(), loginModel.getPassword(),ParamEnum.DeviceType.theValue(),SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.TOKEN));
                ServicesConnection.getInstance().enqueueWithoutRetry(call, LoginRegisterActivity.this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {

                                SPrefrenceValues.setPrefrences(LoginRegisterActivity.this,serverResponse);
                                Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
                                SharedPreferenceWriter.getInstance(LoginRegisterActivity.this).writeBooleanValue(SPreferenceKey.IS_LOGIN, true);
                                finish();
                                startActivity(intent);

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(LoginRegisterActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(LoginRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(LoginRegisterActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private void checkEmailApi() {
        if(CommonUtils.networkConnectionCheck(LoginRegisterActivity.this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(LoginRegisterActivity.this);
                Call<CommonModelDataObject> call = anInterface.checkEmail(signupModel.getEmail(), signupModel.getUsername());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, LoginRegisterActivity.this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                Intent intent = new Intent(LoginRegisterActivity.this,SelectLanguageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(ParamEnum.CameFrom.theValue(),ParamEnum.Register.theValue());
                                intent.putExtra(ParamEnum.Register.theValue(),signupModel);
                                startActivity(intent);

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(LoginRegisterActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(LoginRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(LoginRegisterActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }


    }

    private boolean checkValidation(String type) {
        boolean ret=true;

        if(type.equalsIgnoreCase("login"))
        {
            if(loginModel!=null)
            {
            if(!loginModel.getEmail().equalsIgnoreCase("") || loginModel.getPassword().length()<8)
            {
                if(!loginModel.getEmail().equalsIgnoreCase(""))
                {
                    ret= android.util.Patterns.EMAIL_ADDRESS.matcher(loginModel.getEmail()).matches();
                    if(!loginModel.getEmail().contains("@"))
                    {
                        ret=true;
                    }
                    if(!ret) errorMsg="Please enter valid Email or Username";

                }
                   else if(loginModel.getPassword().length()<8)
                    {
                        ret =false;
                        errorMsg="Please enter password aleast of 8 digits";

                    }



            } else if (loginModel.getEmail().length() == 0 || loginModel.getPassword().length()==0)
            {
                if(loginModel.getEmail().length() == 0)
                {
                    ret=false;
                    errorMsg="Please enter email";
                }else if(loginModel.getPassword().length()==0)
                {
                    ret=false;
                    errorMsg="Please enter password";
                }
            }

            }else
            {
                ret=false;
                errorMsg="Please enter Email or Username";
            }



        }
        else if(type.equalsIgnoreCase("signup"))
        {
            if(signupModel!=null)
            {
                if(signupModel.getName()==null ||
                   signupModel.getEmail()==null ||
                   signupModel.getUsername()==null||
                   signupModel.getPassword()==null)
                {
                    if(signupModel.getName()==null)
                    {
                        ret=false;
                        errorMsg="Please enter Full Name";
                    }else if(signupModel.getEmail()==null)
                    {
                        ret=false;
                        errorMsg="Please enter Email Id";
                    }else if(signupModel.getUsername()==null)
                    {
                        ret=false;
                        errorMsg="Please enter User Name";
                    }
                    else if(signupModel.getPassword()==null)
                    {
                        ret=false;
                        errorMsg="Please enter Password";
                    }


                }else if(!signupModel.getEmail().equalsIgnoreCase(""))
                    {
                        ret= android.util.Patterns.EMAIL_ADDRESS.matcher(signupModel.getEmail()).matches();
                        if(!ret) errorMsg="Please enter valid Email ID";
                    }




            }else
            {
                ret=false;
                errorMsg="Please enter Full Name";
            }


        }
        else if(type.equalsIgnoreCase("forgot"))
        {
            if(emailEd.getText().toString().length()>0)
            {
                ret = android.util.Patterns.EMAIL_ADDRESS.matcher(emailEd.getText().toString().trim()).matches();
                if(!ret)
                {
                    errorMsg="Please enter valid Email Id";
                }
            }else
            {
                ret=false;
                errorMsg="Please enter Email Id";
            }

        }


        return ret;
    }

    @Override
    public void Data(Object object) {
        if(object instanceof LoginModel) loginModel=  new LoginModel(((LoginModel) object).getEmail(),((LoginModel) object).getPassword());
        else if(object instanceof SignupModel) signupModel=new SignupModel(((SignupModel) object).getName(),((SignupModel) object).getEmail(),((SignupModel) object).getUsername(),((SignupModel) object).getPassword());
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
        if(CommonUtils.networkConnectionCheck(LoginRegisterActivity.this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(LoginRegisterActivity.this);
                Call<CommonModelDataObject> call = anInterface.checkSocalId(data.getStringExtra("id"),SharedPreferenceWriter.getInstance(LoginRegisterActivity.this).getString(SPreferenceKey.TOKEN),ParamEnum.DeviceType.theValue());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, LoginRegisterActivity.this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                if(serverResponse.getMessage().equalsIgnoreCase("User details found")) {
                                    SPrefrenceValues.setPrefrences(LoginRegisterActivity.this, serverResponse);
                                    Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if(serverResponse.getMessage().equalsIgnoreCase("User does not exist"))
                                {
                                    CommonUtils.showSnackBar(LoginRegisterActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                                    Intent langInt = new Intent(LoginRegisterActivity.this,SelectLanguageActivity.class);
                                    langInt.putExtra(ParamEnum.CameFrom.theValue(),cameFrom);
                                    langInt.putExtra("id", data.getStringExtra("id"));
                                    langInt.putExtra("email", data.getStringExtra("email"));
                                    langInt.putExtra("name", data.getStringExtra("name"));
                                    langInt.putExtra("image", data.getStringExtra("image"));
                                    langInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                Toast.makeText(LoginRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(LoginRegisterActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }


    }


    @Override
    public void onSuccess(String userName,String socialId,String fullName,String profilePic) {
        Intent data=new Intent();
        data.putExtra("id",socialId);
        Log.e("email",userName);
        data.putExtra("email", userName);
        data.putExtra("name", fullName);
        data.putExtra("image", profilePic);
        checkSocalIdApi(data,ParamEnum.Instagram.theValue());
    }

    @Override
    public void onFail(String error) {

    }
}
