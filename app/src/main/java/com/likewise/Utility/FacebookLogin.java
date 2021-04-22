package com.likewise.Utility;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FacebookLogin extends AppCompatActivity implements FacebookCallback<LoginResult> {

    private static final int FB_LOGIN = 121;
    CallbackManager callbackManager;
    AccessToken accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        fbSignIn();
        accessToken = AccessToken.getCurrentAccessToken();
    }


    private void fbSignIn() {
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        LoginManager.getInstance().logOut();
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList( "public_profile","email"));
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList( "public_profile","email","user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager,this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        getBasicProfile(loginResult);
    }

    @Override
    public void onCancel() {
        CommonUtils.showSnackBar(this,"Cancelled",ParamEnum.Failure.theValue());
        finish();
    }

    @Override
    public void onError(FacebookException error) {

    }
    private void getBasicProfile(LoginResult loginResult) {
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("onFacebook",""+object.toString());
                            Intent intent = new Intent();
                            intent.putExtra("id", ""+object.optString("id"));
                            intent.putExtra("email", object.optString("email"));
                            intent.putExtra("name", object.optString("name"));
                            intent.putExtra("image", "https://graph.facebook.com/"+object.optString("id")+"/picture?type=large");
                            intent.putExtra("friends", ""+ object.optJSONObject("friends"));
                            setResult(FB_LOGIN,intent);
                            Log.e("email","------>"+object.optString("email"));
                            finish();

                    }
                });

        Log.e("accessToken",""+token.getToken());

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,friends");
        request.setParameters(parameters);
        request.executeAsync();

    }

}