package com.likewise.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.likewise.Activity.InvitationActivity;
import com.likewise.Activity.MainActivity;
import com.likewise.Activity.NoPatnerFoundActivity;
import com.likewise.Activity.SearchingPatner;
import com.likewise.Model.SocketModel;
import com.likewise.R;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ServerTimeCalculator;
import com.likewise.databinding.AdapterCoinsBinding;
import com.likewise.databinding.ItemNotificationsBinding;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import hari.bounceview.BounceView;
import io.socket.client.Socket;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> implements MainActivity.IRequest {

    private Context context;
    private List<SocketModel.List> list;
    private CountDownTimer countDownTimer;
    private Socket socket;
    private int isCheckPostion=-1;
    private Animation animation;

    public NotificationAdapter(Context context, List<SocketModel.List> list, Socket socket)
    {
        this.context=context;
        this.list=list;
        this.socket=socket;
        this.isCheckPostion=-1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationsBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_notifications,parent,false);
        return new NotificationAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       new  ServerTimerCalculation(context,position,holder.binding).execute();
    }


    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }


    private void setTimer(int pos,ItemNotificationsBinding binding, long time) {
        long timer=time;
        try {
            if(timer<0)
            {
                timer=timer+60000;
                timer=25000-timer;
            }
            Log.e("timer", "------------------------------------->>>>>>>>>>>>>>>>>>>>>>"+timer);
        countDownTimer= new CountDownTimer(timer, 1000) {
           @Override
           public void onFinish() {
               if(!(isCheckPostion==pos)) {
                   if (!socket.connected()) {
                       socket.connect();
                   }
                   try {
                       JSONObject object = new JSONObject();
                       object.put("_id", list.get(pos).getId());
                       object.put("sender_id", SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID));
                       object.put("receiver_id", list.get(pos).getUserId());
                       object.put("game_id", list.get(pos).getGameId());
                       object.put("type", list.get(pos).getType());
                       socket.emit("reject", object);
                       Log.e("reject", "yes");
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
           }


           @Override
           public void onTick(long millisUntilFinished) {
               binding.timerText.setVisibility(View.VISIBLE);
               binding.acceptText.setVisibility(View.VISIBLE);
               binding.declineText.setVisibility(View.VISIBLE);
               int sec = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
               animation= CommonUtils.makeMeBlink(binding.acceptText, context.getResources().getDrawable(R.drawable.drawable_red_corners));
               binding.timerText.setText(sec + " secs left");
           }
       }.start();

        } catch (Exception e) {
            System.out.println("NumberFormatException: " + e.getMessage());
        }
    }

    @Override
    public void onDecline(String gameId) {
        int pos=-1;
        for(int i=0;i<list.size();i++)
        {
            if(gameId.equalsIgnoreCase(list.get(i).getGameId()))
            {
                list.get(i).setStatus("4");
                pos=i;
                break;
            }
        }

        notifyItemChanged(pos);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemNotificationsBinding binding;

        public MyViewHolder(ItemNotificationsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            MainActivity.setListner(NotificationAdapter.this);
            BounceView.addAnimTo(binding.acceptText);
            BounceView.addAnimTo(binding.declineText);
            CommonUtils.showAnimation(binding.mainCl);
            binding.acceptText.setOnClickListener(this);
            binding.declineText.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.acceptText:
                    binding.acceptText.setEnabled(false);
                    if(!socket.connected())
                    {
                        socket.connect();
                    }
                    try {
                        JSONObject object = new JSONObject();
                        object.put("_id",list.get(getAdapterPosition()).getId());
                        object.put("sender_id", SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID));
                        object.put("receiver_id", list.get(getAdapterPosition()).getUserId());
                        object.put("game_id", list.get(getAdapterPosition()).getGameId());
                        object.put("type",list.get(getAdapterPosition()).getType());
                        socket.emit("accept",object);
                        if(countDownTimer!=null) {
                            isCheckPostion=getAdapterPosition();
                            countDownTimer.cancel();
                            countDownTimer.onFinish();
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    break;

                case R.id.declineText:
                    if(!socket.connected())
                    {
                        socket.connect();
                    }
                    try {
                        JSONObject object = new JSONObject();
                        object.put("_id",list.get(getAdapterPosition()).getId());
                        object.put("sender_id", SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.ID));
                        object.put("receiver_id", list.get(getAdapterPosition()).getUserId());
                        object.put("game_id", list.get(getAdapterPosition()).getGameId());
                        object.put("type",list.get(getAdapterPosition()).getType());
                        socket.emit("reject",object);
                        Log.e("reject", "yes");
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
        }


    }

    public class ServerTimerCalculation extends AsyncTask<Void, Void ,Long>
    {

        private Context context;
        private int position;
        private ItemNotificationsBinding binding;
        public ServerTimerCalculation(Context context,int position,ItemNotificationsBinding binding)
        {
            this.binding=binding;
            this.position=position;
            this.context=context;
        }

        @Override
        protected Long doInBackground(Void... strings) {
            return ServerTimeCalculator.getTime(list.get(position).getCreatedAt());
        }

        @Override
        protected void onPostExecute(Long s) {
            if(position==0) {
                Log.e("secounds", "" + s);
            }
            ImageGlider.setNormalImage(context,binding.userIv,binding.progressBar,list.get(position).getProfilePic());
            binding.secAgoText.setText(ServerTimeCalculator.getTimeAgo(list.get(position).getCreatedAt()));
            // No Action Yet
            if(list.get(position).getStatus().equalsIgnoreCase("1")) {
                //Time mode
                if (list.get(position).getType().equalsIgnoreCase("1")) {
                    if (s < 31000 ) {
                        setTimer(position,binding, s);
                    } else {
                        if(animation!=null)
                         {
                          animation.cancel();
                         }
                        binding.timerText.setVisibility(View.GONE);
                        binding.acceptText.setVisibility(View.GONE);
                        binding.declineText.setVisibility(View.GONE);
                    }
                }
                //Live Mode
                else if (list.get(position).getType().equalsIgnoreCase("2")) {
                    binding.timerText.setVisibility(View.GONE);
                }
            }

            //Request Accepted
            else if(list.get(position).getStatus().equalsIgnoreCase("2")
                    || list.get(position).getStatus().equalsIgnoreCase("3"))
            {
                binding.timerText.setVisibility(View.GONE);
                binding.acceptText.setVisibility(View.GONE);
                binding.declineText.setVisibility(View.GONE);

            }

            //Request Declined
            else if(list.get(position).getStatus().equalsIgnoreCase("4")
                    || list.get(position).getStatus().equalsIgnoreCase("5"))
            {
                binding.timerText.setVisibility(View.GONE);
                binding.acceptText.setVisibility(View.GONE);
                binding.declineText.setVisibility(View.GONE);
            }

            binding.userNameText.setText(list.get(position).getFullName());

            if(list.get(position).getUsersDetail().size()>0) {
                binding.descText.setText(list.get(position).getUsersDetail().get(0).getAboutus());
            }
            else
            {
                binding.descText.setVisibility(View.GONE);
            }
        }
    }

    }

