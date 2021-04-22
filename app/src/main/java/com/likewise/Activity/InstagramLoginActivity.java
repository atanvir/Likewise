package com.likewise.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.likewise.R;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityInstagramBinding;

public class InstagramLoginActivity extends AppCompatActivity {
    private static OAuthListener mListener;
    private static final String TAG = InstagramLoginActivity.class.getSimpleName();
    ActivityInstagramBinding binding;
    public static void setListner(OAuthListener listener) {
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_instagram);
        binding.webview.setVerticalScrollBarEnabled(false);
        binding.webview.setHorizontalScrollBarEnabled(false);
        binding.webview.setWebViewClient(new OAuthWebViewClient());
        binding.webview.getSettings().setJavaScriptEnabled(true);
        String userAgent = binding.webview.getSettings().getUserAgentString();
        binding.webview.loadUrl(getIntent().getStringExtra("url"));
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    private class OAuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("url",url);
            if (url.startsWith(ParamEnum.CALLBACK_URL_INSTAGRAM.theValue())) {
                String urls[] = url.split("=");
                mListener.onLoginSuccessFul(urls[1]);
                finish();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(description);
            finish();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            binding.lottieAnimationView.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            binding.lottieAnimationView.setVisibility(View.GONE);
        }
    }

    public interface OAuthListener {
        public abstract void onLoginSuccessFul(String accessToken);
        public abstract void onError(String error);
    }
}