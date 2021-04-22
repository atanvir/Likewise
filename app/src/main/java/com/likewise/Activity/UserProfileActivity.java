package com.likewise.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Adapter.SelectLanguageAdapter;
import com.likewise.Adapter.UserProfileLanguageAdapter;
import com.likewise.BottomSheet.AddLangaugeBottomSheet;
import com.likewise.BottomSheet.CountryBottomSheet;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SPrefrenceValues;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.AddRequestBody;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.FilePath;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityEditProfileBinding;
import com.likewise.databinding.PopUpAddInterestBinding;
import com.likewise.databinding.PopUpGenderBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, RadioGroup.OnCheckedChangeListener, CountryBottomSheet.OnNationalityClick, AddLangaugeBottomSheet.OnLangaugeSelected, UserProfileLanguageAdapter.OnKnownLanguageClick {

    //layout below about me
    RecyclerView languageRV;
    ImageView ivAddLanguage;
    private List<String> langCodes;

    //Interest
    private LinearLayout LLInterest;
    private EditText edOneInterest,edTwoInterest,edThreeInterest;

    //Residence
    private LinearLayout LLResidence;
    private EditText countryEd,stateEd,cityEd;

    //Nationality
    private ImageView ivNationality;
    private TextView tvNationality;
    private LinearLayout LLNationality;


    //Main Layout
    ActivityEditProfileBinding binding;

    private File imgFile=null;
    String errorMsg="";

    PopUpGenderBinding popUpGenderBinding;
    PopUpAddInterestBinding popUpAddInterestBinding;

    private String gender="";
    List<CommonModelDataObject.CountryList> countryLists= new ArrayList<>();
    private String nationality;
    private boolean adapterSet=false;
    private List<String> langaueCodeList = new ArrayList<>();
    private String defaultLangCode="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_edit_profile);
        init();
        getUserDetailApi();
    }

    private void getUserDetailApi()  {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.getUserDetail();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                setUserProfile(response.body());

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(UserProfileActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(UserProfileActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }


    }

    private void setUserProfile(CommonModelDataObject serverResponse) {

        binding.tvRank.setText(""+serverResponse.getData().getUserValue());
        binding.tvActivity.setText(""+serverResponse.getWordCount()+" words");
        binding.tvAccuracy.setText(""+serverResponse.getAccuracy()+"%");
        binding.mode.setImageResource(getModeType(serverResponse.getMode()));
        binding.tvPlayed.setText(""+serverResponse.getPlayed());
        binding.tvCreated.setText(""+serverResponse.getCreated());
        binding.tvPatners.setText(""+serverResponse.getPatners());
        ImageGlider.setRoundImage(this,binding.ivprofile,binding.progressBar,serverResponse.getData().getProfilePic());
        // Personal Information
        binding.userFullNameText.setText(serverResponse.getData().getName());
        binding.nameEd.setText(serverResponse.getData().getName());
        binding.unameEd.setText(serverResponse.getData().getUsername());
        binding.gmailEd.setText(serverResponse.getData().getEmail());
        binding.passEd.setText(serverResponse.getData().getPassword());
        binding.genderEd.setText(serverResponse.getData().getGender());
        binding.dobEd.setText(CommonUtils.changeFormatDate(serverResponse.getData().getDob()));
        binding.dobEd.setTag(serverResponse.getData().getDob());
        binding.aboutEd.setText(serverResponse.getData().getAboutus());
        binding.coinsText.setText(""+serverResponse.getData().getCoins());
        binding.tvtotalPoints.setText(""+serverResponse.getData().getTotalPoints());
        if(serverResponse.getData().getType().equalsIgnoreCase("1"))
        {
            binding.abtHeadText.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.fb_home),null);
        }

        tvNationality.setText(serverResponse.getData().getNationalit());
        ImageGlider.setRoundImage(this,ivNationality,null,getCountryImage(serverResponse.getData().getNationalit(),serverResponse.getCountryList()));
        countryLists=serverResponse.getCountryList();
        nationality=serverResponse.getData().getNationalit();
        langaueCodeList=serverResponse.getData().getLanguageCode();
        defaultLangCode=serverResponse.getData().getDefaultLangCode();
        SharedPreferenceWriter.getInstance(this).writeStringValue(SPreferenceKey.LANGUAGE_CODE,TextUtils.join(",",langaueCodeList));
        LinearLayoutManager manager =new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        languageRV.setLayoutManager(manager);
        languageRV.setAdapter(new UserProfileLanguageAdapter(this,getUserLangCodes(countryLists,defaultLangCode),binding.tvedit.getText().toString().trim(),this));
        tvNationality.setText(serverResponse.getData().getNationalit());
        countryEd.setText(serverResponse.getData().getCountry());
        stateEd.setText(serverResponse.getData().getState());
        cityEd.setText(serverResponse.getData().getCity());
        if(serverResponse.getData().getInterest().contains(","))
        {
           String  interest[]=serverResponse.getData().getInterest().split(",");
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
       int imageId = 0;
       if(mode!=null) {
           switch (mode) {

               case 1:
                   imageId = R.drawable.timer_new;
                   break;

               case 2:
                   imageId = R.drawable.life_un;
                   break;

               default:
                   imageId = R.drawable.life_un;
                   break;
           }
       }else
       {
           imageId = R.drawable.life_un;
       }

           return imageId;

    }

    private List<CommonModelDataObject.CountryList> getUserLangCodes(List<CommonModelDataObject.CountryList> countryList,String defaultLangCode) {
        langCodes=new ArrayList<>(Arrays.asList(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.LANGUAGE_CODE).split(",")));
        List<CommonModelDataObject.CountryList> lists=new ArrayList<>();
        for(int i=0 ;i<countryList.size();i++)
        {
            for(int j=0;j<langCodes.size();j++)
            {
                if(!langCodes.get(j).equalsIgnoreCase("")) {
                    if (countryList.get(i).getCode().equalsIgnoreCase(langCodes.get(j))) {
                        Log.e("langCode", langCodes.get(j));
                        Log.e("countryCode", countryList.get(i).getCode());
                        countryList.get(i).setChecked(false);
                        lists.add(countryList.get(i));
                    }
                }
            }

        }

        for(int i=0;i<lists.size();i++)
        {
            if(lists.get(i).getCode().equalsIgnoreCase(defaultLangCode))
            {
                lists.get(i).setChecked(true);
                break;
            }
        }
        return lists;
    }

    private void init() {
        //layout below about me
        //layout below about me
        languageRV=findViewById(R.id.languageRV);
        ivAddLanguage=findViewById(R.id.ivAddLanguage);
        ivAddLanguage.setOnClickListener(this);
        //Nationality
        ivNationality=findViewById(R.id.ivNationality);
        tvNationality=findViewById(R.id.tvNationality);
        LLNationality=findViewById(R.id.LLNationality);
        LLNationality.setOnClickListener(this);
        tvNationality.setOnClickListener(this);
        ivNationality.setOnClickListener(this);

        //Residence
        countryEd=findViewById(R.id.countryEd);
        stateEd=findViewById(R.id.stateEd);
        cityEd=findViewById(R.id.cityEd);
        LLResidence=findViewById(R.id.LLResidence);
        LLResidence.setOnClickListener(this);
        countryEd.setOnClickListener(this);
        stateEd.setOnClickListener(this);
        cityEd.setOnClickListener(this);

        //Interests
        edOneInterest=findViewById(R.id.edOneInterest);
        edTwoInterest=findViewById(R.id.edTwoInterest);
        edThreeInterest=findViewById(R.id.edThreeInterest);
        LLInterest=findViewById(R.id.LLInterest);
        LLInterest.setOnClickListener(this);
        edOneInterest.setOnClickListener(this);
        edTwoInterest.setOnClickListener(this);
        edThreeInterest.setOnClickListener(this);

        //main layout
        binding.tvtotalPoints.setOnClickListener(this);
        binding.closeIv.setOnClickListener(this);
        binding.coinsText.setOnClickListener(this);
        binding.tvedit.setOnClickListener(this);
        binding.ivprofile.setOnClickListener(this);
        binding.mainCl.setOnClickListener(this);
        binding.dobEd.setOnClickListener(this);
        binding.genderEd.setOnClickListener(this);
        binding.unameEd.setOnClickListener(this);
        binding.gmailEd.setOnClickListener(this);
        binding.aboutEd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unameEd:
            checkText();
            break;

            case R.id.gmailEd:
            checkText();
            break;

            case R.id.aboutEd:
            checkText();
            break;

            case R.id.closeIv:
            onBackPressed();
            break;

            case R.id.stateEd:
            checkText();
            break;

            case R.id.countryEd:
            checkText();
            break;

            case R.id.cityEd:
            checkText();
            break;

            case R.id.coinsText:
            Intent coinIntent = new Intent(this, GetCoinsActivity.class);
            coinIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(coinIntent);
            break;

            case R.id.tvtotalPoints:
            Intent pointIntent = new Intent(this, LeaderboardActivity.class);
            pointIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(pointIntent);
            break;

            case R.id.tvNationality:
            checkText();
            break;

            case R.id.tvedit:
            if(binding.tvedit.getText().toString().equalsIgnoreCase("Edit"))
            {
            binding.tvedit.setText("Save");
            setFocusablity(true);
            LinearLayoutManager manager =new LinearLayoutManager(this);
            manager.setOrientation(RecyclerView.HORIZONTAL);
            languageRV.setLayoutManager(manager);
            languageRV.setAdapter(new UserProfileLanguageAdapter(this,getUserLangCodes(countryLists,defaultLangCode),binding.tvedit.getText().toString().trim(),this));
            }
            else {
                binding.tvedit.setText("Edit");
                setFocusablity(false);
                if(checkValidation()) updateUserProfileApi();
                else CommonUtils.showSnackBar(UserProfileActivity.this,errorMsg,ParamEnum.Failure.theValue());
            }
            break;

            case R.id.ivprofile:
            if(!checkText()) CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
            break;

            case R.id.dobEd:
            if(!checkText())
            {
                Date date = Calendar.getInstance().getTime();
                DatePickerDialog dialog = new DatePickerDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT, this,
                        Integer.parseInt(new SimpleDateFormat("yyyy").format(date)),
                        Integer.parseInt(new SimpleDateFormat("MM").format(date)) - 1,
                        Integer.parseInt(new SimpleDateFormat("dd").format(date)));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
            break;

            case R.id.genderEd:
            if(!checkText()) genderPopup();
            break;

            case R.id.LLInterest:
            checkText();
            break;

           case R.id.LLNationality:
           checkText();
           break;

           case R.id.LLResidence:
           checkText();
            break;

            case R.id.ivNationality:
            if(!checkText()){
            CommonUtils.playSound("press_button",UserProfileActivity.this);
            CountryBottomSheet sheet = new CountryBottomSheet(this);
            sheet.show(getSupportFragmentManager(),"");}
            break;

            case R.id.ivAddLanguage:
            if(!checkText()) {
                CommonUtils.playSound("press_button",UserProfileActivity.this);
                AddLangaugeBottomSheet bottomSheet = new AddLangaugeBottomSheet(this);
                bottomSheet.show(getSupportFragmentManager(),"");
            }
            break;

            case R.id.edOneInterest:
            checkText();
            break;


            case R.id.edTwoInterest:
            checkText();
            break;

            case R.id.edThreeInterest:
            checkText();
            break;

        }
    }


    private boolean  checkText() {
        boolean ret=false;
        if (binding.tvedit.getText().toString().equalsIgnoreCase("Edit"))
        {
            ret=true;
            CommonUtils.showSnackBar(this,getString(R.string.enable_editing),ParamEnum.Failure.theValue());
        }
        return ret;
    }

    private void genderPopup() {
        Dialog dialog=new Dialog(this,android.R.style.Theme_Black);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUpGenderBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.pop_up_gender, null, false);
        dialog.setContentView(popUpGenderBinding.getRoot());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        popUpGenderBinding.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        popUpGenderBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        popUpGenderBinding.genderRG.setOnCheckedChangeListener(this);
        dialog.show();
    }

    private boolean checkValidation() {
        boolean ret=true;
        if(binding.gmailEd.getText().toString().trim().length()>0) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.gmailEd.getText().toString()).matches()) {
                ret = false;
                errorMsg = "Please enter valid Email Address";
            }
        }
        return ret;



    }

    private void updateUserProfileApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ResponseBean bean = new ResponseBean();
                bean.setName(binding.nameEd.getText().toString().trim());
                bean.setUsername(binding.unameEd.getText().toString().trim());
                bean.setEmail(binding.gmailEd.getText().toString().trim());
                bean.setGender(binding.genderEd.getText().toString().trim());
                bean.setDob(""+binding.dobEd.getTag());
                bean.setAboutus(binding.aboutEd.getText().toString().trim());
                bean.setNationalit(tvNationality.getText().toString());
                bean.setCountry(countryEd.getText().toString().trim());
                bean.setState(stateEd.getText().toString().trim());
                bean.setCity(cityEd.getText().toString().trim());
                bean.setInterest(edOneInterest.getText().toString()+","+edTwoInterest.getText().toString()+","+edThreeInterest.getText().toString());
                bean.setLanguageCode(Arrays.asList(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.LANGUAGE_CODE).split(",")));
                bean.setDefaultLangCode(defaultLangCode);

                AddRequestBody body = new AddRequestBody(bean);

                //ProfilePic
                RequestBody requestBody=null;
                MultipartBody.Part part = null;

                if(imgFile!=null)
                {
                    requestBody=RequestBody.create(MediaType.parse("image/*"),imgFile);
                    part=MultipartBody.Part.createFormData("profilePic",imgFile.getName(),requestBody);
                }

                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.updateUserProfile(part,body.getBody());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                SPrefrenceValues.setPrefrences(UserProfileActivity.this,serverResponse);
                                Toast.makeText(UserProfileActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(UserProfileActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(UserProfileActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }


    private void setFocusablity(boolean isFocusable) {
        if(isFocusable)
        {
            binding.ivprofile.setFocusableInTouchMode(true);
            binding.nameEd.setFocusableInTouchMode(true);
            binding.unameEd.setFocusableInTouchMode(true);
            binding.gmailEd.setFocusableInTouchMode(true);
            binding.passEd.setFocusableInTouchMode(true);
            edOneInterest.setFocusableInTouchMode(true);
            edTwoInterest.setFocusableInTouchMode(true);
            edThreeInterest.setFocusableInTouchMode(true);
            binding.aboutEd.setFocusableInTouchMode(true);
            languageRV.setFocusableInTouchMode(true);

            countryEd.setFocusableInTouchMode(true);
            stateEd.setFocusableInTouchMode(true);
            cityEd.setFocusableInTouchMode(true);
        }
        else
        {
            binding.nameEd.setFocusable(false);
            binding.unameEd.setFocusable(false);
            binding.gmailEd.setFocusable(false);
            binding.passEd.setFocusable(false);
            binding.aboutEd.setFocusable(false);
            languageRV.setFocusable(false);
            edOneInterest.setFocusable(false);
            edTwoInterest.setFocusable(false);
            edThreeInterest.setFocusable(false);
            countryEd.setFocusable(false);
            stateEd.setFocusable(false);
            cityEd.setFocusable(false);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                String path= FilePath.getPath(UserProfileActivity.this,result.getUri());
                try {
                    imgFile= new Compressor(this).compressToFile(new File(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ImageGlider.setRoundImage(this,binding.ivprofile,binding.progressBar, String.valueOf(Uri.fromFile(imgFile)));

//                binding.ivprofile.setImageURI(Uri.fromFile(imgFile));

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Date date= null;
        try {
            date = simpleDateFormat.parse(dayOfMonth+"-"+(month+1)+"-"+year);

            StringBuilder builder=new StringBuilder();
            builder.append(simpleDateFormat.format(date));
            binding.dobEd.setTag(builder);
            binding.dobEd.setText(new SimpleDateFormat("dd MMMM yyyy").format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        switch (radioGroup.getCheckedRadioButtonId())
        {
            case R.id.maleRB: binding.genderEd.setText("Male"); break;
            case R.id.femaleRb: binding.genderEd.setText("Female"); break;
            case R.id.otherRB: binding.genderEd.setText("Other"); break;

        }

    }

    @Override
    public void onNationality(String countryImage, String name) {
        ImageGlider.setRoundImage(this,ivNationality,null,countryImage);
        tvNationality.setText(name);
        nationality=name;
//        LinearLayoutManager manager =new LinearLayoutManager(this);
//        manager.setOrientation(RecyclerView.HORIZONTAL);
//        languageRV.setLayoutManager(manager);
//        languageRV.setAdapter(new UserProfileLanguageAdapter(this,getUserLangCodes(countryLists,nationality),null));

    }

    @Override
    public void onLanguageAdded() {
        LinearLayoutManager manager =new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        languageRV.setLayoutManager(manager);
        languageRV.setAdapter(new UserProfileLanguageAdapter(this,getUserLangCodes(countryLists,defaultLangCode),binding.tvedit.getText().toString().trim(),this));
    }

    @Override
    public void onDefaultLange(String code) {
        this.defaultLangCode=code;
        LinearLayoutManager manager =new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        languageRV.setLayoutManager(manager);
        languageRV.setAdapter(new UserProfileLanguageAdapter(this,getUserLangCodes(countryLists,defaultLangCode),binding.tvedit.getText().toString().trim(),this));
    }
}
