package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.DropBoxManager;
import android.util.Log;
import android.widget.Toast;

import com.likewise.Adapter.ViewChatAdapter;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.MessageResponse;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.CustomSorting;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityViewChatBinding;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewChatActivity extends AppCompatActivity {

    ActivityViewChatBinding binding;
    Map<Integer, List<MessageResponse>> chatData= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_view_chat);
        chatHistory(getIntent().getStringExtra("room_id"));
    }

    private void chatHistory(String room_id) {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.getChatData(room_id,getIntent().getStringExtra("_id"));
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            { setUpData(serverResponse.getData()); }
                            else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            { CommonUtils.showSnackBar(ViewChatActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue()); }
                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(ViewChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            CommonUtils.showSnackBar(ViewChatActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    public Map<Integer,List<MessageResponse>> setUpData(ResponseBean data) {
        List<MessageResponse> firstCardList = new ArrayList<>();
        List<MessageResponse> secoundCardList = new ArrayList<>();
        List<MessageResponse> thirdCardList = new ArrayList<>();
        List<MessageResponse> fourthCardList = new ArrayList<>();
        List<MessageResponse> fifthCardList = new ArrayList<>();
        List<MessageResponse> sixCardList = new ArrayList<>();

        for(int i=0;i<data.getChatData().size();i++)
        {
            if(data.getChatData().get(i).getCard().equalsIgnoreCase("0"))
            {
                firstCardList.add(data.getChatData().get(i));
                chatData.put(Integer.valueOf(data.getChatData().get(i).getCard()), firstCardList);
            }

            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("1"))
            {
                secoundCardList.add(data.getChatData().get(i));
                chatData.put(Integer.valueOf(data.getChatData().get(i).getCard()), secoundCardList);
            }
            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("2"))
            {
                thirdCardList.add(data.getChatData().get(i));
                chatData.put(Integer.valueOf(data.getChatData().get(i).getCard()), thirdCardList);
            }
            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("3"))
            {
                fourthCardList.add(data.getChatData().get(i));
                chatData.put(Integer.valueOf(data.getChatData().get(i).getCard()), fourthCardList);
            }
            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("4"))
            {
                fifthCardList.add(data.getChatData().get(i));
                chatData.put(Integer.valueOf(data.getChatData().get(i).getCard()), fifthCardList);
            }
            else if(data.getChatData().get(i).getCard().equalsIgnoreCase("5"))
            {
                sixCardList.add(data.getChatData().get(i));
                chatData.put(Integer.valueOf(data.getChatData().get(i).getCard()), sixCardList);
            }
        }

        chatData = new TreeMap(chatData);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvViewChat.setLayoutManager(manager);
        binding.rvViewChat.setAdapter(new ViewChatAdapter(ViewChatActivity.this,chatData,data.getReceiverDetails(),data.getReceiver()));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.rvViewChat);
        return chatData;
    }
}
