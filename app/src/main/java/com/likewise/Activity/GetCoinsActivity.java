package com.likewise.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.adapters.ToolbarBindingAdapter;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.likewise.Adapter.CoinsAdapter;
import com.likewise.Model.CoinPay;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.CustomCoinSorting;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityGetCoinsBinding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import hari.bounceview.BounceView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetCoinsActivity extends AppCompatActivity implements View.OnClickListener, OnInitializationCompleteListener, OnCompleteListener<ShortDynamicLink>, CoinsAdapter.coinPurchase, PurchasesUpdatedListener, BillingClientStateListener, SkuDetailsResponseListener {

    ActivityGetCoinsBinding binding;
    //layout header
    TextView headerText;
    ImageView qstIv,closeIv;
    RewardedAd rewardedAd;
    List<SkuDetails> list;
    private Integer watchVideoCoin,referFriendCoin;
    BillingClient billingClient;
    String sku="";
    int coins=0;
    boolean isCalled;
    List<String> skuDetailsList = new ArrayList<>();
    private SkuDetailsParams.Builder params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_get_coins);
        init();
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(this);
        CommonUtils.showLoadingDialog(this);
        getCoinList();
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
                             // setUserProfile(response.body());
                                skuDetailsList.clear();
                                List<CoinPay> list=serverResponse.getData().getCoinPay();
                                for(int i=0;i<list.size();i++) {
                                    if(list.get(i).getId().equals("5ea1744834bd5c719e30a8e9")) {
                                        list.get(i).setId("5ea1744834bd5c719e30a8e10");
                                    }
                                        skuDetailsList.add("" + list.get(i).getId());
                               }
                                 params= SkuDetailsParams.newBuilder();
                                 params.setSkusList(skuDetailsList).setType(BillingClient.SkuType.INAPP);
                                 billingClient.querySkuDetailsAsync(params.build(),GetCoinsActivity.this);
                                watchVideoCoin=serverResponse.getData().getCoinModel().get(0).getWatchVideo();
                                referFriendCoin=serverResponse.getData().getCoinModel().get(0).getReferFriend();
                                binding.tvWatchVideoCoin.setText("+"+serverResponse.getData().getCoinModel().get(0).getWatchVideo()+" coins");
                                binding.tvReferCoin.setText("+"+serverResponse.getData().getCoinModel().get(0).getReferFriend()+" coins");

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.dismissLoadingDialog();
                                CommonUtils.showSnackBar(GetCoinsActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(GetCoinsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoAds();
    }

    private void videoAds() {
        MobileAds.initialize(this, this);
        rewardedAd = new RewardedAd(this, ParamEnum.UNIT_ID.theValue());
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                Log.e("loaded","onRewardedAdLoaded");
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                Log.e("failedToLoad","failed error"+errorCode);
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    private void init() {
        //layout header
        headerText=findViewById(R.id.headerText);
        qstIv=findViewById(R.id.qstIv);
        closeIv=findViewById(R.id.closeIv);

        headerText.setText(getString(R.string.get_coins));
        qstIv.setVisibility(View.GONE);

        BounceView.addAnimTo(binding.referCl);
        BounceView.addAnimTo(binding.referCl);
        BounceView.addAnimTo(binding.btnTryAgain);

        closeIv.setOnClickListener(this);
        binding.referCl.setOnClickListener(this);
        binding.watchVideocl.setOnClickListener(this);
        binding.btnTryAgain.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.closeIv:
            onBackPressed();
            break;

            case R.id.btnTryAgain:
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(this);
            CommonUtils.showLoadingDialog(this);
            getCoinList();
            break;

            case R.id.referCl:
            String link = "https://likewise.page.link/?invitedby=" + UUID.randomUUID().toString()+"&userId="+SharedPreferenceWriter.getInstance(GetCoinsActivity.this).getString(SPreferenceKey.ID)+"&coins="+referFriendCoin+"&name="+SharedPreferenceWriter.getInstance(GetCoinsActivity.this).getString(SPreferenceKey.NAME);
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

            case R.id.watchVideocl:
            if (rewardedAd.isLoaded()) {
                RewardedAdCallback adCallback = new RewardedAdCallback() {
                    @Override
                    public void onRewardedAdOpened() {
                        // Ad opened.
                    }

                    @Override
                    public void onRewardedAdClosed() {
                        // Ad closed.
                    }

                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem reward) {
                        Log.i("reward",""+reward.getAmount());
                        addCoinsApi(watchVideoCoin);
                        // User earned reward.
                    }

                    @Override
                    public void onRewardedAdFailedToShow(int errorCode) {
                        // Ad failed to display.
                        CommonUtils.showSnackBar(GetCoinsActivity.this,""+errorCode,ParamEnum.Failure.theValue());
                    }
                };
                rewardedAd.show(this, adCallback);}
            else{
                CommonUtils.showSnackBar(this,"Sorry! Currently We have No Ads \n Please try after some time",ParamEnum.Failure.theValue());
             }
            break;
        }
    }

    private void addCoinsApi(Integer coins)  {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.addCoins(coins);
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                SharedPreferenceWriter.getInstance(GetCoinsActivity.this).writeStringValue(SPreferenceKey.COINS,serverResponse.getData().getCoins());
                                CommonUtils.playSound("earn_coin",GetCoinsActivity.this);
                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(GetCoinsActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(GetCoinsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }


    @Override
    public void onInitializationComplete(InitializationStatus initializationStatus) {
        Log.e("onIntComplete", "onInitializationComplete"+initializationStatus.toString());
    }

    @Override
    public void onComplete(@NonNull com.google.android.gms.tasks.Task<ShortDynamicLink> task) {
        if(task.isSuccessful())
        {
            Uri shortLink =task.getResult().getShortLink();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shortLink.toString());
            startActivity(Intent.createChooser(sharingIntent, "Share using"));

        }
        else
        {
            Log.e("error",""+task.getException());
        }

    }

    @Override
    public void onPurchase(SkuDetails model) {
        sku=model.getSku();
        String coinsText1= model.getTitle().split("coins")[0].trim();
        coins= Integer.parseInt(coinsText1.replace("+",""));
        Log.e("sku",sku);
        Log.e("coins",""+coins);


        // method Open Popup for billing purchase
        BillingFlowParams flowParams =  BillingFlowParams.newBuilder().setSkuDetails(model).build();
        BillingResult responseCode= billingClient.launchBillingFlow(GetCoinsActivity.this,flowParams);
    }


    // when user buy a product
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            for (Purchase purchase : list) {
                handlePuchase(purchase);
            }
        } else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED)
        {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)
        {
            Toast.makeText(this, "Already Purchased", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePuchase(Purchase purchase) {
        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
        {
            if(purchase.getSku().equals(sku))
            {
                ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                ConsumeResponseListener listener = new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                        Toast.makeText(GetCoinsActivity.this, "Puchase Acknowledge", Toast.LENGTH_SHORT).show();
                    }
                };
                billingClient.consumeAsync(consumeParams,listener);
                Toast.makeText(this, "Puchase SuccessFull", Toast.LENGTH_SHORT).show();
                addCoinsApi(coins);
            }
        }

    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        CommonUtils.dismissLoadingDialog();
    }

    @Override
    public void onBillingServiceDisconnected() {
        CommonUtils.dismissLoadingDialog();
    }

    @Override
    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
        if(list!=null && billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK)
        {
            binding.lottieAnimationView.setVisibility(View.GONE);
            binding.btnTryAgain.setVisibility(View.GONE);
            binding.recyclerView2.setVisibility(View.VISIBLE);
            binding.tvLabel.setText("Buy");
            this.list=list;
            Collections.sort(list,new CustomCoinSorting());
            GridLayoutManager manager=new GridLayoutManager(GetCoinsActivity.this,2);
            binding.recyclerView2.setLayoutManager(manager);
            binding.recyclerView2.setAdapter(new CoinsAdapter(GetCoinsActivity.this,list,GetCoinsActivity.this));
            binding.recyclerView2.getAdapter().notifyDataSetChanged();
        }else
        {
            binding.lottieAnimationView.setVisibility(View.VISIBLE);
            binding.btnTryAgain.setVisibility(View.VISIBLE);
            binding.tvLabel.setText("Oops!\n Something went wrong");
            binding.recyclerView2.setVisibility(View.GONE);

        }
    }


}
