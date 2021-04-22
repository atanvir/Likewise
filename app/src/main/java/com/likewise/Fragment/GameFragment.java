package com.likewise.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.likewise.Activity.CreateGameActivity;
import com.likewise.Activity.GameOverActivity;
import com.likewise.Activity.LeaderboardActivity;
import com.likewise.Activity.MainActivity;
import com.likewise.Activity.ViewChatActivity;
import com.likewise.Adapter.GameAdapter;
import com.likewise.Adapter.LeaderBoardAdapter;
import com.likewise.Interface.SocketCallbacks;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.MessageResponse;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.SocketHelper.SocketConnection;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ParamEnum;
import com.likewise.Utility.ServerTimeCalculator;
import com.likewise.databinding.FragmentGamezBinding;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameFragment extends Fragment implements MainActivity.UnReadMessageListner {
    private String type,name;
    private FragmentGamezBinding binding;
    private Socket socket;
    private List<ResponseBean> myGames;

    public GameFragment(Socket socket)
    {
        this.socket=socket;
        if(!socket.connected()) {
            socket.connect();
        }
    }

    public GameFragment()
    {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_gamez,container,false);
        if(getArguments()!=null) {
            type = getArguments().getString("type");
            name = getArguments().getString("name");
        }

        return binding.getRoot();
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(name.equalsIgnoreCase("Current")) getChatUserListApi(true);
        else if(name.equalsIgnoreCase("Completed"))getChatUserListApi(false);
        else if(name.equalsIgnoreCase("All")) myMatchApi("3");
        else if(name.equalsIgnoreCase("Random")) myMatchApi("2");
        else if(name.equalsIgnoreCase("Friends")) myMatchApi("1");
    }

    private void myMatchApi(String gameType) {
    try {
        CommonUtils.showLoadingDialog(getActivity());
        ServicesInterface anInterface = ServicesConnection.getInstance().createService(getActivity());
        Call<CommonModelDataList> call = anInterface.myMatch(gameType);
        ServicesConnection.getInstance().enqueueWithoutRetry(call, getActivity(), true, new Callback<CommonModelDataList>() {
            @Override
            public void onResponse(Call<CommonModelDataList> call, Response<CommonModelDataList> response) {
                if(response.isSuccessful())
                {
                    CommonModelDataList serverResponse=response.body();
                    if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                    {
                        if(serverResponse.getData().size()>0)
                        {
                            CommonUtils.dismissLoadingDialog();
                            myGames=response.body().getData();
                            Collections.sort(myGames, new CustomSorting());
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            GameAdapter adapter = new GameAdapter(getActivity(), type, name, myGames, socket);
                            binding.recyclerView.setAdapter(adapter);
//                            if(!SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("1")) {
//
//                            }
//                            else {
//                                userFriendFacbookApi(serverResponse, false);
//                            }
                        }else
                        {
                            CommonUtils.dismissLoadingDialog();
                            binding.getRoot().findViewById(R.id.emptyBgCl).setVisibility(View.VISIBLE);
                        }

                    }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                    {
                        CommonUtils.dismissLoadingDialog();
                        CommonUtils.showSnackBar(getActivity(),serverResponse.getMessage(),ParamEnum.Failure.theValue());
                    }
                }

            }

            @Override
            public void onFailure(Call<CommonModelDataList> call, Throwable t) {
                Log.e("failure",t.getMessage());
            }
        });
    } catch (Exception e) {
        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}


    private void getChatUserListApi(boolean status) {
        if(CommonUtils.networkConnectionCheck(getActivity())) {
            try {
                CommonUtils.showLoadingDialog(getActivity());
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(getActivity());
                Call<CommonModelDataList> call = anInterface.getChatUserList(status);
                ServicesConnection.getInstance().enqueueWithoutRetry(call, getActivity(), false, new Callback<CommonModelDataList>() {
                    @Override
                    public void onResponse(Call<CommonModelDataList> call, Response<CommonModelDataList> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataList serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                if(serverResponse.getData().size()>0)
                                {
                                    CommonUtils.dismissLoadingDialog();
                                    myGames=response.body().getData();
                                    if(status)
                                    {
                                        MainActivity.setUnreadMessageListner(GameFragment.this);
                                    }
                                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    GameAdapter adapter = new GameAdapter(getActivity(), type, name, myGames, socket);
                                    binding.recyclerView.setAdapter(adapter);
//                                     if(!SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.USER_TYPE).equalsIgnoreCase("1")) {
//
//                                    }else {
//                                        userFriendFacbookApi(serverResponse, status);
//                                    }
                                }else
                                {
                                    CommonUtils.dismissLoadingDialog();
                                    binding.getRoot().findViewById(R.id.emptyBgCl).setVisibility(View.VISIBLE);
                                }

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.dismissLoadingDialog();
                                CommonUtils.showSnackBar(getActivity(),serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModelDataList> call, Throwable t) {
                        Log.e("failure",t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            CommonUtils.showSnackBar(getActivity(),getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    private void userFriendFacbookApi(CommonModelDataList serverResponse, boolean status) {
        if(CommonUtils.networkConnectionCheck(getActivity())) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(getActivity(),"https://graph.facebook.com/v2.3/");
                Call<CommonModelDataList> call = anInterface.userFriendFacbook(SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.SOCIAL_ID));
                ServicesConnection.getInstance().enqueueWithoutRetry(call, getActivity(), false, new Callback<CommonModelDataList>() {
                    @Override
                    public void onResponse(Call<CommonModelDataList> call, Response<CommonModelDataList> response) {
                        CommonUtils.dismissLoadingDialog();
                        if(response.isSuccessful())
                        {
                            myGames=getRealFacebbokFriends(serverResponse.getData(),response.body());
                            if(status)
                            {
                                MainActivity.setUnreadMessageListner(GameFragment.this);
                            }
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                GameAdapter adapter = new GameAdapter(getActivity(), type, name, myGames, socket);
                                binding.recyclerView.setAdapter(adapter);
                                binding.recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModelDataList> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            if(getActivity()!=null) {
                CommonUtils.showSnackBar(getActivity(), getString(R.string.no_internet), ParamEnum.Failure.theValue());
            }
        }
    }

    private List<ResponseBean> getRealFacebbokFriends(List<ResponseBean> data,CommonModelDataList body) {

        for(int l=0;l<data.size();l++)
        {
            data.get(l).setUser_type(""+0);
        }

        for(int i=0;i<body.getData().size();i++)
        {
            for(int j=0;j<data.size();j++) {
                if(data.get(j).getProfilePic().contains(body.getData().get(i).getId()))
                {
                    data.get(j).setUser_type(""+1);
                }
            }
        }



        return data;
    }

    private List<ResponseBean> getLivesModeData(List<ResponseBean> data) {
        List<ResponseBean> list=new ArrayList<>();
        for(int i=0;i<data.size();i++)
        {
            if(data.get(i).getMode().equalsIgnoreCase("2"))
            {
               list.add(data.get(i));
            }
        }

        return list;
    }


    @Override
    public void unReadMessageCount(long msgCount, String roomId) {
        Log.e("msgCount", "-------->>>>>>>>>>" + msgCount);
        int pos = 0;
        for (int i = 0; i < myGames.size(); i++) {
            if (myGames.get(i).getRoom_id().equalsIgnoreCase(roomId)) {
                myGames.get(i).setMessageCount(msgCount);
                pos = i;
                break;
            }
        }
        int finalPos = pos;
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.recyclerView.getAdapter().notifyItemChanged(finalPos);
                }
            });
        }
    }

    public class CustomSorting implements Comparator<ResponseBean> {

        @Override
        public int compare(ResponseBean o1, ResponseBean o2) {
            if(Math.round(o1.getDetails().getPointTotalMatchPersantege())>Math.round(o2.getDetails().getPointTotalMatchPersantege())) return -1;
            else if(Math.round(o1.getDetails().getPointTotalMatchPersantege())<Math.round(o2.getDetails().getPointTotalMatchPersantege())) return +1;
            else return 0;
        }
    }
}

