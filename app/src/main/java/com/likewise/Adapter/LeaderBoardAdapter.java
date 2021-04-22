package com.likewise.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Activity.OtherUserProfileActivity;
import com.likewise.Model.ResponseBean;
import com.likewise.R;

import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.databinding.AdapterLeaderboardBinding;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.MyViewHolder> {

    private Context context;
    private List<ResponseBean> list = new ArrayList<>();

    public LeaderBoardAdapter(Context context, List<ResponseBean> list)
    {
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterLeaderboardBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_leaderboard,parent,false);
        return new LeaderBoardAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageGlider.setRoundImage(context,holder.binding.userIv,holder.binding.progressBar,list.get(position).getReceiverDetails().getProfilePic());
        if(list.get(position).getReceiverDetails().getType()==1)
        {
            holder.binding.tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,context.getResources().getDrawable(R.drawable.fb_home),null);
        }
        holder.binding.tvName.setText(list.get(position).getReceiverDetails().getName());
        if(list.get(position).getScores()!=null) {
            holder.binding.tvScore.setText("" + list.get(position).getReceiverDetails().getTotalPoints()+" points");
        }else
        {
            holder.binding.tvScore.setText("0 point");
        }

        holder.binding.tvGamesCommon.setText(list.get(position).getGameCounts()+" games");
        holder.binding.tvRank.setText(""+Math.round(Double.parseDouble(list.get(position).getReceiverDetails().getUserValue())));
        if(list.get(position).getLikewisePer()!=0.0)
        {
            holder.binding.progressPercText.setVisibility(View.VISIBLE);
            holder.binding.ProgressBar.setVisibility(View.VISIBLE);
            holder.binding.percentText.setVisibility(View.VISIBLE);
            new CommonUtils().setProgressbar(holder.binding.ProgressBar,holder.binding.progressPercText, (int) Math.round(list.get(position).getLikewisePer()));

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterLeaderboardBinding binding;


        public MyViewHolder(AdapterLeaderboardBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            CommonUtils.showAnimation(binding.mainCl);
            binding.mainCl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Intent intent=new Intent(context, OtherUserProfileActivity.class);
                        intent.putExtra("_id",list.get(getAdapterPosition()).getReceiverDetails().getId());
                        intent.putExtra("rank",list.get(getAdapterPosition()).getReceiverDetails().getUserValue());
                        intent.putExtra("likeWise",list.get(getAdapterPosition()).getLikewisePer());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                }
            });
        }
    }
}
