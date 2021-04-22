package com.likewise.BottomSheet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.likewise.Activity.SelectLanguageActivity;
import com.likewise.Adapter.AddImagesAdapter;
import com.likewise.Adapter.NationalityAdapter;
import com.likewise.Adapter.PublicGalleryAdapter;
import com.likewise.Interface.OnItemClickListner;
import com.likewise.Model.CommonLanguageModel;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.GalleryModel;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.AllImagesSingleton;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.BottomSheetAddImagesBinding;
import com.likewise.databinding.BottomSheetCountryBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryBottomSheet extends BottomSheetDialogFragment implements NationalityAdapter.OnItemClick {
    private BottomSheetCountryBinding binding;
    private OnNationalityClick listner;
    private NationalityAdapter adapter;
    public CountryBottomSheet(OnNationalityClick listner)
    {
        this.listner=listner;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding= DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_country,container,false);
        languagesApi();
       return binding.getRoot();
    }




    private void languagesApi() {
        try {
            ServicesInterface anInterface=ServicesConnection.getInstance().createService(getActivity());
            Call<CommonLanguageModel> call=anInterface.listLanguage(SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.LANGUAGE_CODE));
            ServicesConnection.getInstance().enqueueWithoutRetry(call, getActivity(), true, new Callback<CommonLanguageModel>() {
                @Override
                public void onResponse(Call<CommonLanguageModel> call, Response<CommonLanguageModel> response) {
                    if(response.isSuccessful())
                    {
                        CommonLanguageModel serverResponse = response.body();
                        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        binding.rvCountry.setLayoutManager(manager);
                        adapter= new NationalityAdapter(getActivity(),serverResponse.getData(),CountryBottomSheet.this);
                        binding.rvCountry.setAdapter(adapter);

                    }

                }

                @Override
                public void onFailure(Call<CommonLanguageModel> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(String countryImage, String countryName) {
        if(listner!=null) listner.onNationality(countryImage,countryName);
        dismiss();

    }


    public interface OnNationalityClick{
        void onNationality(String countryImage,String name);
    }
}


