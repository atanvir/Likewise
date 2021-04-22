package com.likewise.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.likewise.Model.CommonModelDataObject;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.FilePath;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.ActivityFeedbackBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    ActivityFeedbackBinding binding;
    File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_feedback);
        init();
    }

    private void init() {
        binding.ivClose.setOnClickListener(this);
        binding.edfeedback.addTextChangedListener(this);
        binding.btnfeedback.setOnClickListener(this);
        binding.tvChatURL.setOnClickListener(this);
        binding.ivupload.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        binding.countText.setText(""+charSequence.toString().length()+"/1000");
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ivClose:
                onBackPressed();
                break;

            case R.id.btnfeedback:
                if(!binding.edfeedback.getText().toString().equalsIgnoreCase(""))
                {
                    addFeedbackApi();
                }
                break;

            case R.id.ivupload:

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);


                break;

            case R.id.tvChatURL:
                String url = "https://likewise.chat/";
                Intent intent= new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;

        }
    }

    private void addFeedbackApi() {
        if(CommonUtils.networkConnectionCheck(this)) {
            try {
                Map<String, RequestBody> requestBodyMap = new HashMap<>();
                requestBodyMap.put("message",RequestBody.create(MediaType.parse("text/plain"),binding.edfeedback.getText().toString()));
                RequestBody body=null;
                MultipartBody.Part part=null;
                if(imgFile!=null) {
                   body = RequestBody.create(MediaType.parse("image/*"), imgFile);
                    part = MultipartBody.Part.createFormData("image",imgFile.getName(),body);
                }


                ServicesInterface anInterface = ServicesConnection.getInstance().createService(this);
                Call<CommonModelDataObject> call = anInterface.addFeedback(part,requestBodyMap);
                ServicesConnection.getInstance().enqueueWithoutRetry(call, this, true, new Callback<CommonModelDataObject>() {
                    @Override
                    public void onResponse(Call<CommonModelDataObject> call, Response<CommonModelDataObject> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataObject serverResponse=response.body();

                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                finish();
                                Toast.makeText(FeedbackActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
                                CommonUtils.showSnackBar(FeedbackActivity.this,serverResponse.getMessage(),ParamEnum.Failure.theValue());
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<CommonModelDataObject> call, Throwable t) {
                        Log.e("failure",t.getMessage());

                    }
                });
            } catch (Exception e) {
                Toast.makeText(FeedbackActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            CommonUtils.showSnackBar(FeedbackActivity.this,getString(R.string.no_internet),ParamEnum.Failure.theValue());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                String path= FilePath.getPath(FeedbackActivity.this,result.getUri());
                try {
                    imgFile= new Compressor(this).compressToFile(new File(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.tvfileName.setText(imgFile.getName());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
