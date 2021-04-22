package com.likewise.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.likewise.Model.PlayGameResponseModel;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Utility.ImageGlider;

import java.util.List;

public class ImgeSliderAdapter extends PagerAdapter {

    private Context context;
    private List<PlayGameResponseModel.UserDetail> list;
    public ImgeSliderAdapter(Context context, List<PlayGameResponseModel.UserDetail> list)
    {
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view= layoutInflater.inflate(R.layout.adapter_imageslide,container,false);
        ImageView ivUser=view.findViewById(R.id.userIv);
        ProgressBar progresBar=view.findViewById(R.id.progressBar);
        if(list.get(position)!=null) {
            ImageGlider.setRoundImage(context, ivUser, progresBar, list.get(position).getProfilePic());
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
}
