package com.likewise.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Model.Morerecisely;
import com.likewise.Interface.OnItemClickListner;
import com.likewise.R;
import com.likewise.Utility.CommonUtils;
import com.likewise.databinding.AdapterMorePreciselyBinding;

import java.util.List;

public class MorePrecieslyAdapter extends RecyclerView.Adapter<MorePrecieslyAdapter.MyViewHolder> {

    private Context context;
    private List<Morerecisely> morereciselyList;
    private OnItemClickListner listner;

    public MorePrecieslyAdapter(Context context, List<Morerecisely> morereciselyList, OnItemClickListner listner)
    {
        this.context=context;
        this.morereciselyList=morereciselyList;
        this.listner=listner;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterMorePreciselyBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_more_precisely,parent,false);
        return new MorePrecieslyAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.text.setText(morereciselyList.get(position).getConverse());
        if(morereciselyList.get(position).isChecked())
        {
            holder.binding.text.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.text.setBackground(context.getResources().getDrawable(R.drawable.drawable_light_purple2));
        }else
        {
            holder.binding.text.setTextColor(context.getResources().getColor(R.color.grey));
            holder.binding.text.setBackground(context.getResources().getDrawable(R.drawable.drawable_white_stroke_dot_corners));

        }

    }

    @Override
    public int getItemCount() {
        return morereciselyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AdapterMorePreciselyBinding binding;
        public MyViewHolder(AdapterMorePreciselyBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            CommonUtils.showAnimation(binding.mainCl);
            binding.mainCl.setOnClickListener(this);
            binding.text.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.mainCl:

                    for(int i=0;i<morereciselyList.size();i++)
                    {
                        morereciselyList.get(i).setChecked(false);
                    }
                    listner.onMorePrecislySelected(morereciselyList.get(getAdapterPosition()).getId());
                    morereciselyList.get(getAdapterPosition()).setChecked(true);
                    notifyDataSetChanged();

                break;

                case R.id.text:
                    binding.mainCl.performClick();
                    break;
            }

        }
    }



}
