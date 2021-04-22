package com.likewise.BottomSheet;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.likewise.Interface.SheetItemClickListner;
import com.likewise.R;
import com.likewise.databinding.BottomSheetFacebookLoginBinding;

public class FacebookBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetFacebookLoginBinding binding;
    private SheetItemClickListner listner;
    private AnimationDrawable animation;
    public FacebookBottomSheet(SheetItemClickListner listner)
    {
        this.listner=listner;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding= DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_facebook_login,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        animation= (AnimationDrawable)binding.instagramCl.getBackground();
        animation.setEnterFadeDuration(500);
        animation.setExitFadeDuration(200);
        animation.start();
        binding.fbCl.setOnClickListener(this);
        binding.instagramCl.setOnClickListener(this);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        animation.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fbCl:
            dismiss();
            if(listner!=null) listner.onFacebookLogin();
            break;

            case R.id.instagramCl:
            dismiss();
            if(listner!=null) listner.onInstagramLogin();
            break;
        }
    }



}
