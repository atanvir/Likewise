package com.likewise.Fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.os.IResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.likewise.Model.SignupModel;
import com.likewise.R;
import com.likewise.Utility.CustomTextWatcher;
import com.likewise.Utility.ILoginRegister;
import com.likewise.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment implements View.OnFocusChangeListener, CustomTextWatcher.TextWatcherWithInstance, View.OnClickListener {

    FragmentRegisterBinding binding;
    private SignupModel model =new SignupModel();
    private ILoginRegister listner;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_register,container,false);
        binding.ivShowPassword.setOnClickListener(this);
        init();

        return binding.getRoot();
    }

    private void init() {
        binding.fullNameEd.setOnFocusChangeListener(this);
        binding.emailEd.setOnFocusChangeListener(this);
        binding.userNameEd.setOnFocusChangeListener(this);
        binding.passEd.setOnFocusChangeListener(this);

        new CustomTextWatcher().registerEditText(binding.fullNameEd)
                               .registerEditText(binding.emailEd)
                               .registerEditText(binding.userNameEd)
                               .registerEditText(binding.passEd)
                               .setCallback(this);
    }




    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId())
        {
            case R.id.fullNameEd:
                if(b) ViewCompat.setElevation(binding.fullNameEd,10);
                ViewCompat.setElevation(binding.emailEd,5);
                ViewCompat.setElevation(binding.userNameEd,5);
                ViewCompat.setElevation(binding.passEd,5);
                break;

            case R.id.emailEd:
                ViewCompat.setElevation(binding.emailEd,10);
                ViewCompat.setElevation(binding.fullNameEd,5);
                ViewCompat.setElevation(binding.userNameEd,5);
                ViewCompat.setElevation(binding.passEd,5);
                break;

            case R.id.userNameEd:
                ViewCompat.setElevation(binding.userNameEd,10);
                ViewCompat.setElevation(binding.fullNameEd,5);
                ViewCompat.setElevation(binding.emailEd,5);
                ViewCompat.setElevation(binding.passEd,5);
                break;

            case R.id.passEd:
                ViewCompat.setElevation(binding.passEd,10);
                ViewCompat.setElevation(binding.fullNameEd,5);
                ViewCompat.setElevation(binding.emailEd,5);
                ViewCompat.setElevation(binding.userNameEd,5);
                break;

        }
    }


    @Override
    public void beforeTextChanged(EditText editText, CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(EditText editText, CharSequence s, int start, int before, int count) {

        switch (editText.getId())
        {
            case R.id.fullNameEd:
                if(listner!=null) model.setName(s.toString()); listner.Data(model);
                break;


            case R.id.emailEd:
                if(listner!=null) model.setEmail(s.toString()); listner.Data(model);
                break;


            case R.id.userNameEd:
                if(listner!=null) model.setUsername(s.toString()); listner.Data(model);
                break;


            case R.id.passEd:
                if(listner!=null) model.setPassword(s.toString()); listner.Data(model);
                break;


        }

    }

    public void setListner(ILoginRegister listner)
    {
        this.listner=listner;
    }

    @Override
    public void afterTextChanged(EditText editText, Editable editable) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ivShowPassword:


                if(((BitmapDrawable)binding.ivShowPassword.getDrawable()).getBitmap()==(((BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.eye_un))).getBitmap())
                {
                    binding.passEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    Bitmap bitmap = ((BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.eye_s)).getBitmap();
                    binding.ivShowPassword.setImageBitmap(bitmap);

                }else
                {
                    binding.passEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    Bitmap bitmap = ((BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.eye_un)).getBitmap();
                    binding.ivShowPassword.setImageBitmap(bitmap);

                }
                break;



    }
    }
}
