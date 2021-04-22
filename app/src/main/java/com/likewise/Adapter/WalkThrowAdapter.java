package com.likewise.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.likewise.R;

public class WalkThrowAdapter extends PagerAdapter {

    private Context context;
    private int img[];
    private String headerTile[]={"Welcome to the chat game!","Match your chat.","Who will be your best match?"};
    private String footerTile[]={"You will be paired with a partner, \n and won't see each other's chat","Follow the instructions and earn points \n when you enter the same text.","Play with friends or random partners"};

    public WalkThrowAdapter(Context context,int img[])
    {
        this.context=context;
        this.img=img;
    }

    @Override
    public int getCount() {
        return img.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.adapter_walk_throw,container,false);
        ImageView walkIv=view.findViewById(R.id.walkIv);
        TextView headerText=view.findViewById(R.id.headerText);
        TextView footerText=view.findViewById(R.id.footerText);
        TextView footerText2=view.findViewById(R.id.footerText2);

        if(position==1)
        {
            footerText2.setVisibility(View.VISIBLE);
            footerText2.setText("Score more with unique suggestions, \n multiple words and streaks");
        }
        else {
            footerText2.setVisibility(View.GONE);
        }

        headerText.setText(headerTile[position]);
        footerText.setText(footerTile[position]);
        walkIv.setImageResource(img[position]);
        ViewPager viewPager=(ViewPager) container;
        viewPager.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       ViewPager viewPager= (ViewPager)container;
       View view=(View) object;

       viewPager.removeView(view);

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }


}
