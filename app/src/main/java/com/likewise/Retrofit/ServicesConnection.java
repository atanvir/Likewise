package com.likewise.Retrofit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.likewise.Activity.PlayGameActivity;
import com.likewise.Activity.SocialRegisterActivity;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.SocketModel;
import com.likewise.R;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SPrefrenceValues;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServicesConnection {
    private static ServicesConnection connect;
    private ServicesInterface clientService;
//   private static final String BASE_URL = "http://3.128.167.18:8000/mobile/";
   private static final String BASE_URL = "http://18.224.247.81:8000/mobile/";
    public static final int DEFAULT_RETRIES = 0;

    public static synchronized ServicesConnection getInstance() {
        if (connect == null) {
            connect = new ServicesConnection();
        }
        return connect;
    }


    public ServicesInterface createService(final Context context) throws Exception {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);//
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(1, TimeUnit.MINUTES);
        httpClient.readTimeout(1, TimeUnit.MINUTES);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization",SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.JWT_TOKEN))
                        .method(original.method(),original.body())
                        .build();
                return chain.proceed(request);

            }

        });
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        clientService = retrofit.create(ServicesInterface.class);
        return clientService;
    }

    public ServicesInterface createService(Context context,String url) throws Exception {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);//
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(1, TimeUnit.MINUTES);
            httpClient.readTimeout(1, TimeUnit.MINUTES);
            httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            clientService = retrofit.create(ServicesInterface.class);

        return clientService;
    }




    //    enqueue
    public <T> boolean enqueueWithRetry(Call<T> call, final Activity activity, boolean isLoader, final int retryCount, final Callback<T> callback) {
        if (CommonUtils.networkConnectionCheck(activity)) {
            if(isLoader)
            {
                if(activity!=null){
                    CommonUtils.showLoadingDialog(activity);
                }
            }
            call.enqueue(new ServicesRetryableCallback<T>(call, retryCount)
            {
                @Override
                public void onFinalResponse(Call<T> call, Response<T> response)
                {
                    if(isLoader){CommonUtils.dismissLoadingDialog();}
                    if(checkUnAuthorisedUser(response.body(),activity)) {
                        callback.onResponse(call, response);
                    }


                }

                @Override
                public void onFinalFailure(Call<T> call, Throwable t)
                {
                    if(CommonUtils.customProgressBar!=null)
                    {
                        CommonUtils.dismissLoadingDialog();
                    }
                    if(t instanceof SocketTimeoutException)
                    {

                    }


                    callback.onFailure(call, t);
                }
            });
           return true;
        } else {
            CommonUtils.showSnackBar(activity,activity.getString(R.string.no_internet), ParamEnum.Failure.theValue());
            CommonUtils.dismissLoadingDialog();
            return false;
        }
    }

    private <T> boolean checkUnAuthorisedUser(T body, Activity activity) {
        boolean ret=true;
        if(body instanceof CommonModelDataObject)
        {
            if(((CommonModelDataObject) body).getStatus()!=null) {
                if (((CommonModelDataObject) body).getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue())) {
                    if (((CommonModelDataObject) body).getMessage().equalsIgnoreCase("Unauthorised user")) {
                        ret = intent(activity);
                    }
                }
            }
        }else if(body instanceof CommonModelDataList)
        {
            if(((CommonModelDataList) body).getStatus()!=null){
            if(((CommonModelDataList) body).getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue())) {
                if (((CommonModelDataList) body).getMessage().equalsIgnoreCase("Unauthorised user")) {

                    ret = intent(activity);
                }
            }
            }
        }

        return ret;
    }

    private boolean intent(Activity activity) {
        Intent intent=new Intent(activity, SocialRegisterActivity.class);
        activity.startActivity(intent);
        activity.finish();
        SPrefrenceValues.removePrefrences(activity);
        CommonUtils.getDeviceToken(activity);
        Toast.makeText(activity,"Login Expire,Please Login Again",Toast.LENGTH_LONG).show();
        return false;

    }

    public  <T> boolean  enqueueWithoutRetry(Call<T> call, Activity activity, boolean isLoader, final Callback<T> callback) {
        return enqueueWithRetry(call, activity,isLoader, DEFAULT_RETRIES, callback);
    }
}
