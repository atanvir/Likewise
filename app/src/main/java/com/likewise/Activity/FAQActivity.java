package com.likewise.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.likewise.Adapter.FAQAdapter;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityFaqBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAQActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityFaqBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_faq);
        binding.closeIv.setOnClickListener(this);
        listFAQApi();

    }

    private void listFAQApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.listFaq();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                setUpData(serverResponse.getData());


                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(FAQActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(FAQActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(FAQActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }
    }

    private void setUpData(ResponseBean data) {
    List<String> bodyList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();
    String abc=Html.fromHtml(data.getValue()).toString();
    Log.e("abc",abc);
        for(int i=0;i<data.getValue().split("body").length;i++){
            if(i%2!=0){
               String body=abc.split("body")[i].replace("&lt;","").replace(">","").replace("./","").toString();
               body=body.replace(">","");
               body=body.replace("</","");
               Log.e("body",body);
                bodyList.add(body.trim());
            }
         }
        for(int i=0;i< data.getValue().split("title").length;i++){
            if(i%2!=0){
                String title=Html.fromHtml(abc.split("title")[i].replace("&lt;","").replace(">","").replace("./","")).toString();
                title=title.replace(">","");
                titleList.add(title);
            }
        }

        for(int i=0;i<bodyList.size();i++)
        {
            Log.e("description",bodyList.get(i)+"\n");
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new FAQAdapter(FAQActivity.this,titleList,bodyList));
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
