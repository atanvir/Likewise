package com.likewise.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.likewise.Activity.InstagramLoginActivity;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstagramLogin implements InstagramLoginActivity.OAuthListener {
    private OAuthAuthenticationListener mListener;
    private String authURL;
    private Context context;
    private static final String TAG = "InstagramAPI";

   // For Help please refer this https://levelup.gitconnected.com/getting-started-with-the-instagram-basic-display-api-5124c92c4935

    public InstagramLogin(Context context) {
        this.context = context;
        authURL = ParamEnum.AUTHORIZATION_INSTAGRAM.theValue()
                + "?client_id="+ParamEnum.CLIENT_ID_INSTAGRAM.theValue()
                + "&redirect_uri=" +ParamEnum.CALLBACK_URL_INSTAGRAM.theValue()
                + "&scope=user_profile,user_media&response_type=code";
        Log.e("url",authURL);
    }
    private void getAccessToken(String code) {
    try {
        CommonUtils.showLoadingDialog((Activity)context);
        ServicesInterface anInterface= ServicesConnection.getInstance().createService(context,"https://api.instagram.com/oauth/");
        Call<CommonModelDataObject> call=anInterface.accessToken(ParamEnum.CLIENT_ID_INSTAGRAM.theValue(),
                                                                 ParamEnum.CLIENT_SECRET_INSTAGRAM.theValue(),
                                                                 ParamEnum.GRANT_TYPE.theValue(),
                                                                 ParamEnum.CALLBACK_URL_INSTAGRAM.theValue(),code);
        ServicesConnection.getInstance().enqueueWithoutRetry(call, (Activity)context, false, new Callback<CommonModelDataObject>() {
            @Override
            public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                getBasicProfileDataApi(response.body().getUser_id(),response.body().getAccess_token());
            }

            @Override
            public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                mListener.onFail(t.getMessage());
            }
        });

    } catch (Exception e) {
        e.printStackTrace();
        mListener.onFail(e.getMessage());
    }
    }
    private void getBasicProfileDataApi(String socialId,String accessToken) {
        try {
            ServicesInterface anInterface= ServicesConnection.getInstance().createService(context,"https://graph.instagram.com/");
            Call<CommonModelDataObject> call=anInterface.basicDisplay(socialId,accessToken,"username,account_type,media_count");
            ServicesConnection.getInstance().enqueueWithoutRetry(call, (Activity)context, false, new Callback<CommonModelDataObject>() {
                @Override
                public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                    getHdProfilePic(response.body());
                    //getFollowersApi(accessToken);
                }

                @Override
                public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                    mListener.onFail(t.getMessage());
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            mListener.onFail(e.getMessage());
        }
    }

//    private void getFollowersApi(String accessToken) {
//
//        try {
//            ServicesInterface anInterface= ServicesConnection.getInstance().createService(context,"https://api.instagram.com/v1/users/self/");
//            Call<CommonModelDataObject> call=anInterface.getFollwerList(accessToken);
//            ServicesConnection.getInstance().enqueueWithoutRetry(call, (Activity)context, false, new Callback<CommonModelDataObject>() {
//                @Override
//                public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
//
//                }
//
//                @Override
//                public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
//                    mListener.onFail(t.getMessage());
//                }
//
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            mListener.onFail(e.getMessage());
//        }
//    }

    private void getHdProfilePic(CommonModelDataObject data) {
        try {
            ServicesInterface anInterface= ServicesConnection.getInstance().createService(context,"https://www.instagram.com/");
            Call<CommonModelDataObject> call=anInterface.getProfileInfo(data.getUsername());
            ServicesConnection.getInstance().enqueueWithoutRetry(call, (Activity)context, false, new Callback<CommonModelDataObject>() {
                @Override
                public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                   CommonUtils.dismissLoadingDialog();
                   if(response.body()!=null) mListener.onSuccess(data.getUsername(), data.getId(), response.body().getData().getUser().getFull_name(), response.body().getData().getUser().getProfile_pic_url_hd());
                   else mListener.onSuccess(data.getUsername(), data.getId(), "", "");
                }

                @Override
                public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                    mListener.onFail(t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mListener.onFail(e.getMessage());
        }
    }

    public void setListener(OAuthAuthenticationListener listener) {
        mListener = listener;
    }

    public void authorize() {
        Intent intent=new Intent(context, InstagramLoginActivity.class);
        intent.putExtra("url",authURL);
        InstagramLoginActivity.setListner(this);
        context.startActivity(intent);
    }


    @Override
    public void onLoginSuccessFul(String accessToken) {
        Log.e("before ","Access Token "+accessToken);
        accessToken=accessToken.replace("#_","");
        Log.e("after ","Access Token "+accessToken);
        getAccessToken(accessToken);

    }

    @Override
    public void onError(String error) {
        mListener.onFail("Authorization failed "+error);
    }

    public interface OAuthAuthenticationListener {
        public abstract void onSuccess(String username,String id,String fullName,String profilePic);
        public abstract void onFail(String error);
    }

}