package com.likewise.BottomSheet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.likewise.Activity.SelectLanguageActivity;
import com.likewise.Adapter.OthersCountryAdapter;
import com.likewise.Adapter.PopularCountryAdapter;
import com.likewise.Fragment.SelectLanguageFragment;
import com.likewise.Model.CommonLanguageModel;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;
import com.likewise.Utility.SelectCountry;
import com.likewise.databinding.BottomSheetAddLanguageBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLangaugeBottomSheet extends BottomSheetDialogFragment implements PopularCountryAdapter.LagaugeAdded, OthersCountryAdapter.LagaugeAdded {
    private BottomSheetAddLanguageBinding binding;
    private List<ResponseBean> popularList = new ArrayList<>();
    private List<ResponseBean> otherList = new ArrayList<>();
    private OnLangaugeSelected listner;
    public AddLangaugeBottomSheet(OnLangaugeSelected listner)
    {
        this.listner=listner;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding= DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_add_language,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SelectCountry.removeAll(SelectCountry.list);
        languagesApi();

    }

    private void languagesApi() {
        if(CommonUtils.networkConnectionCheck(getActivity()))
        {
            try {
                ServicesInterface anInterface=ServicesConnection.getInstance().createService(getActivity());
                Call<CommonLanguageModel> call=anInterface.listLanguage(SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.LANGUAGE_CODE));
                ServicesConnection.getInstance().enqueueWithoutRetry(call, getActivity(), true, new Callback<CommonLanguageModel>() {
                    @Override
                    public void onResponse(Call<CommonLanguageModel> call, Response<CommonLanguageModel> response) {
                        if(response.isSuccessful())
                        {
                            CommonLanguageModel serverResponse = response.body();
                            setUpRecycleView(serverResponse.getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonLanguageModel> call, Throwable t) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


        }else
        {
            CommonUtils.showSnackBar(getActivity(),getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }
    private void setUpRecycleView(List<ResponseBean> data) {
       List<String> langCodes=new ArrayList<>(Arrays.asList(SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.LANGUAGE_CODE).split(",")));

        for(int i=0;i<data.size();i++)
        {
            for(int j=0;j<langCodes.size();j++)
            {
                if(data.get(i).getCode().equalsIgnoreCase(langCodes.get(j)) && !data.get(i).isChecked())
                {
                    data.get(i).setChecked(true);
                    SelectCountry.add(data);
                }
            }
        }


        for(int i=0 ; i<data.size(); i++)
        {
            if(data.get(i).getStatus().equalsIgnoreCase("0"))
            {
                otherList.add(data.get(i));

            }else if(data.get(i).getStatus().equalsIgnoreCase("1"))
            {
                popularList.add(data.get(i));
            }
        }
        Collections.sort(popularList,new SelectLanguageFragment.CountrySort());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(new PopularCountryAdapter(getActivity(),popularList,AddLangaugeBottomSheet.class.getSimpleName(),this));
        Collections.sort(otherList,new SelectLanguageFragment.CountrySort());
        binding.otherRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.otherRV.setAdapter(new OthersCountryAdapter(getActivity(),otherList,AddLangaugeBottomSheet.class.getSimpleName(),this));
    }

    @Override
    public void onItem(ResponseBean bean) {
        List<String> langauageCode=new ArrayList<>(Arrays.asList(SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.LANGUAGE_CODE).split(",")));
        if(!langauageCode.contains(bean.getCode())) {
            langauageCode.add(bean.getCode());
            SharedPreferenceWriter.getInstance(getActivity()).writeStringValue(SPreferenceKey.LANGUAGE_CODE, TextUtils.join(",", langauageCode));
        }

        if(listner!=null) listner.onLanguageAdded();

    }

    public interface OnLangaugeSelected
    {
       void onLanguageAdded();
    }

    
}


