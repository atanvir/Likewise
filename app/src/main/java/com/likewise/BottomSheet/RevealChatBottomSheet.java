package com.likewise.BottomSheet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.likewise.Activity.GameOverActivity;
import com.likewise.Activity.GetCoinsActivity;
import com.likewise.Adapter.CoinsAdapter;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.BottomSheetRevealChatBinding;

import org.json.JSONObject;

import hari.bounceview.BounceView;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevealChatBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    BottomSheetRevealChatBinding binding;
    private Socket socket;
    private String receiverName,game_id;
    int revealCoins;

    public RevealChatBottomSheet(Socket socket,String receverName,String game_id)
    {
       this.socket=socket;
       this.receiverName=receverName;
       this.game_id=game_id;
       if(!socket.connected()) socket.connect();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_reveal_chat,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        BounceView.addAnimTo(binding.btnViewChat);
        binding.btnViewChat.setOnClickListener(this);
        getRevealChat();
    }

    private void getRevealChat()  {
        if(CommonUtils.networkConnectionCheck(getActivity())) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(getActivity());
                Call<CommonModelDataObject> call = anInterface.getCoins();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, getActivity(), true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                revealCoins=serverResponse.getData().getCoinModel().get(0).getSpeedToRevealChat();
                                binding.btnViewChat.setText("Spend "+serverResponse.getData().getCoinModel().get(0).getSpeedToRevealChat()+" coins to view "+receiverName+"\'s chat");

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(getActivity(),serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnViewChat:
                dismiss();
               int currentCoins= Integer.parseInt(SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.COINS));
               if(!(currentCoins>=revealCoins))
               {
                   InsufficientCoinsBottomSheet sheet = new InsufficientCoinsBottomSheet();
                   sheet.show(getActivity().getSupportFragmentManager(),"");

               }else {
                   CommonUtils.playSound("reveal_chat", getActivity());
                   try {
                       JSONObject object = new JSONObject();
                       object.put("_id", SharedPreferenceWriter.getInstance(getActivity()).getString(SPreferenceKey.ID));
                       object.put("game_id", game_id);
                       socket.emit("coin_dedicated", object);

                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }

                break;
        }
    }
}
