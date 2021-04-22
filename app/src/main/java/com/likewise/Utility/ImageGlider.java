package com.likewise.Utility;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.likewise.Activity.CreateGameActivity;
import com.likewise.Activity.OtherUserProfileActivity;
import com.likewise.Activity.PlayGameActivity;
import com.likewise.Activity.UserProfileActivity;
import com.likewise.R;


public class ImageGlider
{

    public static void setRoundImage(Context context,ImageView imageView, ProgressBar progressBar,String url)
    {
        int placeHolder=R.drawable.anonymous_user;
        if(progressBar!=null)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        if(url.equalsIgnoreCase("https://res.cloudinary.com/dlopkjzfr/image/upload/v1580813623/qtjpwprowb6mouuqmyjp.png"))
        {
            url="";
        }

        if(context instanceof CreateGameActivity)
        {
            if(progressBar.getId()==R.id.progressBar44)
            {

            }else {
                placeHolder = 0;
            }
        }else if(context instanceof PlayGameActivity)
        {
            placeHolder = 0;
        }

        Glide.with(context).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if(progressBar!=null)
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if(progressBar!=null)
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).apply(RequestOptions.bitmapTransform(new RoundedCorners(14))).placeholder(placeHolder).into(imageView);
    }



    public  static void setNormalImage(Context context,ImageView userIv, ProgressBar progressBar,String url)
    {
//        int placeHolder=R.drawable.anonymous_user;
        if(url.equalsIgnoreCase("https://res.cloudinary.com/dlopkjzfr/image/upload/v1580813623/qtjpwprowb6mouuqmyjp.png"))
        {
            url="";
        }


        Glide.with(context).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(userIv);
    }



}
