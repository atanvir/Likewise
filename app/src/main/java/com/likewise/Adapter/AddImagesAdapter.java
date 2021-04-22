package com.likewise.Adapter;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Model.CameraModel;
import com.likewise.Model.FbFriend;
import com.likewise.Model.GalleryModel;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Utility.AllImagesSingleton;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.databinding.AdapterAddImagesBinding;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.CorneredSort;

import java.util.ArrayList;
import java.util.List;

import hari.bounceview.BounceView;
import okhttp3.Response;

public class AddImagesAdapter extends RecyclerView.Adapter<AddImagesAdapter.MyViewHolder>  {

    private Context context;
    private List<? extends Object> list;
    private onImageDeleteListner listner;


    public AddImagesAdapter(Context context, List<? extends Object> list,onImageDeleteListner listner)
    {
        this.context=context;
        this.list=list;
        this.listner=listner;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterAddImagesBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_add_images,parent,false);
        return new AddImagesAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Gallery
        if (list.get(position).getClass().getSimpleName().equalsIgnoreCase(GalleryModel.class.getSimpleName()))
        {
            ImageGlider.setRoundImage(context,holder.binding.imageIv,holder.binding.progressBar,((GalleryModel)list.get(position)).getPath());



        }
        //Public Gallery
        else if (list.get(position).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName()))
        {
            ImageGlider.setRoundImage(context,holder.binding.imageIv,holder.binding.progressBar,((ResponseBean)list.get(position)).getPath());

        }
        //Camera
        else if (list.get(position).getClass().getSimpleName().equalsIgnoreCase(CameraModel.class.getSimpleName()))
        {
            ImageGlider.setRoundImage(context,holder.binding.imageIv,holder.binding.progressBar,((CameraModel)list.get(position)).getPath());


        }



    }
    public void setData(List<? extends Object> data)
    {
        this.list=data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AdapterAddImagesBinding binding;

        public MyViewHolder(final AdapterAddImagesBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            CommonUtils.showAnimation(binding.mainCl);
            BounceView.addAnimTo(binding.selectIv);
            binding.selectIv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.selectIv:
                list.remove(getAdapterPosition());
                notifyDataSetChanged();
                if(listner!=null)
                {
                    listner.onDelete();
                }
                break;
            }
        }

    }

    public interface onImageDeleteListner{
        void onDelete();
    }


}
