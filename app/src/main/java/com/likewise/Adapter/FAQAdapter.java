package com.likewise.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.databinding.AdapterFaqBinding;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.MyViewHolder> {

    private Context context;
    private List<String> titleList;
    private List<String> bodyTitle;

    public FAQAdapter(Context context, List<String> titleList,List<String> bodyTitle)
    {
        this.context=context;
        this.titleList=titleList;
        this.bodyTitle=bodyTitle;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterFaqBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_faq,parent,false);
        return new FAQAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.headerText.setText(titleList.get(position));
        holder.binding.desText.setText(bodyTitle.get(position));
    }

    @Override
    public int getItemCount() {
        return titleList!=null?titleList.size():0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterFaqBinding binding;

        public MyViewHolder(final AdapterFaqBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

            binding.mainCl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(binding.desText.getVisibility()==View.GONE){
                        binding.headerText.setCompoundDrawablesWithIntrinsicBounds(null,null, context.getResources().getDrawable(R.drawable.up),null);
                        binding.desText.setVisibility(View.VISIBLE);
                    }else {

                        binding.headerText.setCompoundDrawablesWithIntrinsicBounds(null,null, context.getResources().getDrawable(R.drawable.down),null);
                        binding.desText.setVisibility(View.GONE);
                    }



                }
            });

        }


    }


}
