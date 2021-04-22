package com.likewise.Utility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.likewise.Activity.ChatActivity;
import com.likewise.R;

import hari.bounceview.BounceView;

public class GameAnimationHelper implements Animation.AnimationListener {

    private  ConstraintLayout clMatch,clBonus,clStreak,clCoins;
    private  TextView tvMatch,tvBonusWords,tvBonusPoints,tvStreakWords,tvStreakPoints,tvCoins,tvCoinsScore,tvPoints;
    private  ImageView ivCoins2;
    private  Animation common_anim,coins_anim,match_anim;
    private long point;
    private Context context;

    public GameAnimationHelper(View view,Context context,long point)
    {
        this.context=context;
        init(view,context);
        this.point=point;

    }

    public GameAnimationHelper(View view,Context context)
    {
        this.context=context;
        init(view,context);

    }

    private void init(View view, Context context) {
        //Match
        clMatch=view.findViewById(R.id.clMatch);
        tvMatch=view.findViewById(R.id.tvMatch);

        //Bonus
        clBonus=view.findViewById(R.id.clBonus);
        tvBonusWords=view.findViewById(R.id.tvBonusWords);
        tvBonusPoints=view.findViewById(R.id.tvBonusPoints);

        //Streak
        clStreak=view.findViewById(R.id.clStreak);
        tvStreakWords=view.findViewById(R.id.tvStreakWords);
        tvStreakPoints=view.findViewById(R.id.tvStreakPoints);

        //Coins
        clCoins=view.findViewById(R.id.clCoins);
        tvCoinsScore=view.findViewById(R.id.tvCoinsScore);
        tvCoins=view.findViewById(R.id.tvCoins);
        ivCoins2=view.findViewById(R.id.ivCoins2);
        tvPoints=view.findViewById(R.id.tvPoints);

        common_anim=AnimationUtils.loadAnimation(context,R.anim.common_slide_up);
        coins_anim=AnimationUtils.loadAnimation(context,R.anim.coins_slide_up);
        match_anim=AnimationUtils.loadAnimation(context,R.anim.match_slide_up);
    }

    public void showMatchAnimation(long match)
    {
        clMatch.setVisibility(View.VISIBLE);
        tvMatch.setText(""+match);
        tvMatch.startAnimation(match_anim);
        match_anim.setAnimationListener(this);



    }

    public  void showBonusAnimation(int bonus,long point)
    {
        clBonus.setVisibility(View.VISIBLE);
        tvBonusWords.setText(bonus+"x words");
        tvBonusPoints.setText(""+(bonus*point));
        tvBonusPoints.startAnimation(common_anim);
        common_anim.setAnimationListener(this);
        CommonUtils.playSound("bonus",context);

    }

    public void showStreakAnimation(int showStreak,long points)
    {
        clStreak.setVisibility(View.VISIBLE);
        tvStreakWords.setText("" + showStreak + "x");
        tvStreakPoints.setText("" + (points));
        tvStreakPoints.startAnimation(common_anim);
        common_anim.setAnimationListener(this);
        CommonUtils.playSound("streaks",context);

    }

    public void showCoinAnimation(int maxScore,int coins)
    {
        clCoins.setVisibility(View.VISIBLE);
        tvCoinsScore.setText("" +  maxScore + " points");
        tvCoins.setText("" + coins + " coins");
        ivCoins2.startAnimation(coins_anim);
        coins_anim.setAnimationListener(this);
        CommonUtils.playSound("coin",context);
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(clMatch.getVisibility()==View.VISIBLE){ clMatch.setVisibility(View.GONE); }
        else if(clBonus.getVisibility()==View.VISIBLE){ clBonus.setVisibility(View.GONE);}
        else if(clStreak.getVisibility()==View.VISIBLE){ clStreak.setVisibility(View.GONE);}
        else if(clCoins.getVisibility()==View.VISIBLE) clCoins.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
