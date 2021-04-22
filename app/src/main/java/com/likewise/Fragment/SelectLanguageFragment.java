package com.likewise.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.likewise.Activity.SelectLanguageActivity;
import com.likewise.Adapter.OthersCountryAdapter;
import com.likewise.Adapter.PopularCountryAdapter;
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
import com.likewise.databinding.FragmentSelectLangaugeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectLanguageFragment extends Fragment {

    private List<ResponseBean> popularList = new ArrayList<>();
    private List<ResponseBean> otherList = new ArrayList<>();
    FragmentSelectLangaugeBinding binding;
    int position;


    public SelectLanguageFragment(int position)
    {
        this.position=position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_select_langauge,container,false);
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
                ServicesInterface anInterface= ServicesConnection.getInstance().createService(getActivity());
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
        otherList.clear();
        popularList.clear();
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


        if(position==0) {
            Collections.sort(popularList, new CountrySort());
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerView.setAdapter(new PopularCountryAdapter(getActivity(), popularList, SelectLanguageActivity.class.getSimpleName(), null));
            binding.recyclerView.getAdapter().notifyDataSetChanged();
        }
        if(position==1) {
            if (otherList.size() > 0) {
                Collections.sort(otherList, new CountrySort());
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.recyclerView.setAdapter(new OthersCountryAdapter(getActivity(), otherList, SelectLanguageActivity.class.getSimpleName(), null));
                binding.recyclerView.getAdapter().notifyDataSetChanged();
            }
        }


//        binding.okBtn.setOnClickListener(this);

    }

    public static class CountrySort implements Comparator<ResponseBean>
    {
        @Override
        public int compare(ResponseBean o1, ResponseBean o2) {
            return o1.getLanguage().compareTo(o2.getLanguage());
        }
    }

}
