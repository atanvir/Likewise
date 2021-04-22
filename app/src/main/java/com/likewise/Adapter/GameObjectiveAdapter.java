package com.likewise.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Model.Objective;
import com.likewise.Interface.OnItemClickListner;
import com.likewise.R;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.databinding.AdapterGameObjectiveBinding;

import java.util.List;

import hari.bounceview.BounceView;

public class GameObjectiveAdapter extends RecyclerView.Adapter<GameObjectiveAdapter.MyViewHolder> {
    private Context context;
    private List<Objective> objectiveList;
    private OnItemClickListner listner;


    public GameObjectiveAdapter(Context context, List<Objective> objectiveList, OnItemClickListner listner) {
        this.context = context;
        this.objectiveList = objectiveList;
        this.listner=listner;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterGameObjectiveBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_game_objective, parent, false);
        return new GameObjectiveAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(objectiveList.get(position).isChecked())
        {  for (int i = 0; i < objectiveList.get(position).getPicture().size(); i++) {
            if (objectiveList.get(position).getPicture().get(i).getType().equalsIgnoreCase("selected")) {
                Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.a)).getBitmap();
                holder.binding.ivmainContainer.setImageBitmap(bitmap);
                ImageGlider.setRoundImage(context, holder.binding.iv, holder.binding.progresBar, objectiveList.get(position).getPicture().get(i).getPicture());
            }
        }
        }
        else {
            for (int i = 0; i < objectiveList.get(position).getPicture().size(); i++) {
                if (objectiveList.get(position).getPicture().get(i).getType().equalsIgnoreCase("Unselected")) {
                    Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.b)).getBitmap();
                    holder.binding.ivmainContainer.setImageBitmap(bitmap);
                    ImageGlider.setRoundImage(context, holder.binding.iv, holder.binding.progresBar, objectiveList.get(position).getPicture().get(i).getPicture());
                }
            }
        }

        holder.binding.tvobjective.setText(objectiveList.get(position).getExplanation());
    }

    @Override
    public int getItemCount() {
        return objectiveList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AdapterGameObjectiveBinding binding;

        public MyViewHolder(final AdapterGameObjectiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            BounceView.addAnimTo(binding.mainCl);
            CommonUtils.showAnimation(binding.mainCl);
            binding.ivmainContainer.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.ivmainContainer:
                    for(int i=0;i<objectiveList.size();i++)
                    {
                        objectiveList.get(i).setChecked(false);
                    }
                    listner.onObjectiveSelected(objectiveList.get(getAdapterPosition()).getId());
                    objectiveList.get(getAdapterPosition()).setChecked(true);
                    notifyDataSetChanged();
                    break;
            }
        }
    }

}
