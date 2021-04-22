package com.likewise.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.likewise.Model.CoinPay;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.AdapterCoinsBinding;

import java.util.List;

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.MyViewHolder> {
    private Context context;
    private List<SkuDetails> list;
    int coins[]={R.drawable.coins1,R.drawable.coins2,R.drawable.coins3,R.drawable.coiins4};
    private coinPurchase listner;

    public CoinsAdapter(Context context, List<SkuDetails> list, coinPurchase listner)
    {
        this.context=context;
        this.list=list;
        this.listner=listner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterCoinsBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_coins,parent,false);
        return new CoinsAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.e("title",list.get(position).getTitle());
        holder.binding.coinIv.setImageResource(coins[position]);
        holder.binding.amountText.setText(list.get(position).getPrice());
        holder.binding.coinsText.setText(list.get(position).getTitle().split("(LikeWise - Chat Guessing Game)")[0].trim().replace("(", ""));
    }

    @Override
    public int getItemCount() { return list!=null?list.size():0; }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdapterCoinsBinding binding;
        public MyViewHolder(AdapterCoinsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            CommonUtils.showAnimation(binding.mainCl);
            binding.mainCl.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.mainCl:
                if(listner!=null) listner.onPurchase(list.get(getAdapterPosition()));
                break;
            }
        }
    }

    public interface coinPurchase{
        void onPurchase(SkuDetails model);
    }
}
