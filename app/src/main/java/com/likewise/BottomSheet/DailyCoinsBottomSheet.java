package com.likewise.BottomSheet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.likewise.Activity.GetCoinsActivity;
import com.likewise.Activity.MainActivity;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.BottomSheetDailyCoinsBinding;

import hari.bounceview.BounceView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyCoinsBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener, OnInitializationCompleteListener {
    private BottomSheetDailyCoinsBinding binding;
    private OnCoinsData listner;
    int dailyCoin;
    int videopoint;
    RewardedAd rewardedAd;

    public DailyCoinsBottomSheet(OnCoinsData listner) {
        this.listner = listner;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_daily_coins, container, false);
        CommonUtils.makeMeBlink(binding.clCollect,getActivity().getResources().getDrawable(R.drawable.drawable_red_corners2),800);
        getDailyCoins();
        BounceView.addAnimTo(binding.clCollect);
        BounceView.addAnimTo(binding.clDoubleEarning);
        binding.clCollect.setOnClickListener(this);
        binding.clDoubleEarning.setOnClickListener(this);
        return binding.getRoot();
    }




    private void getDailyCoins() {
        if (CommonUtils.networkConnectionCheck(getActivity())) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(getActivity());
                Call<CommonModelDataObject> call = anInterface.getCoins();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, getActivity(), true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if (response.isSuccessful()) {
                            CommonModelDataObject serverResponse = response.body();

                            if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue())) {
                                dailyCoin = serverResponse.getData().getCoinModel().get(0).getDailyCollection();
                                videopoint = serverResponse.getData().getCoinModel().get(0).getWatchVideo();
//                                if (SharedPreferenceWriter.getInstance(getActivity()).getInt(SPreferenceKey.DAY) <= 1) {
//                                    binding.tvDay.setText("Day " + 1 + ":");
//                                } else {
//                                    binding.tvDay.setText("Days " + SharedPreferenceWriter.getInstance(getActivity()).getInt(SPreferenceKey.DAY) + ":");
//                                }

                                binding.tvCoins.setText("" + dailyCoin + " coins");
                            } else if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue())) {
                                CommonUtils.showSnackBar(getActivity(), serverResponse.getMessage(), ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure", t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        } else {
            CommonUtils.showSnackBar(getActivity(), getString(R.string.no_internet), ParamEnum.Failure.theValue());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clCollect:
                addDailyCoin(dailyCoin);
                break;

            case R.id.clDoubleEarning:
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
                            addDailyCoin(dailyCoin*2);
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display.
                            CommonUtils.showSnackBar(getActivity(), "" + errorCode, ParamEnum.Failure.theValue());
                        }
                    };
                    rewardedAd.show(getActivity(), adCallback);
                } else {
                    Toast.makeText(getActivity(), "Sorry! Currently We have No Ads \n Please try after some time", Toast.LENGTH_SHORT).show();
//                    CommonUtils.showSnackBar(getActivity(), "Sorry! Currently We have No Ads \n Please try after some time", ParamEnum.Failure.theValue());
                }

                break;
        }
    }

    private void addDailyCoin(int dailyCoin) {
        if (CommonUtils.networkConnectionCheck(getActivity())) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(getActivity());
                Call<CommonModelDataObject> call = anInterface.collectCoin(dailyCoin, "2");
                ServicesConnection.getInstance().enqueueWithoutRetry(call, getActivity(), true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if (response.isSuccessful()) {
                            CommonModelDataObject serverResponse = response.body();

                            if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue())) {
                                CommonUtils.playSound("coin", getActivity());
                                int day=SharedPreferenceWriter.getInstance(getActivity()).getInt(SPreferenceKey.DAY);
                                day=day+1;
                                SharedPreferenceWriter.getInstance(getActivity()).writeIntValue(SPreferenceKey.DAY,day);
                                SharedPreferenceWriter.getInstance(getActivity()).writeStringValue(SPreferenceKey.COINS,""+serverResponse.getData().getCoins());
                                dismiss();
                                if (listner != null)
                                    listner.coins(SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.COINS));
                            } else if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue())) {
                                CommonUtils.showSnackBar(getActivity(), serverResponse.getMessage(), ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure", t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        } else {
            CommonUtils.showSnackBar(getActivity(), getString(R.string.no_internet), ParamEnum.Failure.theValue());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        videoAds();

    }

    private void videoAds() {
        MobileAds.initialize(getActivity(), this);
        rewardedAd = new RewardedAd(getActivity(), ParamEnum.UNIT_ID.theValue());
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }



    @Override
    public void onInitializationComplete(InitializationStatus initializationStatus) {

    }

    public interface OnCoinsData {
        void coins(String coins);
    }
}
