package com.likewise.BottomSheet;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.likewise.Activity.LoginRegisterActivity;
import com.likewise.Interface.SheetItemClickListner;
import com.likewise.R;
import com.likewise.Utility.InstagramLogin;
import com.likewise.databinding.BottomSheetFacebookLoginBinding;
import com.likewise.databinding.BottomSheetLoginFirstBinding;

public class LoginFirstBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetLoginFirstBinding binding;
    private SheetItemClickListner listner;
    private AnimationDrawable animation;
    public LoginFirstBottomSheet(SheetItemClickListner listner)
    {
        this.listner=listner;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding= DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_login_first,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        animation= (AnimationDrawable)binding.instagramCl.getBackground();
        animation.setEnterFadeDuration(500);
        animation.setExitFadeDuration(200);
        animation.start();
        binding.emailCl.setOnClickListener(this);
        binding.instagramCl.setOnClickListener(this);
        binding.fbCl.setOnClickListener(this);
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
            
            case R.id.emailCl:
            dismiss();
            Intent intent = new Intent(getActivity(), LoginRegisterActivity.class);
            intent.putExtra("isRegister", true);
            startActivity(intent);
            break;

            case R.id.instagramCl:
            dismiss();
            if(listner!=null) listner.onInstagramLogin();
            break;
        }

    }



}
