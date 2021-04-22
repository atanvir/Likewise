package com.likewise.BottomSheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.likewise.Activity.GetCoinsActivity;
import com.likewise.R;
import com.likewise.Utility.CommonUtils;
import com.likewise.databinding.BottomSheetInsufficientCoinsBinding;

import hari.bounceview.BounceView;

public class InsufficientCoinsBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    BottomSheetInsufficientCoinsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_insufficient_coins, container, false);
        CommonUtils.makeMeBlink(binding.btnViewChat,getActivity().getResources().getDrawable(R.drawable.drawable_red_corners3),800);
        BounceView.addAnimTo(binding.btnViewChat);
        binding.btnViewChat.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnViewChat:
            dismiss();
            Intent intent = new Intent(getActivity(), GetCoinsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            break;
        }
    }
}
