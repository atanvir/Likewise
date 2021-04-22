package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityPrivacyPoliciesBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivacyPoliciesActivity extends AppCompatActivity implements View.OnClickListener {


    ActivityPrivacyPoliciesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_privacy_policies);
        binding.closeIv.setOnClickListener(this);
        listPrivacyPolicyApi();
    }

    private void listPrivacyPolicyApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.listPrivacyPolicy();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                    binding.mainCl.setVisibility(View.VISIBLE);
                                    binding.headerText.setText("");
                                    binding.footerText.setText(Html.fromHtml(serverResponse.getData().getValue()).toString());

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(PrivacyPoliciesActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(PrivacyPoliciesActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(PrivacyPoliciesActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            case R.id.closeIv:
                onBackPressed();
                break;

        }
    }
}
