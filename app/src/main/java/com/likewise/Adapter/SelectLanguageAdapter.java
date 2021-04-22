package com.likewise.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Model.CommonModelDataObject;

import com.likewise.Model.Laguage;
import com.likewise.Interface.OnItemClickListner;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.AdapterSelectLanguageBinding;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import hari.bounceview.BounceView;

public class SelectLanguageAdapter extends RecyclerView.Adapter<SelectLanguageAdapter.MyViewHolder> {
    private Context context;
    private List<? extends Object> countryLists;
    private OnItemClickListner listner;
    int checkPos=-1;

    public SelectLanguageAdapter(Context context,List<? extends Object> countryLists, OnItemClickListner listner)
    {
      this.context=context;
      this.countryLists=countryLists;
      this.listner=listner;
    }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    AdapterSelectLanguageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_select_language, parent, false);
    return new SelectLanguageAdapter.MyViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    //Profile Detail Screen and Edit Profile Screen
    if(countryLists.get(position).getClass().getSimpleName().equalsIgnoreCase(CommonModelDataObject.CountryList.class.getSimpleName()) ||
       countryLists.get(position).getClass().getSimpleName().equalsIgnoreCase(CommonModelDataObject.CountryList.class.getSimpleName()))
    {
      holder.binding.countryNameText.setTextColor(ContextCompat.getColor(context, R.color.lightBlack));
      holder.binding.shortNameText.setTextColor(ContextCompat.getColor(context, R.color.lightBlack));
      ImageGlider.setRoundImage(context,holder.binding.countryIv,holder.binding.progressBar,((CommonModelDataObject.CountryList) countryLists.get(position)).getPicture());
      holder.binding.countryNameText.setText(((CommonModelDataObject.CountryList) countryLists.get(position)).getLanguage());
      holder.binding.shortNameText.setText(((CommonModelDataObject.CountryList) countryLists.get(position)).getDescription());
      if(((CommonModelDataObject.CountryList) countryLists.get(position)).isChecked()) holder.binding.checkBox.setVisibility(View.VISIBLE);
      else holder.binding.checkBox.setVisibility(View.GONE);
    }

    // Create Game Screen
    else if(countryLists.get(position).getClass().getSimpleName().equalsIgnoreCase(Laguage.class.getSimpleName()))
    {
      ImageGlider.setRoundImage(context, holder.binding.countryIv, holder.binding.progressBar, ((Laguage) countryLists.get(position)).getPicture());
      holder.binding.countryNameText.setText(((Laguage) countryLists.get(position)).getLanguage());
      holder.binding.shortNameText.setText(((Laguage) countryLists.get(position)).getDescription());
      if(((Laguage) countryLists.get(position)).isChecked()) holder.binding.checkBox.setVisibility(View.VISIBLE);
      else holder.binding.checkBox.setVisibility(View.GONE);
    }

    //Play Game Screen
    else if(countryLists.get(position).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName()))
    {
      holder.binding.countryNameText.setTextColor(ContextCompat.getColor(context, R.color.lightBlack));
      holder.binding.shortNameText.setTextColor(ContextCompat.getColor(context, R.color.lightBlack));
      holder.binding.countryNameText.setText(((ResponseBean) countryLists.get(position)).getLanguage());
      holder.binding.shortNameText.setText(((ResponseBean) countryLists.get(position)).getDescription());
      ImageGlider.setRoundImage(context, holder.binding.countryIv, holder.binding.progressBar, ((ResponseBean) countryLists.get(position)).getPicture());
      if(!(((ResponseBean) countryLists.get(position)).isEnabled()))
      {
        holder.binding.countryNameText.setTextColor(ContextCompat.getColor(context,R.color.grey));
        holder.binding.shortNameText.setTextColor(ContextCompat.getColor(context,R.color.grey));
        ImageGlider.setRoundImage(context, holder.binding.countryIv, holder.binding.progressBar, ((ResponseBean) countryLists.get(position)).getGreyImage());
      }
      if(((ResponseBean) countryLists.get(position)).isChecked()) holder.binding.checkBox.setVisibility(View.VISIBLE);
      else holder.binding.checkBox.setVisibility(View.GONE);
    }
  }
  @Override
  public int getItemCount() {
    return countryLists!=null?countryLists.size():0;
  }
  @Override
  public int getItemViewType(int position) { return super.getItemViewType(position); }


  public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    AdapterSelectLanguageBinding binding;

    public MyViewHolder(AdapterSelectLanguageBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
      CommonUtils.showAnimation(binding.mainCl);
      BounceView.addAnimTo(binding.mainCl);
      BounceView.addAnimTo(binding.countryIv);
      binding.mainCl.setOnClickListener(this);
      binding.mainCl.setOnLongClickListener(this);
      binding.countryIv.setOnLongClickListener(this);
      binding.countryIv.setOnClickListener(this);
      binding.checkBox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.mainCl:

          //Create Game Screen
          if (countryLists.get(getAdapterPosition()).getClass().getSimpleName().equalsIgnoreCase(Laguage.class.getSimpleName())) {
            for (int i = 0; i < countryLists.size(); i++) {
              ((Laguage) countryLists.get(i)).setChecked(false);
            }
            checkPos = getAdapterPosition();
            ((Laguage) countryLists.get(getAdapterPosition())).setChecked(true);
            listner.onLanguageSelected(((Laguage) countryLists.get(getAdapterPosition())).getCode(), ((Laguage) countryLists.get(getAdapterPosition())).getLanguage(), ((Laguage) countryLists.get(getAdapterPosition())).getDescription(), ((Laguage) countryLists.get(getAdapterPosition())).getPicture());
            notifyDataSetChanged();
          }
          //Play Game Screen
          else if (countryLists.get(getAdapterPosition()).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName())) {
            if (((ResponseBean) countryLists.get(getAdapterPosition())).isEnabled()) {

              for (int i = 0; i < countryLists.size(); i++) {
                ((ResponseBean) countryLists.get(i)).setChecked(false);
              }
              checkPos = getAdapterPosition();
              ((ResponseBean) countryLists.get(getAdapterPosition())).setChecked(true);
              listner.onLanguageSelected(((ResponseBean) countryLists.get(getAdapterPosition())).getCode(), ((ResponseBean) countryLists.get(getAdapterPosition())).getLanguage(), ((ResponseBean) countryLists.get(getAdapterPosition())).getDescription(), ((ResponseBean) countryLists.get(getAdapterPosition())).getPicture());
              notifyDataSetChanged();
            }
            else
            {
              CommonUtils.showSnackBar(context,"No partner available for this language", ParamEnum.Failure.theValue());
            }

          }
          break;

        case R.id.countryIv:
          binding.mainCl.performClick();
          break;

        case R.id.checkBox:
          if (!countryLists.get(getAdapterPosition()).getClass().getSimpleName().equalsIgnoreCase(CommonModelDataObject.CountryList.class.getSimpleName())) {
            if (checkPos != getAdapterPosition()) {
              // countryLists.remove(getAdapterPosition());
              notifyDataSetChanged();
            }
            //Create Game Screen
            else if (countryLists.get(getAdapterPosition()).getClass().getSimpleName().equalsIgnoreCase(Laguage.class.getSimpleName())) {
              checkPos = getAdapterPosition();
              ((Laguage) countryLists.get(getAdapterPosition())).setChecked(true);
              listner.onLanguageSelected(((Laguage) countryLists.get(getAdapterPosition())).getCode(), ((Laguage) countryLists.get(getAdapterPosition())).getLanguage(), ((Laguage) countryLists.get(getAdapterPosition())).getDescription(), ((Laguage) countryLists.get(getAdapterPosition())).getPicture());
              notifyDataSetChanged();
            }
            //Play Game Screen
            else if (countryLists.get(getAdapterPosition()).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName())) {
              checkPos = getAdapterPosition();
              ((ResponseBean) countryLists.get(getAdapterPosition())).setChecked(true);
              listner.onLanguageSelected(((ResponseBean) countryLists.get(getAdapterPosition())).getCode(), ((ResponseBean) countryLists.get(getAdapterPosition())).getLanguage(), ((ResponseBean) countryLists.get(getAdapterPosition())).getDescription(), ((ResponseBean) countryLists.get(getAdapterPosition())).getPicture());
              notifyDataSetChanged();
            }
          }
          break;

      }
    }


    @Override
    public boolean onLongClick(View v) {
      return false;
    }
  }

}
