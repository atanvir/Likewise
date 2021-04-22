package com.likewise.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.likewise.Model.MessageResponse;
import com.likewise.Model.ReceiverDetails;
import com.likewise.R;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.ImageGlider;
import com.likewise.databinding.AdapterViewChatBinding;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewChatAdapter extends RecyclerView.Adapter<ViewChatAdapter.MyViewHolder> {
    //Toolbar
    ImageView ivprofilePic,ivBack;
    CircleImageView ivcountry;
    TextView tvName,tvcoin,tvlangCode,tvPoints;
    ProgressBar progressBarCountryIv,progressBar;

    private Context context;
    private Map<Integer, List<MessageResponse>> chatData;
    private ReceiverDetails receiverDetails;
    private ReceiverDetails details;


    public ViewChatAdapter(Context context, Map<Integer, List<MessageResponse>> chatData,ReceiverDetails receiverDetails,ReceiverDetails details)
    {
        this.context=context;
        this.receiverDetails=receiverDetails;
        this.chatData=chatData;
        this.details=details;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterViewChatBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_view_chat,viewGroup,false);
        return new ViewChatAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        if(details.getProfilePic()!=null) {
            ImageGlider.setRoundImage(context, ivprofilePic, progressBar, details.getProfilePic());
        }
        if(details.getName()!=null)
        {
            if(details.getName().contains(" ")) tvName.setText(details.getName().split(" ")[0]);
            else tvName.setText(details.getName().trim());
        }
        tvcoin.setText(SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.COINS));
        tvPoints.setText("" + getCardWisePoints(chatData.get(chatData.keySet().toArray()[i])));
        Glide.with(context).load(receiverDetails.getPicture()).into(ivcountry);
        if(receiverDetails.getCode().contains("-"))
        {
            tvlangCode.setText(receiverDetails.getCode().split("-")[0]);
        }else
        {
            tvlangCode.setText(receiverDetails.getCode());
        }
        Glide.with(context).load(chatData.get(chatData.keySet().toArray()[i]).get(0).getImage()).into(holder.binding.ivBackground);

        holder.binding.chatRv.setLayoutManager(new LinearLayoutManager(context));
        holder.binding.chatRv.setAdapter(new ChatAdapter(context,chatData.get(chatData.keySet().toArray()[i])));
        try {
            holder.binding.chatRv.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.binding.chatRv.scheduleLayoutAnimation();
    }


    private long getCardWisePoints(List<MessageResponse> list) {
        long max = 0;
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).getPoint()>max)
            {
                max=list.get(i).getPoint();
            }
        }

        return max;

    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdapterViewChatBinding binding;
        public MyViewHolder(@NonNull AdapterViewChatBinding view) {
            super(view.getRoot());
            this.binding=view;
            init(binding.getRoot());
            ivBack.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.ivBack:
                    ((Activity)context).onBackPressed();
                    break;
            }
        }
    }

    private void init(View view) {
        //Toolbar
        ivprofilePic=view.findViewById(R.id.ivprofilePic);
        tvName=view.findViewById(R.id.tvName);
        tvcoin=view.findViewById(R.id.tvcoin);
        tvPoints=view.findViewById(R.id.tvPoints);
        ivcountry=view.findViewById(R.id.ivcountry);
        tvlangCode=view.findViewById(R.id.tvlangCode);
        progressBar=view.findViewById(R.id.progressBar);
        ivBack=view.findViewById(R.id.ivBack);
        progressBarCountryIv=view.findViewById(R.id.progressBarCountryIv);
    }
}
