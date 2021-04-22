package com.likewise.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Activity.CreateGameActivity;
import com.likewise.Model.FbFriend;
import com.likewise.Interface.OnItemClickListner;
import com.likewise.R;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.databinding.AdapterFacebookFriendsBinding;

import java.util.ArrayList;
import java.util.List;

public class FacebookFriendsAdapter extends RecyclerView.Adapter<FacebookFriendsAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<FbFriend> beanList;
    public FilterClass filterClass;
    private OnItemClickListner listner;
    private int checkPos=-1;

    public FacebookFriendsAdapter(Context context, List<FbFriend> beanList, OnItemClickListner listner)
    {
        this.context=context;
        this.beanList=beanList;
        this.listner=listner;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterFacebookFriendsBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_facebook_friends,parent,false);
        return new FacebookFriendsAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageGlider.setRoundImage(context,holder.binding.userIv,holder.binding.progressBar44,beanList.get(position).getProfilePic());
        holder.binding.nameText.setText(beanList.get(position).getName());
        if(beanList.get(position).isChecked()) holder.binding.checkBox.setVisibility(View.VISIBLE);
        else holder.binding.checkBox.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return beanList!=null?beanList.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if(filterClass==null)
        {
            filterClass=new FilterClass();
        }
        return filterClass;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdapterFacebookFriendsBinding binding;

        public MyViewHolder(AdapterFacebookFriendsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            CommonUtils.showAnimation(binding.mainCl);
            binding.mainCl.setOnClickListener(this);
            binding.userIv.setOnClickListener(this);
            binding.checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.mainCl:
                    for(int i=0;i<beanList.size();i++)
                    {
                        beanList.get(i).setChecked(false);
                    }
                    checkPos=getAdapterPosition();
                    beanList.get(getAdapterPosition()).setChecked(true);
                    listner.onSocialPatnerSelected(beanList.get(getAdapterPosition()).getId());
                    notifyDataSetChanged();
                    break;

                case R.id.userIv:
                binding.mainCl.performClick();
                break;

                case R.id.checkBox:
                    if(checkPos!=getAdapterPosition()) {
//                        beanList.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }else
                    {
                        checkPos=getAdapterPosition();
                        beanList.get(getAdapterPosition()).setChecked(true);
                        listner.onSocialPatnerSelected(beanList.get(getAdapterPosition()).getId());
                        notifyDataSetChanged();

                    }
                break;
            }
        }
    }





    public class FilterClass extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            List<FbFriend> filterList= new ArrayList<>(constraint.toString().length());
            for(FbFriend bean: beanList)
            {
                if(bean.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                {
                    filterList.add(bean);
                }

            }

                results.count=filterList.size();
                results.values=filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
           beanList= (List<FbFriend>) results.values;
           notifyDataSetChanged();
        }
    }

}
