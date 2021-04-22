package com.likewise.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.databinding.AdapterCoinsBinding;
import com.likewise.databinding.AdapterNationalityBinding;

import java.util.ArrayList;
import java.util.List;

import hari.bounceview.BounceView;


public class NationalityAdapter extends RecyclerView.Adapter<NationalityAdapter.MyViewHolder> {

    private Context context;
    private List<ResponseBean> list;
    private OnItemClick listner;

    public NationalityAdapter(Context context, List<ResponseBean> list,OnItemClick listner)
    {
        this.context=context;
        this.list=list;
        this.listner=listner;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterNationalityBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_nationality,parent,false);
        return new NationalityAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageGlider.setRoundImage(context,holder.binding.countryIv,holder.binding.progressBar,list.get(position).getPicture());
        holder.binding.countryNameText.setText(list.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdapterNationalityBinding binding;
        public MyViewHolder(AdapterNationalityBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            CommonUtils.showAnimation(binding.mainCl);
            BounceView.addAnimTo(binding.mainCl);
            binding.mainCl.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.mainCl:
                if(listner!=null)
                {
                    listner.onClick(list.get(getAdapterPosition()).getPicture(),list.get(getAdapterPosition()).getDescription());
                }
                break;
            }
        }

    }

    public interface OnItemClick
    {
       void onClick(String countryImage,String countryName);
    }
}
