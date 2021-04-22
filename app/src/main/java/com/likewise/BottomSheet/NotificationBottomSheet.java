package com.likewise.BottomSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.likewise.Activity.ChatActivity;
import com.likewise.Adapter.NotificationAdapter;
import com.likewise.Interface.SheetItemClickListner;
import com.likewise.Interface.SocketCallbacks;
import com.likewise.Model.ResponseBean;
import com.likewise.Model.SocketModel;
import com.likewise.R;
import com.likewise.SocketHelper.SocketConnection;
import com.likewise.databinding.BottomSheetLoginFirstBinding;
import com.likewise.databinding.BottomSheetNotificationBinding;

import io.socket.client.Socket;


public class NotificationBottomSheet extends BottomSheetDialogFragment  {

    private BottomSheetNotificationBinding binding;
    private ResponseBean model;
    private Socket socket;

    public NotificationBottomSheet(ResponseBean model, Socket socket)
    {
        this.model=model;
        this.socket=socket;
        if(!socket.connected()) {
            socket.connect();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_notification,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(model.getList().size()>0) {
            binding.lottie.setVisibility(View.GONE);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerView.setAdapter(new NotificationAdapter(getActivity(), model.getList(), socket));
        }else {
            binding.lottie.setVisibility(View.VISIBLE);
        }
     }



}
