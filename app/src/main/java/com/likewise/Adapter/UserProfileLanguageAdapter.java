package com.likewise.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.Interface.OnItemClickListner;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.Laguage;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.databinding.AdapterSelectLanguageBinding;
import com.likewise.databinding.AdapterSelectLanguageProfileBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hari.bounceview.BounceView;

public class UserProfileLanguageAdapter extends RecyclerView.Adapter<UserProfileLanguageAdapter.MyViewHolder> {
    private Context context;
    private List<? extends Object> countryLists;
    private OnKnownLanguageClick listner;
    int checkPos=-1;
    private String editOtpion;

    public UserProfileLanguageAdapter(Context context, List<? extends Object> countryLists,String editOtpion,OnKnownLanguageClick listner)
    {
      this.context=context;
      this.countryLists=countryLists;
      this.listner=listner;
      this.editOtpion=editOtpion;
      Log.e("editOption", editOtpion);
    }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    AdapterSelectLanguageProfileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_select_language_profile, parent, false);
    return new UserProfileLanguageAdapter.MyViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    if(countryLists.get(position).getClass().getSimpleName().equalsIgnoreCase(CommonModelDataObject.CountryList.class.getSimpleName())){
      holder.binding.countryNameText.setTextColor(ContextCompat.getColor(context, R.color.lightBlack));
      holder.binding.shortNameText.setTextColor(ContextCompat.getColor(context, R.color.lightBlack));
      ImageGlider.setRoundImage(context,holder.binding.countryIv,holder.binding.progressBar,((CommonModelDataObject.CountryList) countryLists.get(position)).getPicture());
      holder.binding.countryNameText.setText(((CommonModelDataObject.CountryList) countryLists.get(position)).getLanguage());
      holder.binding.shortNameText.setText(((CommonModelDataObject.CountryList) countryLists.get(position)).getDescription());
      holder.binding.checkBox.setChecked(((CommonModelDataObject.CountryList) countryLists.get(position)).isChecked());
    }

    }

  @Override
  public int getItemCount() {
    return countryLists!=null?countryLists.size():0;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    AdapterSelectLanguageProfileBinding binding;

    public MyViewHolder(AdapterSelectLanguageProfileBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
      CommonUtils.showAnimation(binding.mainCl);
      BounceView.addAnimTo(binding.mainCl);
      BounceView.addAnimTo(binding.countryIv);
      binding.checkBox.setOnClickListener(this);
      binding.mainCl.setOnClickListener(this);
      binding.countryIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {


        case R.id.mainCl:
        binding.countryIv.performClick();
        break;

        case R.id.countryIv:
        if (editOtpion.equalsIgnoreCase("Save")) {
          if (listner != null) {
            for (int i = 0; i < countryLists.size(); i++) {
              ((CommonModelDataObject.CountryList) countryLists.get(getAdapterPosition())).setChecked(false);
            }
            ((CommonModelDataObject.CountryList) countryLists.get(getAdapterPosition())).setChecked(true);
            listner.onDefaultLange(((CommonModelDataObject.CountryList) countryLists.get(getAdapterPosition())).getCode());
            notifyDataSetChanged();
          }
        }
        break;

        case R.id.checkBox:
        if (editOtpion.equalsIgnoreCase("Save")) {
            if (!((CommonModelDataObject.CountryList) countryLists.get(getAdapterPosition())).isChecked()) {
              List<String> langCodeList = new ArrayList<>(Arrays.asList(SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.LANGUAGE_CODE).split(",")));
              for (int i = 0; i < langCodeList.size(); i++) {

                langCodeList.remove(i);
                countryLists.remove(getAdapterPosition());
                break;

              }
              SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.LANGUAGE_CODE, TextUtils.join(",", langCodeList));
              notifyDataSetChanged();
            }
          } else {
            if (binding.checkBox.isChecked()) {
              binding.checkBox.setChecked(false);
            }
          }

      }
    }

  }

    public interface OnKnownLanguageClick
    {
      void onDefaultLange(String code);
    }


}
