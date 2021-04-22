package com.likewise.Adapter;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.R;
import com.likewise.databinding.AdapterAddInterestBinding;
import com.likewise.databinding.AdapterCoinsBinding;

import java.util.List;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.MyViewHolder> {

    private Context context;
    private List<String> interestList;

    public InterestAdapter(Context context, List<String> interestList)
    {
        this.context=context;
        this.interestList=interestList;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterAddInterestBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_add_interest,parent,false);
        return new InterestAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.edInterest.setText(interestList.get(position));
    }

    @Override
    public int getItemCount() {
        return interestList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements TextWatcher {

        AdapterAddInterestBinding binding;

        public MyViewHolder(AdapterAddInterestBinding binding) {
            super(binding.getRoot());
            this.binding=binding;


          //  binding.edInterest.addTextChangedListener(this);

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                interestList.remove(interestList.get(getAdapterPosition()));
                interestList.add(charSequence.toString());
                notifyDataSetChanged();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
