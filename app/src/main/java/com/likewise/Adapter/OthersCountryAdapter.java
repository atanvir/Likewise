package com.likewise.Adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.likewise.BottomSheet.AddLangaugeBottomSheet;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.SelectCountry;
import com.likewise.databinding.AdapterCountryBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hari.bounceview.BounceView;

public class OthersCountryAdapter extends RecyclerView.Adapter<OthersCountryAdapter.MyViewHolder> {

    private Context context;
    private List<ResponseBean> list;
    private String cameFrom;
    private LagaugeAdded listner;

    public OthersCountryAdapter(Context context, List<ResponseBean> list, String cameFrom, LagaugeAdded listner)
    {
        this.context=context;
        this.list=list;
        this.cameFrom=cameFrom;
        this.listner=listner;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       AdapterCountryBinding binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_country,parent,false);
       return new OthersCountryAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageGlider.setRoundImage(context, holder.binding.countryIv, holder.binding.progressBar, list.get(position).getPicture());
        holder.binding.tvCountry.setText(list.get(position).getLanguage()+" ("+list.get(position).getDescription()+")");
        if(list.get(position).isChecked())
        {
            holder.binding.ivSelect.setVisibility(View.VISIBLE);
        }else {
            holder.binding.ivSelect.setVisibility(View.GONE);
        }
        if(list.get(position).isChecked()) { holder.binding.mainCl.setBackgroundTintList(context.getColorStateList(R.color.darkPurple));
            holder.binding.tvCountry.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        }
        else { holder.binding.mainCl.setBackgroundTintList(context.getColorStateList(android.R.color.transparent));
            holder.binding.tvCountry.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdapterCountryBinding binding;

        public MyViewHolder(final AdapterCountryBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            BounceView.addAnimTo(binding.mainCl);
            binding.mainCl.setOnClickListener(this);
            binding.mainCl.setOnClickListener(this);
            binding.tvCountry.setOnClickListener(this);
            binding.ivSelect.setOnClickListener(this);

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.mainCl:
                 if(binding.mainCl.getBackgroundTintList()==context.getColorStateList(R.color.darkPurple)) {
                     SelectCountry.getInstance().remove(list.get(getAdapterPosition()));
                     binding.mainCl.setBackgroundTintList(context.getColorStateList(R.color.white));
                     list.get(getAdapterPosition()).setChecked(false);
                     if (cameFrom.equalsIgnoreCase(AddLangaugeBottomSheet.class.getSimpleName())) {
                         String langCode = SharedPreferenceWriter.getInstance(context).getString(SPreferenceKey.LANGUAGE_CODE);
                         if (langCode.contains(",")) {
                             List<String> listCode = Arrays.asList(langCode.split(","));
                             List<String> duplicateList = new ArrayList<>();
                             for (int i = 0; i < listCode.size(); i++) {
                                 if (!list.get(getAdapterPosition()).getCode().equalsIgnoreCase(listCode.get(i))) {
                                     duplicateList.add(listCode.get(i));
                                 }
                             }
                             SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.LANGUAGE_CODE, TextUtils.join(",", duplicateList));

                         } else {
                             if (list.get(getAdapterPosition()).getCode().equalsIgnoreCase(langCode)) {
                                 langCode = "";
                                 SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.LANGUAGE_CODE, langCode);
                             }
                         }
                     }
                     notifyItemChanged(getAdapterPosition());
                 }else
                 {
                     SelectCountry.getInstance().add(list.get(getAdapterPosition()));
                     list.get(getAdapterPosition()).setChecked(true);
                     if(cameFrom.equalsIgnoreCase(AddLangaugeBottomSheet.class.getSimpleName()))
                     {
                         if(listner!=null) listner.onItem(list.get(getAdapterPosition()));

                     }
                     notifyItemChanged(getAdapterPosition());

                 }

                break;

                case R.id.tvCountry:
                binding.mainCl.performClick();
                break;

                case R.id.ivSelect:
                binding.mainCl.performClick();
                break;
            }
        }
    }

    public interface LagaugeAdded
    {
        void onItem(ResponseBean bean);

    }




}

