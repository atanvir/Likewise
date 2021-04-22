package com.likewise.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.likewise.Activity.ChatActivity;
import com.likewise.Activity.MainActivity;
import com.likewise.Activity.OtherUserProfileActivity;
import com.likewise.Activity.UserProfileActivity;
import com.likewise.Activity.ViewChatActivity;
import com.likewise.BottomSheet.RevealChatBottomSheet;
import com.likewise.Fragment.GameFragment;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ParamEnum;
import com.likewise.Utility.ServerTimeCalculator;
import com.likewise.databinding.LayoutMatchedGameBinding;
import com.likewise.databinding.LayoutUnmatchedGameBinding;

import org.json.JSONObject;

import java.util.List;

import hari.bounceview.BounceView;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.MyViewHolder> {
    private Context context;
    private String type;
    String cameFrom;
    private List<ResponseBean> list;
    private Socket socket;
    final int MATCHED=1,UNMATCHED=0;

    public GameAdapter(Context context, String type, String cameFrom, List<ResponseBean> list, Socket socket)
    {
        this.context=context;
        this.type=type;
        this.list=list;
        this.cameFrom=cameFrom;
        this.socket=socket;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        if(type.equalsIgnoreCase("Matched")) return MATCHED;
        else return UNMATCHED;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MATCHED) return new GameAdapter.MyViewHolder((LayoutMatchedGameBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_matched_game,parent,false));
        else return new GameAdapter.MyViewHolder((LayoutUnmatchedGameBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_unmatched_game,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        switch (holder.getItemViewType())
        {
            case MATCHED:
                switch (cameFrom)
                {
                    case "All": setMatchView(holder,position); break;
                    case "Random": setMatchView(holder,position); break;
                    case "Friends": setMatchView(holder,position); break;
                }
                break;


            case UNMATCHED:
             switch (cameFrom)
             {
                 case "Current": setUnMatchedView(holder,position,"Current"); break;
                 case "Completed": setUnMatchedView(holder,position,"Completed"); break;
             }
             break;

        }
    }


    private void setMatchView(MyViewHolder holder, int position) {
        ImageGlider.setRoundImage(context,holder.matchedGameBinding.userIv,holder.matchedGameBinding.progressBar,list.get(position).getProfilePic());
        if(list.get(position).getUser_type()!=null) {
            if (list.get(position).getUser_type().equalsIgnoreCase("1")) {
                holder.matchedGameBinding.nameText.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.fb_home), null);
            }
        }
        holder.matchedGameBinding.nameText.setText(list.get(position).getName());
        new CommonUtils().setProgressbar(holder.matchedGameBinding.ProgressBar, holder.matchedGameBinding.progressPercText, (int) Math.round(list.get(position).getDetails().getPointTotalMatchPersantege()));
        holder.matchedGameBinding.commonText.setText(list.get(position).getDetails().getCount()+" games in common");
    }


    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }


    private void setUnMatchedView(MyViewHolder holder, int position,String tab) {
        if(list.get(position).getMessageCount()>0)
        {
            if(!tab.equalsIgnoreCase("Completed")) {
                holder.unmatchedGameBinding.tvUnreadMessageCount.setText("" + list.get(position).getMessageCount());
                holder.unmatchedGameBinding.tvUnreadMessageCount.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.unmatchedGameBinding.unMatchCL.setElevation(15f);
                }
            }
        }

        if (list.get(position).getSender_id().equalsIgnoreCase(SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID)))
        {
            holder.unmatchedGameBinding.requestIv.setImageResource(R.drawable.blue);
        }else
        {
            holder.unmatchedGameBinding.requestIv.setImageResource(R.drawable.yellow);
        }

        ImageGlider.setRoundImage(context,holder.unmatchedGameBinding.userIv,holder.unmatchedGameBinding.progressBar,list.get(position).getProfilePic());
        if(list.get(position).getPointDetails().getAll_LikeWise_Persantege()==0)
        {
            holder.unmatchedGameBinding.ProgressBar.setProgress(0);
            holder.unmatchedGameBinding.progressPercText.setText("0");
        }else {
            new CommonUtils().setProgressbar(holder.unmatchedGameBinding.ProgressBar, holder.unmatchedGameBinding.progressPercText, (int) Math.round(list.get(position).getPointDetails().getAll_LikeWise_Persantege()));
        }
        holder.unmatchedGameBinding.userNameText.setText(list.get(position).getName());
        if(list.get(position).getUser_type()!=null) {
            if (list.get(position).getUser_type().equalsIgnoreCase("1")) {
                holder.unmatchedGameBinding.userNameText.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.fb_home), null);
            }
        }
        holder.unmatchedGameBinding.tvObjective.setText(list.get(position).getObjectives());
        holder.unmatchedGameBinding.tvMorePreciously.setText(list.get(position).getMorePreciselys());
        Glide.with(context).load(list.get(position).getLanguageDetails().getPicture()).into(holder.unmatchedGameBinding.countryIv);
        holder.unmatchedGameBinding.tvcountryName.setText(list.get(position).getLanguageDetails().getLanguage());
        if(list.get(position).getMode().equalsIgnoreCase("1"))
        {
            holder.unmatchedGameBinding.ivMode.setImageResource(R.drawable.time);
            holder.unmatchedGameBinding.tvMode.setText("Rush");

        }else if(list.get(position).getMode().equalsIgnoreCase("2"))
        {
            holder.unmatchedGameBinding.ivMode.setImageResource(R.drawable.life);
            holder.unmatchedGameBinding.tvMode.setText("Relax");
        }
        holder.unmatchedGameBinding.pointsText.setText(""+list.get(position).getPointDetails().getFollowerDetail().get(0).getPointMax());
        holder.unmatchedGameBinding.matchText.setText(""+Math.round(list.get(position).getPointDetails().getLikeWisePersantege()));
        if(list.get(position).getTimeUpdate()!=null) {
            holder.unmatchedGameBinding.timeText.setText(ServerTimeCalculator.getTimeAgo(list.get(position).getTimeUpdate()));
        }
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,MainActivity.coinDeducted {
        // match
        LayoutMatchedGameBinding matchedGameBinding;
        public MyViewHolder(LayoutMatchedGameBinding binding) {
            super(binding.getRoot());
            this.matchedGameBinding=binding;
            CommonUtils.showAnimation(binding.matchCl);
            BounceView.addAnimTo(binding.matchCl);
            matchedGameBinding.matchCl.setOnClickListener(this);
        }

        //un match
        LayoutUnmatchedGameBinding unmatchedGameBinding;
        public MyViewHolder(LayoutUnmatchedGameBinding binding) {
            super(binding.getRoot());
            this.unmatchedGameBinding=binding;
            CommonUtils.showAnimation(binding.unMatchCL);
            BounceView.addAnimTo(binding.unMatchCL);
            BounceView.addAnimTo(binding.userIv);
            unmatchedGameBinding.unMatchCL.setOnClickListener(this);
            unmatchedGameBinding.userIv.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.unMatchCL:
                switch (cameFrom)
                 {
                   case "Current":
                   intentUnMatch(ChatActivity.class,getAdapterPosition()); break;

                   case "Completed":
                   MainActivity.setListnerCoin(this);
                   checkCoinDeductApi(getAdapterPosition());
                   break;
                 }
                 break;

                case R.id.matchCl:
                intentMatch(OtherUserProfileActivity.class,getAdapterPosition());
                break;


                case R.id.userIv:
                intentUnMatch(OtherUserProfileActivity.class,getAdapterPosition());
                break;
            }
        }

        @Override
        public void onDeduct() {
            intentUnMatch(ViewChatActivity.class,getAdapterPosition());
        }
    }
    private void checkCoinDeductApi(int pos) {
        try {
            ServicesInterface anInterface = ServicesConnection.getInstance().createService(context);
            Call<CommonModelDataObject> call = anInterface.checkCoinDeduct(SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID), list.get(pos).getGame_id());
            ServicesConnection.getInstance().enqueueWithoutRetry(call, ((Activity) context), true, new Callback<CommonModelDataObject>() {
                @Override
                public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                    if (response.isSuccessful()) {
                        CommonModelDataObject serverResponse = response.body();
                        if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue())) {
                            if(serverResponse.getData().getStatus().equalsIgnoreCase("true"))
                            {
                                CommonUtils.playSound("press_button", context);
                                RevealChatBottomSheet sheet = new RevealChatBottomSheet(socket,list.get(pos).getName(),list.get(pos).getGame_id());
                                sheet.show(((FragmentActivity)context).getSupportFragmentManager(),"");
                            }else
                            {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put("_id",SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID));
                                    object.put("game_id",list.get(pos).getGame_id());
                                    socket.emit("coin_dedicated",object);
                                    Log.e("coin_dedicated", object.toString());
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }


                            }


                      } else if (serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue())) {

                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                    Log.e("failure", t.getMessage());

                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void intentMatch(Class<? extends Object> className, int pos) {
        Intent intent = new Intent(context,className);
        String receiverId=list.get(pos).getReceiver_id();
        if(!list.get(pos).getSender_id().equalsIgnoreCase(SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID)))
        {
            receiverId=list.get(pos).getSender_id();
        }
        intent.putExtra("_id",receiverId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void intentUnMatch(Class<? extends Object> className,int pos) {
        Intent intent = new Intent(context,className);
        intent.putExtra("cameFrom",context.getClass().getSimpleName());
        intent.putExtra(ParamEnum.SENDER_ID.theValue(),list.get(pos).getSender_id());
        intent.putExtra(ParamEnum.RECIVER_ID.theValue(),list.get(pos).getReceiver_id());
        intent.putExtra(ParamEnum.ROOM_ID.theValue(),list.get(pos).getRoom_id());
        intent.putExtra(ParamEnum.GAME_ID.theValue(),list.get(pos).getGame_id());
        intent.putExtra(ParamEnum.MODE.theValue(),list.get(pos).getMode());
        intent.putExtra(ParamEnum.CURRENT_CARD.theValue(),list.get(pos).getCurrent_card());
        intent.putExtra(ParamEnum.POINTS.theValue(),list.get(pos).getPointDetails().getFollowerDetail().get(0).getPointMax());
        intent.putExtra(ParamEnum.LIVES.theValue(),list.get(pos).getLives());
        String receiverId=list.get(pos).getReceiver_id();
        if(!list.get(pos).getSender_id().equalsIgnoreCase(SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID)))
        {
            receiverId=list.get(pos).getSender_id();
            intent.putExtra(ParamEnum.RECIVER_ID.theValue(),receiverId);
        }
        intent.putExtra("_id",receiverId);
        Log.e("userId",SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID));
        Log.e("receiver_id",receiverId);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }



}
