package com.likewise.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.likewise.Adapter.AddImagesAdapter;
import com.likewise.Adapter.FacebookFriendsAdapter;
import com.likewise.Adapter.GameObjectiveAdapter;
import com.likewise.Adapter.MorePrecieslyAdapter;
import com.likewise.Adapter.PublicGalleryAdapter;
import com.likewise.Adapter.SelectLanguageAdapter;
import com.likewise.BottomSheet.AddImagesBottomSheet;
import com.likewise.Model.CameraModel;
import com.likewise.Model.CardImageModel;
import com.likewise.Model.CoinPay;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.CreateGameModel;
import com.likewise.Model.CreateGameResponse;
import com.likewise.Model.FbFriend;
import com.likewise.Model.GalleryModel;
import com.likewise.Model.Laguage;
import com.likewise.Model.MessageResponse;
import com.likewise.Model.Morerecisely;
import com.likewise.Model.Objective;
import com.likewise.Model.ResponseBean;
import com.likewise.Interface.OnItemClickListner;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.AddRequestBody;
import com.likewise.Utility.AllImagesSingleton;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.CustomTextWatcher;
import com.likewise.Utility.FilePath;
import com.likewise.Utility.ParamEnum;
import com.likewise.Utility.ServerTimeCalculator;
import com.likewise.databinding.ActivityCreateAGameBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import hari.bounceview.BounceView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGameActivity extends AppCompatActivity implements View.OnClickListener, CustomTextWatcher.TextWatcherWithInstance, OnItemClickListner, View.OnFocusChangeListener, AddImagesAdapter.onImageDeleteListner, AddImagesBottomSheet.bottomSheetListner, View.OnKeyListener {

    ActivityCreateAGameBinding binding;

    //layout objective
    RecyclerView morePreciosulyRv,objectRv;
    EditText customInstEd;
    TextView countText,preciouslyText;
    private boolean isFromInviteButton;

    //layout Bottom
    //No Image
    LinearLayout noImageLL;TextView tvNoImage;

    //Random Images
    LinearLayout randomImagesLL;TextView tvRandom;ImageView ivRandom;

    // Add Image
    RecyclerView addImgRV;LinearLayout addImageLL;TextView tvAddImage;ImageView ivAddImage;
    private AddImagesAdapter adapter;

    //layout header
    TextView headerText;
    ImageView closeIv;

    //layout select mode
    ImageView liveIv,timeIv;
    FacebookFriendsAdapter facebookFriendsAdapter;

    private ResponseBean bean;
    private List<ResponseBean> searchDataList;
    private List<ResponseBean> fbFriendList;

    private String mode="",langCode="",morePreciouslyId="",objectiveId="",socialId="";
    private String fbUserName="",fbProfilePic="";
    private Integer referFriendCoin;
    boolean isFromAddImages=false;

    private File defaultImagePath=null;
    private List<String> allRandomImages;
    private List<String> randomImages;
    private String choosenImageOptions="Random Images";
    private boolean isFromFacebook;
    private boolean isFromSearch;
    private boolean isUserKnow;
    private String languageName="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_create_a_game);
        init();
        settingBackground("Live");
        mode="2";
        binding.ivSearch.setOnClickListener(this);
        BounceView.addAnimTo(binding.ivSearch);
        AllImagesSingleton.removeAll();
        CommonUtils.showLoadingDialog(this);
        publicGalleryApi();
    }

    private void publicGalleryApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataList> call = anInterface.listImages();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataList>() {
                    @Override
                    public void onResponse(Call<CommonModelDataList> call, Response<CommonModelDataList> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataList serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                allRandomImages=getRandomImagesAll(serverResponse.getData());
                                Collections.shuffle(allRandomImages);
                                randomImages=getRandomCard(allRandomImages);
                                getCoinList();

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.dismissLoadingDialog();
                                CommonUtils.showSnackBar(CreateGameActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataList> call, Throwable t) {
                        Log.e("failure",t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(CreateGameActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            CommonUtils.showSnackBar(CreateGameActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
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
            if(imgList.size()!=6) {
                imgList.add(list.get(i));
            }else
            {
                break;
            }
        }
        return imgList;
    }


    private List<String> getRandomImagesAll(List<ResponseBean> data) {
        List<String> allImages= new ArrayList<>();
        for(int i=0;i<data.size();i++)
        {
            for(int j=0;j<data.get(i).getImages().size();j++)
            {
                allImages.add(""+data.get(i).getImages().get(j));
            }
        }
        return allImages;
    }

    private void getCoinList()  {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.getCoins();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                referFriendCoin=serverResponse.getData().getCoinModel().get(0).getReferFriend();
                                binding.tvReferCoin.setText("+"+referFriendCoin+" coins");
                                createGameListApi();

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.dismissLoadingDialog();
                                CommonUtils.showSnackBar(CreateGameActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(CreateGameActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private void createGameListApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.createGame(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.USER_TYPE).equals("1")?ParamEnum.Facebook.theValue():ParamEnum.Instagram.theValue());
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                CommonUtils.dismissLoadingDialog();
                                bean=serverResponse.getData();
                                settingAdapter(bean,fbFriendList);
                                if(checkPermission()) new GetDefaultImage().execute();
//                                if(SharedPreferenceWriter.getInstance(CreateGameActivity.this).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("1")) {
//                                    isFromFacebook=true;
//                                    userFriendFacbookApi();
//                                }else
//                                {
//                                    CommonUtils.dismissLoadingDialog();
//                                    settingAdapter(bean,fbFriendList);
//                                    if(checkPermission()) new GetDefaultImage().execute();
//                                }
                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.dismissLoadingDialog();
                                CommonUtils.showSnackBar(CreateGameActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(CreateGameActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.dismissLoadingDialog();
            CommonUtils.showSnackBar(CreateGameActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private void userFriendFacbookApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this,"https://graph.facebook.com/v2.3/");
                Call<CommonModelDataList> call = anInterface.userFriendFacbook(SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.SOCIAL_ID));
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataList>() {
                    @Override
                    public void onResponse(Call<CommonModelDataList> call, Response<CommonModelDataList> response) {
                        if(response.isSuccessful())
                        {
                            CommonUtils.dismissLoadingDialog();
                            CommonModelDataList serverResponse=response.body();
                            fbFriendList=serverResponse.getData();
                            settingAdapter(bean,fbFriendList);
                            if(checkPermission()) new GetDefaultImage().execute();
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataList> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(CreateGameActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.dismissLoadingDialog();
            CommonUtils.showSnackBar(CreateGameActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private void init() {
        //layout objective
        countText=findViewById(R.id.countText);
        customInstEd=findViewById(R.id.customInstEd);
        preciouslyText=findViewById(R.id.preciouslyText);
        morePreciosulyRv=findViewById(R.id.morePreciosulyRv);
        objectRv=findViewById(R.id.objectRv);


        //layout header
        headerText=findViewById(R.id.headerText);
        closeIv=findViewById(R.id.closeIv);


        //layout select mode
        timeIv=findViewById(R.id.timeIv);
        liveIv=findViewById(R.id.liveIv);

        headerText.setText("Play with a Friend");
        closeIv.setOnClickListener(this);
        timeIv.setOnClickListener(this);
        liveIv.setOnClickListener(this);

        //Layout Bottom
        //No Image
        noImageLL=findViewById(R.id.noImageLL);
        tvNoImage=findViewById(R.id.tvNoImage);
        noImageLL.setOnClickListener(this);
        BounceView.addAnimTo(noImageLL);

        //Random Images
        randomImagesLL=findViewById(R.id.randomImagesLL);
        ivRandom=findViewById(R.id.ivRandom);
        tvRandom=findViewById(R.id.tvRandom);
        randomImagesLL.setOnClickListener(this);
        BounceView.addAnimTo(randomImagesLL);

        // Add Image
        addImageLL=findViewById(R.id.addImageLL);
        addImgRV=findViewById(R.id.addImgRV);
        ivAddImage=findViewById(R.id.ivAddImage);
        tvAddImage=findViewById(R.id.tvAddImage);
        addImageLL.setOnClickListener(this);
        BounceView.addAnimTo(addImageLL);

        //Main layout
        binding.sendInviteBtn.setOnClickListener(this);
        binding.referCl.setOnClickListener(this);
        BounceView.addAnimTo(binding.sendInviteBtn);
        binding.searchFriendEd.setOnFocusChangeListener(this);
        binding.searchFriendEd.setOnClickListener(this);
        binding.searchFriendEd.setOnKeyListener(this);
        new CustomTextWatcher().registerEditText(binding.searchFriendEd).registerEditText(customInstEd).setCallback(this);

    }


    private void settingAdapter(ResponseBean bean,List<ResponseBean> facebookFriendsList) {

        String deafultLangCode=getIntent().getStringExtra("defaultLangCode");
        String socialId=getIntent().getStringExtra("socialId");
        boolean isFirstUserSelectedByDefault=true;

        // Select language
        LinearLayoutManager laguagueManager= new LinearLayoutManager(this);
        laguagueManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.languageRV.setLayoutManager(laguagueManager);
        binding.languageRV.setAdapter(new SelectLanguageAdapter(CreateGameActivity.this,getUserSelectedLanguages(bean.getLaguage(),deafultLangCode!=null?deafultLangCode:bean.getUserData().getDefaultLangCode()),this));

        // Friends
        LinearLayoutManager patnerManager= new LinearLayoutManager(this);
        patnerManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.facebookFrndRV.setLayoutManager(patnerManager);
        if(socialId!=null){
            isFromInviteButton=true;
            binding.searchFriendEd.setText(getIntent().getStringExtra("name"));
            CommonUtils.showLoadingDialog(this);
        }
        else {
            if (deafultLangCode != null) {
                isFirstUserSelectedByDefault = false;
            }
            facebookFriendsAdapter = new FacebookFriendsAdapter(this, getSelectedLangaugeFriend(bean.getFbFriends(), facebookFriendsList, isFromFacebook, langCode, false, socialId, isFirstUserSelectedByDefault), this);
            binding.facebookFrndRV.setAdapter(facebookFriendsAdapter);
            if (binding.facebookFrndRV.getAdapter().getItemCount() > 0) {
                binding.tvNoPartner.setVisibility(View.GONE);
                binding.referCl.setVisibility(View.GONE);
                binding.tvInvitePatner.setVisibility(View.VISIBLE);
                binding.searchFriendEd.setVisibility(View.VISIBLE);
                binding.facebookFrndRV.setVisibility(View.VISIBLE);
                binding.ivSearch.setVisibility(View.VISIBLE);
                binding.view.setVisibility(View.VISIBLE);

            } else {
                binding.tvNoPartner.setVisibility(View.VISIBLE);
                binding.referCl.setVisibility(View.VISIBLE);
                binding.facebookFrndRV.setVisibility(View.GONE);
                binding.tvNoPartner.setText("There are no partners for the selected language.\nInvite more friends to Likewise and earn some coins.");

            }
        }

        //Objective
        List<Objective> goalList=bean.getObjective();
        if(goalList.size()>0)
        {
            this.objectiveId=goalList.get(0).getId();
            goalList.get(0).setChecked(true);
        }

        objectRv.setLayoutManager(new GridLayoutManager(this,goalList.size()));
        objectRv.setAdapter(new GameObjectiveAdapter(this,goalList,this));

        //Sub Goal
        List<Morerecisely> subGoalList=getMorePreciouslyList(objectiveId);
        morePreciosulyRv.setVisibility(View.VISIBLE);
        preciouslyText.setVisibility(View.VISIBLE);

        LinearLayoutManager morePreciosly = new LinearLayoutManager(this);
        morePreciosly.setOrientation(RecyclerView.HORIZONTAL);
        morePreciosulyRv.setLayoutManager(morePreciosly);
        morePreciosulyRv.setAdapter(new MorePrecieslyAdapter(this,subGoalList,this));

        //Add Image
        LinearLayoutManager addImageManager=new LinearLayoutManager(this);
        addImageManager.setOrientation(RecyclerView.HORIZONTAL);
        addImgRV.setLayoutManager(addImageManager);
        adapter=new AddImagesAdapter(this, AllImagesSingleton.getInstance(),this);
        addImgRV.setAdapter(adapter);
    }

    private List<FbFriend> getSelectedLangaugeFriend(List<FbFriend> fbFriendsAll,List<ResponseBean> fbFriendList,boolean isFromFacebook,String langCode,boolean isReferesh,String socialId,boolean defaultFirstSelectedUser) {
        List<FbFriend> resultList= new ArrayList<>();
        for(int i=0;i<fbFriendsAll.size();i++)
        {
            if(fbFriendsAll.get(i).getLanguageCode().contains(langCode)) {
                if (isReferesh) {
                    fbFriendsAll.get(i).setChecked(false);
                }
                resultList.add(fbFriendsAll.get(i));

            }else if(fbFriendsAll.get(i).getLanguageCode().get(0).contains(",")) {
                List<String> newCodes = new ArrayList<>(Arrays.asList(fbFriendsAll.get(i).getLanguageCode().get(0).split(",")));
                if (newCodes.contains(langCode)) {
                    if (isReferesh) {
                        fbFriendsAll.get(i).setChecked(false);
                    }
                    fbFriendsAll.get(i).setLanguageCode(newCodes);
                    resultList.add(fbFriendsAll.get(i));
                }
            }
        }

        List<FbFriend> duplicateList = new ArrayList<>();
        if(isFromFacebook) {
            for (int i = 0; i < resultList.size(); i++) {
                for (int j = 0; j < fbFriendList.size(); j++) {
                    Log.e("facbookId", fbFriendList.get(j).getId());
                    if (resultList.get(i).getSocialId().equalsIgnoreCase(fbFriendList.get(j).getId())) {
                        duplicateList.add(resultList.get(i));
                    }
                }
            }

            if (!(fbFriendList.size() > 0)) {
                duplicateList.clear();
            }
        }else
        {
            duplicateList=resultList;
            List<FbFriend> newList=new ArrayList<>();
            for (int i=0;i<duplicateList.size();i++)
            {
                if(!contains(newList,duplicateList.get(i).getId())) {
                    newList.add(duplicateList.get(i));
               }
            }
            duplicateList=newList;
        }


        // Invite Button CLicked just in case
        if(socialId!=null)
        {
            for(int i=0;i<duplicateList.size();i++)
            {
                if(duplicateList.get(i).getSocialId().equalsIgnoreCase(socialId) || duplicateList.get(i).getId().equalsIgnoreCase(socialId))
                {
                    this.socialId=socialId;
                    duplicateList.get(i).setChecked(true);
                    break;
                }
            }
        }


        if(defaultFirstSelectedUser) {
            if (duplicateList.size() > 0) {
                for (int i = 0; i < duplicateList.size(); i++) {
                    duplicateList.get(i).setChecked(false);
                }
                duplicateList.get(0).setChecked(true);
                this.socialId=duplicateList.get(0).getId();
            }
        }

        return duplicateList;
    }

    private boolean contains(List<FbFriend> newList,String id) {
        boolean ret=false;
        for(int i=0;i<newList.size();i++)
        {
            if(newList.get(i).getId().equalsIgnoreCase(id))
            {
                ret=true;
                break;
            }
        }
        return ret;
    }


    private List<Laguage> getUserSelectedLanguages(List<Laguage> countryList,String default_langCode) {
        List<Laguage> lists=new ArrayList<>();
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
            if(lists.get(i).getCode().equalsIgnoreCase(default_langCode))
            {
                lists.get(i).setChecked(true);
                langCode= lists.get(i).getCode();
                break;
            }
        }

        if(getIntent().getBooleanExtra("isFromInvite",false))
        {
            for(int i=0 ;i<countryList.size();i++)
            {
                if(countryList.get(i).getCode().equalsIgnoreCase(getIntent().getStringExtra("defaultLangCode"))) {
                    languageName = countryList.get(i).getLanguage();
                    break;
                }
            }

            if(langCode.equalsIgnoreCase(""))
            {
                CommonUtils.showSnackBar(this,"Please add "+languageName+" language in your profile first",ParamEnum.Failure.theValue());
            }
        }

    return lists;
    }

    private List<Morerecisely> getMorePreciouslyList(String id) {
        List<Morerecisely> list = new ArrayList<>();
            for(int j=0; j<bean.getMorerecisely().size();j++)
            {
                if(bean.getMorerecisely().get(j).getExplanationId()!=null){
                if(bean.getMorerecisely().get(j).getExplanationId().equalsIgnoreCase(id))
                {
                   bean.getMorerecisely().get(j).setChecked(false);
                   list.add(bean.getMorerecisely().get(j)) ;
                }
            }
            }


            if(list.size()>0)
            {
            morePreciouslyId=list.get(0).getId();
            list.get(0).setChecked(true);
            }

            return list;
        }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            // layout header
            case R.id.closeIv:
            onBackPressed();
            break;

            case R.id.noImageLL:
            isFromAddImages=false;
            if(checkPermission()) {
            CommonUtils.playSound("press_button",CreateGameActivity.this);
            settingAddImagesBackground("No Image");
            if(defaultImagePath==null) defaultImagePath=getDefaultImagePath();
            }
            break;

            case R.id.randomImagesLL:
            CommonUtils.playSound("press_button",CreateGameActivity.this);
            settingAddImagesBackground("Random Images");
            break;

            //layout Add Image
            case R.id.addImageLL:
            isFromAddImages=true;
            if(checkPermission()) {
            settingAddImagesBackground("Select Images");
            addImgRV.setVisibility(View.VISIBLE);
            randomImagesLL.setVisibility(View.INVISIBLE);
            noImageLL.setVisibility(View.INVISIBLE);
            bottomSheetAddImages();
            }
            break;

            case R.id.searchFriendEd:
            binding.scrollView.scrollTo(0, (int)binding.languageRV.getY());
            break;

            case R.id.sendInviteBtn:
            if(checkValidation()) if(checkPermission()) gameCreateApi();
            break;


            case R.id.referCl:
            String link = "https://likewise.page.link/?invitedby=" + UUID.randomUUID().toString()+"&userId="+SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID)+"&coins="+referFriendCoin+"&name="+SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.NAME);
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse(link))
                    .setDomainUriPrefix("https://likewise.page.link")
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.likewise").setMinimumVersion(125).build())
                    .setIosParameters(new DynamicLink.IosParameters.Builder("com.mobulous.LikeWise").setAppStoreId("123456789").setMinimumVersion("1.0.1").build())
                    .buildShortDynamicLink()
                    .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                        @Override
                        public void onSuccess(ShortDynamicLink shortDynamicLink) {
                            Uri  mInvitationUrl = shortDynamicLink.getShortLink();
                            Log.e("link", mInvitationUrl.toString());
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mInvitationUrl.toString());
                            startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        }
            });
            break;


            case R.id.ivSearch:
            if(binding.searchFriendEd.getText().toString().trim().length()>0) {
                CommonUtils.hideKeyboardFrom(this,binding.searchFriendEd);
                searchFriends(binding.searchFriendEd.getText().toString().trim());
            }
            break;

             //layout select mode
            case R.id.timeIv:
            settingBackground("Time");
            mode="1";
            break;

            case R.id.liveIv:
            settingBackground("Live");
            mode="2";
            break;

        }
    }

    private File getDefaultImagePath() {
        File file=null;
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.grey_icon);
        try {
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            file = new File(extStorageDirectory, "ic_launcher.PNG");
            FileOutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return file;
    }

    private void settingAddImagesBackground(String type) {
        choosenImageOptions=type;
        switch (type)
        {
            case "No Image":
            noImageLL.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_light_purple2));
            tvNoImage.setTextColor(ContextCompat.getColor(this,R.color.white));
            randomImagesLL.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_white_stroke_dot_corners));
            tvRandom.setTextColor(ContextCompat.getColor(this,R.color.grey));
            ivRandom.setImageResource(R.drawable.bitmap_random);
            addImageLL.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_white_stroke_dot_corners));
            tvAddImage.setTextColor(ContextCompat.getColor(this,R.color.grey));
            ivAddImage.setImageResource(R.drawable.add_img);
            break;

            case "Random Images":
            noImageLL.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_white_stroke_dot_corners));
            tvNoImage.setTextColor(ContextCompat.getColor(this,R.color.grey));
            randomImagesLL.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_light_purple2));
            tvRandom.setTextColor(ContextCompat.getColor(this,R.color.white));
            ivRandom.setImageResource(R.drawable.random);
            addImageLL.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_white_stroke_dot_corners));
            tvAddImage.setTextColor(ContextCompat.getColor(this,R.color.grey));
            ivAddImage.setImageResource(R.drawable.add_img);
            break;

            case "Select Images":
            noImageLL.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_white_stroke_dot_corners));
            tvNoImage.setTextColor(ContextCompat.getColor(this,R.color.grey));
            randomImagesLL.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_white_stroke_dot_corners));
            tvRandom.setTextColor(ContextCompat.getColor(this,R.color.grey));
            ivRandom.setImageResource(R.drawable.bitmap_random);
            addImageLL.setBackground(ContextCompat.getDrawable(this,R.drawable.drawable_light_purple2));
            tvAddImage.setTextColor(ContextCompat.getColor(this,R.color.white));
            ivAddImage.setImageResource(R.drawable.bitmap_add_images);
            break;

        }
    }

    private void gameCreateApi() {
    try {
        List<String> imgList = new ArrayList<>();
        List<String> galleryImgList = new ArrayList<>();
//        Log.e("choosenOption",choosenImageOptions);
        if (choosenImageOptions.equalsIgnoreCase("No Image")) {
            imgList.add(defaultImagePath.getAbsolutePath());
        }
        else if (choosenImageOptions.equalsIgnoreCase("Select Images")) {
            List<Object> allImages = AllImagesSingleton.getInstance();
            if (allImages.size() > 0) {
                for (int i = 0; i < allImages.size(); i++) {

                    //Gallery
                    if (allImages.get(i).getClass().getSimpleName().equalsIgnoreCase(GalleryModel.class.getSimpleName())) {
                        imgList.add(((GalleryModel) allImages.get(i)).getPath());
                    }

                    //Public Galley
                    else if (allImages.get(i).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName())) {
                        galleryImgList.add(((ResponseBean) allImages.get(i)).getPath());
                    }

                    //Camera
                    else if (allImages.get(i).getClass().getSimpleName().equalsIgnoreCase(CameraModel.class.getSimpleName())) {
                        imgList.add(((CameraModel) allImages.get(i)).getPath());
                    }

                }
            }
        }
        else if (choosenImageOptions.equalsIgnoreCase("Random Images")) {
            galleryImgList=randomImages;
        }

        RequestBody imagesBody=null;
        MultipartBody.Part imgpart[]=new MultipartBody.Part[imgList.size()];

        for(int i=0;i<imgList.size();i++)
        {
            File imgFile=new File(imgList.get(i));
            imagesBody=RequestBody.create(MediaType.parse("image/*"),imgFile);
            imgpart[i]=MultipartBody.Part.createFormData("images",imgFile.getName(),imagesBody);

        }

        ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
        AddRequestBody body = new AddRequestBody(new CreateGameModel(mode,langCode,customInstEd.getText().toString().trim(),morePreciouslyId,objectiveId,socialId,galleryImgList,""));

        Call<CreateGameResponse> call = anInterface.gameCreate(imgpart,body.getBody());
        ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CreateGameResponse>() {
            @Override

            public void onResponse(Call<CreateGameResponse> call, Response<CreateGameResponse> response) {
                if(response.isSuccessful())
                {
                    CreateGameResponse serverResponse=response.body();
                    if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                    {
                      //  CommonUtils.showSnackBar(CreateGameActivity.this,serverResponse.getMessage(),ParamEnum.Success.theValue());
                        //Mode Time
                        if(serverResponse.getData().getGame_details().getMode().equalsIgnoreCase("1")) {
                            Intent intent=new Intent(CreateGameActivity.this,InvitationActivity.class);
                            intent.putExtra("name",fbUserName);
                            intent.putExtra("profilepic",fbProfilePic);
                            intent.putExtra("receiver_id",serverResponse.getData().getReceiver_id());
                            intent.putExtra("gameId",serverResponse.getData().getGame_details().getId());
                            intent.putExtra("notificationId",serverResponse.getNotificationId());
                            finish();
                            startActivity(intent);
                        }
                        // Mode Live
                        else if(serverResponse.getData().getGame_details().getMode().equalsIgnoreCase("2"))
                        {
                            Log.e("senderId", SharedPreferenceWriter.getInstance(CreateGameActivity.this).getString(SPreferenceKey.ID));
                            Intent intent=new Intent(CreateGameActivity.this,ChatActivity.class);
                            intent.putExtra("cameFrom",CreateGameActivity.class.getSimpleName());
                            intent.putExtra("userData",response.body().getData());
                            startActivity(intent);
                        }
                    }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                    {
                        CommonUtils.showSnackBar(CreateGameActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                    }

                }

            }

            @Override
            public void onFailure(Call<CreateGameResponse> call, Throwable t) {
                Log.e("failure",t.getMessage());

            }
        });
    }
    catch (Exception e) {
            Toast.makeText(CreateGameActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkValidation() {
        boolean ret=true;

        if(langCode.equalsIgnoreCase(""))
        {
            ret=false;
            if(getIntent().getBooleanExtra("isFromInvite",false)) CommonUtils.showSnackBar(this,"Please add "+languageName+" language in your profile first",ParamEnum.Failure.theValue());
            else CommonUtils.showSnackBar(this,"Please select language",ParamEnum.Failure.theValue());
        }else if(checkFriend(bean.getFbFriends(),fbFriendList,isFromFacebook,langCode,false).size()==0 && !isFromSearch)
        {
            ret=false;
            CommonUtils.showSnackBar(this,"No partner found for this Langauge",ParamEnum.Failure.theValue());
        }
        else if(socialId.equalsIgnoreCase(""))
        {
            ret=false;
            CommonUtils.showSnackBar(this,"Please select partner",ParamEnum.Failure.theValue());

        }else if(objectiveId.equalsIgnoreCase(""))
        {
            ret=false;
            CommonUtils.showSnackBar(this,"Please select Goal",ParamEnum.Failure.theValue());

        }else if(morePreciouslyId.equalsIgnoreCase(""))
        {
            ret=false;
            CommonUtils.showSnackBar(this,"Please select Sub Goal",ParamEnum.Failure.theValue());

        }else if(choosenImageOptions.equalsIgnoreCase("Select Images"))
        {
            if(!(AllImagesSingleton.getInstance().size() >0)) {
                ret = false;
                CommonUtils.showSnackBar(this, "Please select atleast one image", ParamEnum.Failure.theValue());
            }
        }

        return ret;
    }

    private void settingBackground(String type) {
    if(type.equalsIgnoreCase("Live")) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            liveIv.setBackground(this.getResources().getDrawable(R.drawable.drawable_live_selected));
            timeIv.setBackground(this.getResources().getDrawable(R.drawable.drawable_time_un_selected));
        }
    }
    else if(type.equalsIgnoreCase("Time")) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            timeIv.setBackground(this.getResources().getDrawable(R.drawable.drawable_time_selected));
            liveIv.setBackground(this.getResources().getDrawable(R.drawable.drawable_live_un_selected));
        }
    }
    }



    private boolean checkPermission() {
        boolean ret=true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            {
                ret=false;
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},101);
            }
        } else {
            ret=true;
        }

        return ret;
    }

    private void bottomSheetAddImages() {
        CommonUtils.playSound("press_button",CreateGameActivity.this);
        AddImagesBottomSheet bottomSheetDialog =new AddImagesBottomSheet(addImgRV,randomImagesLL,noImageLL,this);
        bottomSheetDialog.show(getSupportFragmentManager(),"");
    }


    @Override
    public void beforeTextChanged(EditText editText, CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(EditText editText, CharSequence s, int start, int before, int count) {
        switch (editText.getId())
        {
            case R.id.searchFriendEd:
            searchFriends(s.toString());
            break;

            case R.id.customInstEd:
            countText.setText(s.toString().length()+"/300");
            break;
        }
    }

    private void searchFriends(String text) {
        if(text.equalsIgnoreCase("")) {  setApiUserFriendData();}
        else { searchUserApi(text,langCode); }
    }

    private void setApiUserFriendData() {
    isFromInviteButton=false;
    socialId="";
    isFromSearch=false;
    binding.facebookFrndRV.setAdapter(facebookFriendsAdapter=new FacebookFriendsAdapter(CreateGameActivity.this,getSelectedLangaugeFriend(bean.getFbFriends(),fbFriendList,isFromFacebook,langCode,true),CreateGameActivity.this));
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            if(facebookFriendsAdapter.getItemCount()>0)
            {
                binding.tvNoPartner.setVisibility(View.GONE);
                binding.referCl.setVisibility(View.GONE);
                binding.facebookFrndRV.setVisibility(View.VISIBLE);
            }else
            {
                binding.tvNoPartner.setVisibility(View.VISIBLE);
                binding.referCl.setVisibility(View.VISIBLE);
                binding.facebookFrndRV.setVisibility(View.GONE);
                binding.tvNoPartner.setText("There are no partners for the selected language.\nInvite more friends to Likewise and earn some coins.");
            }
        }
    },1000);
    }

    private void searchUserApi(String text, String langCode) {
    try {
        ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
        Call<CommonModelDataList> call = anInterface.searchUsers(text,langCode);
        ServicesConnection.getInstance().enqueueWithoutRetry(call, this, false, new Callback<CommonModelDataList>() {
            @Override
            public void onResponse(Call<CommonModelDataList> call, Response<CommonModelDataList> response) {
                if(response.isSuccessful()) {
                    CommonUtils.dismissLoadingDialog();
                    CommonModelDataList serverResponse = response.body();
                        socialId = "";
                        isFromSearch = true;

                        // Friends
                        LinearLayoutManager patnerManager = new LinearLayoutManager(CreateGameActivity.this);
                        patnerManager.setOrientation(RecyclerView.HORIZONTAL);
                        binding.facebookFrndRV.setLayoutManager(patnerManager);
                        binding.facebookFrndRV.setAdapter(facebookFriendsAdapter = new FacebookFriendsAdapter(CreateGameActivity.this, getSelectedLangaugeFriend(typeCastingList(serverResponse.getData()), fbFriendList, isFromFacebook, langCode, true), CreateGameActivity.this));
                        binding.facebookFrndRV.setAdapter(facebookFriendsAdapter);
                        isFromInviteButton=false;
                        if (binding.facebookFrndRV.getAdapter().getItemCount() > 0) {
                            binding.tvNoPartner.setVisibility(View.GONE);
                            binding.referCl.setVisibility(View.GONE);
                            binding.tvInvitePatner.setVisibility(View.VISIBLE);
                            binding.searchFriendEd.setVisibility(View.VISIBLE);
                            binding.facebookFrndRV.setVisibility(View.VISIBLE);
                            binding.ivSearch.setVisibility(View.VISIBLE);
                            binding.view.setVisibility(View.VISIBLE);
                        } else {
                            binding.tvNoPartner.setVisibility(View.VISIBLE);
                            binding.referCl.setVisibility(View.VISIBLE);
                            binding.facebookFrndRV.setVisibility(View.GONE);
                            binding.tvNoPartner.setText("There are no partners for the selected language.\nInvite more friends to Likewise and earn some coins.");
                        }
                    }
                }

            @Override
            public void onFailure(Call<CommonModelDataList> call, Throwable t) {
                Log.e("failure",t.getMessage());
            }
        });
    }
    catch (Exception e) {
      Toast.makeText(CreateGameActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    }

    private List<FbFriend> typeCastingList(List<ResponseBean> data) {
    searchDataList=data;
    List<FbFriend> list=new ArrayList<>();
    for(int i=0;i<data.size();i++)
    {
        list.add(new FbFriend(data.get(i).getId(),data.get(i).getProfilePic(),data.get(i).getName(),data.get(i).getSocialId(),data.get(i).getLanguageCode(),Integer.parseInt(data.get(i).getType()),data.get(i).getCreatedAt(),data.get(i).getUpdatedAt()));
    }
    return list;
    }

    @Override
    public void afterTextChanged(EditText editText, Editable editable) {
    }

    @Override
    public void onSocialPatnerSelected(String id) {
       this.socialId=id;
       Log.e("socialId",this.socialId);
       if(isFromSearch)
       {
           for(int i=0;i<searchDataList.size();i++)
           {
               if(id.equalsIgnoreCase(searchDataList.get(i).getId()))
               {
                   fbUserName = searchDataList.get(i).getName();
                   fbProfilePic = searchDataList.get(i).getProfilePic();
               }
           }
       }else {
           for (int i = 0; i < bean.getFbFriends().size(); i++) {
               if (id.equalsIgnoreCase(bean.getFbFriends().get(i).getId())) {
                   fbUserName = bean.getFbFriends().get(i).getName();
                   fbProfilePic = bean.getFbFriends().get(i).getProfilePic();
               }
           }
       }

    }

    @Override
    public void onLanguageSelected(String langCode,String langauge,String description,String flag) {
        this.langCode=langCode;
        this.isFromSearch=false;
        this.isFromInviteButton=false;
        this.isUserKnow=false;


        getIntent().putExtra("isFromInvite",false);
        socialId="";

        // Facebook Friends
        LinearLayoutManager patnerManager= new LinearLayoutManager(this);
        patnerManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.facebookFrndRV.setLayoutManager(patnerManager);
        facebookFriendsAdapter = new FacebookFriendsAdapter(this,getSelectedLangaugeFriend(bean.getFbFriends(),fbFriendList,isFromFacebook,langCode,true),this);
        binding.facebookFrndRV.setAdapter(facebookFriendsAdapter);
        if(binding.facebookFrndRV.getAdapter().getItemCount()>0)
        {
            binding.tvNoPartner.setVisibility(View.GONE);
            binding.referCl.setVisibility(View.GONE);
            binding.facebookFrndRV.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.tvNoPartner.setVisibility(View.VISIBLE);
            binding.referCl.setVisibility(View.VISIBLE);
            binding.facebookFrndRV.setVisibility(View.GONE);
            binding.tvNoPartner.setText("There are no partners for the selected language. \nInvite more friends to Likewise and earn some coins.");
        }
    }

    private List<FbFriend> getSelectedLangaugeFriend(List<FbFriend> fbFriendsAll,List<ResponseBean> fbFriendList,boolean isFromFacebook,String langCode,boolean isReferesh) {
        List<FbFriend> resultList= new ArrayList<>();
        for(int i=0;i<fbFriendsAll.size();i++)
        {
          if(fbFriendsAll.get(i).getLanguageCode().contains(langCode))
          {
              if(isReferesh) {fbFriendsAll.get(i).setChecked(false);}
              resultList.add(fbFriendsAll.get(i));

          }else if(fbFriendsAll.get(i).getLanguageCode().get(0).contains(","))
          {
              List<String> newCodes=new ArrayList<>(Arrays.asList(fbFriendsAll.get(i).getLanguageCode().get(0).split(",")));
              if(newCodes.contains(langCode)) {
                  if (isReferesh) {
                      fbFriendsAll.get(i).setChecked(false);
                  }
                  fbFriendsAll.get(i).setLanguageCode(newCodes);
                  resultList.add(fbFriendsAll.get(i));
              }
          }
        }

        List<FbFriend> duplicateList = new ArrayList<>();
        if(isFromFacebook) {
            for (int i = 0; i < resultList.size(); i++) {
                for (int j = 0; j < fbFriendList.size(); j++) {
                    Log.e("facbookId", fbFriendList.get(j).getId());
                    if (resultList.get(i).getSocialId().equalsIgnoreCase(fbFriendList.get(j).getId())) {
                        duplicateList.add(resultList.get(i));
//                    resultList.remove(i);
                    }
                }
            }

            if (!(fbFriendList.size() > 0)) {
                duplicateList.clear();
            }
        }else
        {
            duplicateList=resultList;
            List<FbFriend> newList=new ArrayList<>();
            for (int i=0;i<duplicateList.size();i++)
            {
                if(!contains(newList,duplicateList.get(i).getId())) {
                newList.add(duplicateList.get(i));
                }
            }

            duplicateList=newList;
        }

        if(isFromInviteButton)
        {
            if(duplicateList.size()>0)
            {
                for(int i=0;i<duplicateList.size();i++)
                {
                    if(getIntent().getStringExtra("socialId").equalsIgnoreCase(duplicateList.get(i).getId())){
                        this.socialId=duplicateList.get(i).getId();
                        duplicateList.get(i).setChecked(true);
                        break;
                    }

                }
            }
        } else if (duplicateList.size() > 0) {
            for (int i = 0; i < duplicateList.size(); i++) {
                duplicateList.get(i).setChecked(false);
            }
            duplicateList.get(0).setChecked(true);
            this.socialId=duplicateList.get(0).getId();
        }

        return duplicateList;
    }
    private List<FbFriend> checkFriend(List<FbFriend> fbFriendsAll,List<ResponseBean> fbFriendList,boolean isFromFacebook,String langCode,boolean isReferesh) {
        List<FbFriend> resultList= new ArrayList<>();
        for(int i=0;i<fbFriendsAll.size();i++)
        {
            if(fbFriendsAll.get(i).getLanguageCode().contains(langCode))
            {
                if(isReferesh) {fbFriendsAll.get(i).setChecked(false);}
                resultList.add(fbFriendsAll.get(i));

            }else if(fbFriendsAll.get(i).getLanguageCode().get(0).contains(","))
            {
                List<String> newCodes=new ArrayList<>(Arrays.asList(fbFriendsAll.get(i).getLanguageCode().get(0).split(",")));
                if(newCodes.contains(langCode)) {
                    if (isReferesh) {
                        fbFriendsAll.get(i).setChecked(false);
                    }
                    fbFriendsAll.get(i).setLanguageCode(newCodes);
                    resultList.add(fbFriendsAll.get(i));
                }
            }
        }

        List<FbFriend> duplicateList = new ArrayList<>();
        if(isFromFacebook) {
            for (int i = 0; i < resultList.size(); i++) {
                for (int j = 0; j < fbFriendList.size(); j++) {
                    Log.e("facbookId", fbFriendList.get(j).getId());
                    if (resultList.get(i).getSocialId().equalsIgnoreCase(fbFriendList.get(j).getId())) {
                        duplicateList.add(resultList.get(i));
//                    resultList.remove(i);
                    }
                }
            }

            if (!(fbFriendList.size() > 0)) {
                duplicateList.clear();
            }
        }else
        {
            duplicateList=resultList;
            List<FbFriend> newList=new ArrayList<>();
            for (int i=0;i<duplicateList.size();i++)
            {
                if(!contains(newList,duplicateList.get(i).getId())) {
                    newList.add(duplicateList.get(i));
                }
            }
            duplicateList=newList;
        }

        if(isFromInviteButton)
        {
            if(duplicateList.size()>0)
            {
                for(int i=0;i<duplicateList.size();i++)
                {
                    if(getIntent().getStringExtra("socialId").equalsIgnoreCase(duplicateList.get(i).getId())){
                        this.socialId=duplicateList.get(i).getId();
                        duplicateList.get(i).setChecked(true);
                        break;
                    }

                }
            }
        }
        return duplicateList;
    }

    @Override
    public void onObjectiveSelected(String objectiveId) {
        Log.e("objectiveId",objectiveId);

        this.objectiveId=objectiveId;
        this.morePreciouslyId="";
        morePreciosulyRv.setVisibility(View.VISIBLE);
        preciouslyText.setVisibility(View.VISIBLE);

        //More Preciously
        LinearLayoutManager morePreciosly = new LinearLayoutManager(this);
        morePreciosly.setOrientation(RecyclerView.HORIZONTAL);
        morePreciosulyRv.setLayoutManager(morePreciosly);
        morePreciosulyRv.setAdapter(new MorePrecieslyAdapter(this, getMorePreciouslyList(objectiveId),this));

        if(!(AllImagesSingleton.getInstance().size()>0)) {
            if (objectiveId.equalsIgnoreCase("5e8ada3529f207737e532bb7")) randomImagesLL.performClick();
            else noImageLL.performClick();
        }

    }

    @Override
    public void onMorePrecislySelected(String morePreciouslyId) {
        this.morePreciouslyId=morePreciouslyId;
    }

    @Override
    public void onSelectedImagesCalled() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK) {

            Uri uri = new CommonUtils().getImageUri(this, (Bitmap) data.getExtras().get("data"));
            List<CameraModel> list = new ArrayList<>();
            CameraModel model = new CameraModel(FilePath.getPath(this, uri));
            list.add(model);
            adapter.setData(list);
            AllImagesSingleton.add(model);
            addImgRV.setVisibility(View.VISIBLE);
            randomImagesLL.setVisibility(View.INVISIBLE);
            noImageLL.setVisibility(View.INVISIBLE);

            //Add Image
            LinearLayoutManager addImageManager = new LinearLayoutManager(this);
            addImageManager.setOrientation(RecyclerView.HORIZONTAL);
            addImgRV.setLayoutManager(addImageManager);
            adapter = new AddImagesAdapter(this, AllImagesSingleton.getInstance(), this);
            addImgRV.setAdapter(adapter);
            addImgRV.getAdapter().notifyDataSetChanged();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 101:
            if(grantResults.length>0) {
                boolean readGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (readGranted && writeGranted)
                {
                    if(isFromAddImages) bottomSheetAddImages();
                    else defaultImagePath=getDefaultImagePath();
                }else
                {
                    CommonUtils.showSnackBar(this,"You need to allow permissions for the security purpose",ParamEnum.Failure.theValue());
                }
            }
            break;
        }

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId())
        {
            case R.id.searchFriendEd:
            if(hasFocus) binding.scrollView.scrollTo(0, (int)binding.languageRV.getY());
            break;
        }
    }

    @Override
    public void onDelete() {
    if(AllImagesSingleton.getInstance().size()==0)
    {
        addImgRV.setVisibility(View.GONE);
        randomImagesLL.setVisibility(View.VISIBLE);
        noImageLL.setVisibility(View.VISIBLE);
    }
    }

    @Override
    public void onDismiss() {
        if(AllImagesSingleton.getInstance().size()==0)
        {
            addImgRV.setVisibility(View.GONE);
            randomImagesLL.setVisibility(View.VISIBLE);
            noImageLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL) {
         if(binding.searchFriendEd.getText().toString().length()>=1)
         {
             setApiUserFriendData();
         }
        }
        return false;
    }


    public class GetDefaultImage extends AsyncTask<Void,Void,File>{
        @Override
        protected File doInBackground(Void... voids) {
            return getDefaultImagePath();
        }

        @Override
        protected void onPostExecute(File file) {
            defaultImagePath=file;
        }
    }

}
