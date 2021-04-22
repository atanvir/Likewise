package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.likewise.Adapter.WalkThrowAdapter;
import com.likewise.R;
import com.likewise.databinding.ActivityWalkThrowBinding;

import java.util.Timer;
import java.util.TimerTask;

public class WalkThrowActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    ActivityWalkThrowBinding binding;
    int img[]={R.drawable.w1,R.drawable.walk_th2,R.drawable.walk_th4};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_walk_throw);
        init();
    }

    private void init() {

        binding.viewPager.setAdapter(new WalkThrowAdapter(this,img));
        binding.tabLayout.setupWithViewPager(binding.viewPager,true);
        binding.viewPager.addOnPageChangeListener(this);
        binding.playText.setOnClickListener(this);
        binding.tvSkip.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            case R.id.playText:
            intent();
            break;

            case R.id.tvSkip:
            Intent intent=new Intent(WalkThrowActivity.this,SocialRegisterActivity.class);
            startActivity(intent);
            break;
        }

    }

    private void intent() {

        Intent intent=new Intent(this,SocialRegisterActivity.class);
        finish();
        startActivity(intent);

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(position==2)
        {
            binding.playText.setVisibility(View.VISIBLE);
            binding.tvSkip.setVisibility(View.INVISIBLE);
            binding.tabLayout.setVisibility(View.GONE);
        }
        else if(position!=0)
        {
            binding.playText.setVisibility(View.GONE);
            binding.tvSkip.setVisibility(View.VISIBLE);
            binding.tabLayout.setVisibility(View.VISIBLE);

        }

        else
        {
            binding.playText.setVisibility(View.GONE);
            binding.tabLayout.setVisibility(View.VISIBLE);


        }




    }

    @Override
    public void onPageSelected(int position) {


    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
