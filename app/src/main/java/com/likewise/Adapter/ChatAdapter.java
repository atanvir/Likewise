package com.likewise.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Model.MessageResponse;
import com.likewise.R;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.MyPasswordTransformation;
import com.likewise.databinding.AdapterLeftChatBinding;
import com.likewise.databinding.AdapterRightChatBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>  {
    private Context context;
    final int LEFT_USER=2;
    final int RIGHT_USER=1;
    private List<MessageResponse> list;

    public ChatAdapter(Context context, List<MessageResponse> list) {
        this.context = context;
        this.list=list;
    }

    @Override
    public int getItemViewType(int position) {
        int pos=-1;
        if(list!=null) {
            if (list.get(position).getSenderId().equalsIgnoreCase(SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID))) pos= RIGHT_USER;
            else pos= LEFT_USER;
        }
        return pos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==LEFT_USER) {
            AdapterLeftChatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_left_chat, parent, false);
            return new ChatAdapter.MyViewHolder(binding);
        }else
        {
            AdapterRightChatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_right_chat, parent, false);
            return new ChatAdapter.MyViewHolder(binding);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        switch (holder.getItemViewType())
        {
            case RIGHT_USER:
            holder.rightBinding.tvRight.setText(list.get(position).getMessage());
            if(list.get(position).isMatched()) holder.rightBinding.ivRight.setBackgroundTintList(context.getColorStateList(R.color.chat_purple));
            else holder.rightBinding.ivRight.setBackgroundTintList(context.getColorStateList(R.color.chat_black));
            break;

            case LEFT_USER:
            if(list.get(position).isMatched()) {
                holder.leftBinding.ivleft.setBackgroundTintList(context.getColorStateList(R.color.chat_purple));
                holder.leftBinding.tvleft.setText(list.get(position).getMessage());
                holder.leftBinding.tvleft.setVisibility(View.VISIBLE);
                holder.leftBinding.tIEdText.setVisibility(View.GONE);
            }
            else {
               holder.leftBinding.ivleft.setBackgroundTintList(context.getColorStateList(R.color.chat_black));
                if(list.get(position).isEncrypted())
                {
                    holder.leftBinding.tvleft.setText(list.get(position).getEncryped());
                    holder.leftBinding.tvleft.setVisibility(View.GONE);
                    holder.leftBinding.tIEdText.setVisibility(View.VISIBLE);
                    holder.leftBinding.tIEdText.setTransformationMethod(new MyPasswordTransformation());
                    holder.leftBinding.tIEdText.setText(list.get(position).getMessage());
                }else {
                    holder.leftBinding.tvleft.setText(list.get(position).getMessage());
                    holder.leftBinding.tvleft.setVisibility(View.VISIBLE);
                    holder.leftBinding.tIEdText.setVisibility(View.GONE);
                }
                }
            break;
        }
    }


    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdapterLeftChatBinding leftBinding;
        public MyViewHolder(AdapterLeftChatBinding binding) {
            super(binding.getRoot());
            this.leftBinding = binding;
            CommonUtils.showAnimation(binding.mainCl);
            binding.mainCl.setOnClickListener(this);
        }

        AdapterRightChatBinding rightBinding;
        public MyViewHolder(AdapterRightChatBinding binding) {
            super(binding.getRoot());
            this.rightBinding=binding;
            CommonUtils.showAnimation(binding.mainCl);
            binding.mainCl.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.mainCl:
                CommonUtils.hideKeyboardFrom(context,((Activity)context).getCurrentFocus());
                break;
            }
        }
    }
}
