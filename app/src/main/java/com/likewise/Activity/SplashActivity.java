package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.likewise.BuildConfig;
import com.likewise.Firebase.MyFirebaseMessageService;
import com.likewise.R;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;

public class
SplashActivity extends AppCompatActivity implements OnSuccessListener<PendingDynamicLinkData> {

    TextView tvVersionNo;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tvVersionNo=findViewById(R.id.tvVersionNo);
        tvVersionNo.setText("v "+ BuildConfig.VERSION_NAME);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
        new IntentIntegrator(this).initiateScan(); // `this` is the current Activity
//
//        CommonUtils.printHashKey(this);
//        Log.e("user_id",SharedPreferenceWriter.getInstance(this).getString(SPreferenceKey.ID));
//        if(CommonUtils.getDeviceToken(SplashActivity.this))
//        {
//            intentCall();
//        }
    }




    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    private void intentCall() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(SplashActivity.this);
            }
        },3000);
    }

    @Override
    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null && pendingDynamicLinkData != null) {
            if(pendingDynamicLinkData.getLink()!=null) {
                if (pendingDynamicLinkData.getLink().getBooleanQueryParameter("invitedby", false)) {
                    String userId = pendingDynamicLinkData.getLink().getQueryParameter("userId");
                    String coins = pendingDynamicLinkData.getLink().getQueryParameter("coins");
                    String name = pendingDynamicLinkData.getLink().getQueryParameter("name");
                    createAnonymousAccountWithReferrerInfo(name, userId, coins);
                }
            }
        }

            if(SharedPreferenceWriter.getInstance(SplashActivity.this).getBoolean(SPreferenceKey.IS_LOGIN))
            {
                if(getIntent().getStringExtra("title")!=null)
                {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("cameFrom", MyFirebaseMessageService.class.getSimpleName());
                    String title=getIntent().getStringExtra("title");
                    if(title.contains("sends you an invitation!"))
                    {
                        intent.putExtra("invite", ""+true);
                    }
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }else if(!SharedPreferenceWriter.getInstance(SplashActivity.this).getBoolean(SPreferenceKey.IS_FIRST_TIME))
            {
                Intent intent = new Intent(SplashActivity.this, WalkThrowActivity.class);
                SharedPreferenceWriter.getInstance(SplashActivity.this).writeBooleanValue(SPreferenceKey.IS_FIRST_TIME,true);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent = new Intent(SplashActivity.this, SocialRegisterActivity.class);
                startActivity(intent);
                finish();
            }

    }


    private void createAnonymousAccountWithReferrerInfo(String name,String userId,String coins) {
        FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference userRecord = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                        userRecord.child("_id").setValue(user.getUid());
                        userRecord.child("referred_by").setValue(name);
                        userRecord.child("senderId").setValue(userId);
                        userRecord.child("coins").setValue(coins);
                        userRecord.child("isReceived").setValue(false);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
