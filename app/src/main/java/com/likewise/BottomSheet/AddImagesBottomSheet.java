package com.likewise.BottomSheet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.likewise.Activity.CreateGameActivity;
import com.likewise.Adapter.AddImagesAdapter;
import com.likewise.Adapter.PublicGalleryAdapter;
import com.likewise.Interface.OnItemClickListner;
import com.likewise.Interface.SheetItemClickListner;
import com.likewise.Model.CameraModel;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.GalleryModel;
import com.likewise.Model.ResponseBean;
import com.likewise.R;
import com.likewise.Retrofit.ServicesConnection;
import com.likewise.Retrofit.ServicesInterface;
import com.likewise.Utility.AllImagesSingleton;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.FilePath;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.BottomSheetAddImagesBinding;
import com.likewise.databinding.BottomSheetFacebookLoginBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import hari.bounceview.BounceView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddImagesBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener, OnItemClickListner, TextWatcher, AddImagesAdapter.onImageDeleteListner {
    public static final int TAKE_PICTURE =1 ;
    private BottomSheetAddImagesBinding binding;
    private AddImagesAdapter adapter;
    private PublicGalleryAdapter publicGalleryAdapter;
    private RecyclerView addImgRV;
    private  List<ResponseBean> imageData;
    private LinearLayout randomImagesLL,noImageLL;
    private bottomSheetListner listner;

    public AddImagesBottomSheet(RecyclerView addImgRV,LinearLayout randomImagesLL,LinearLayout noImageLL,bottomSheetListner listner)
    {
        this.addImgRV=addImgRV;
        this.randomImagesLL=randomImagesLL;
        this.noImageLL=noImageLL;
        this.listner=listner;
    }

    public interface bottomSheetListner{
        void onDismiss();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding= DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_add_images,container,false);
       return binding.getRoot();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(listner!=null)
        {
            listner.onDismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.takePicLL.setOnClickListener(this);
        binding.publicGalleryLL.setOnClickListener(this);
        binding.galleryLL.setOnClickListener(this);
        binding.searchFriendEd.addTextChangedListener(this);
        binding.ivSearch.setOnClickListener(this);
        binding.ivSelect.setOnClickListener(this);
        BounceView.addAnimTo(binding.ivSelect);
        binding.publicGalleryLL.performClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

            case R.id.takePicLL:
                if(AllImagesSingleton.getInstance().size()!=6) {
                    settingBackground("Take Picture");
                    takePhotoIntent();
                }else
                {
                    Toast.makeText(getActivity(), "You can select upto 6 images", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.publicGalleryLL:
                settingBackground("Public Gallery");
                binding.searchFriendEd.setVisibility(View.VISIBLE);
                binding.ivSearch.setVisibility(View.VISIBLE);
                publicGalleryApi();
                break;

            case R.id.galleryLL:
                settingBackground("Gallery");
                binding.searchFriendEd.setVisibility(View.GONE);
                binding.ivSearch.setVisibility(View.GONE);
                new GalleryImagesUri(getActivity()).execute();
                break;

            case R.id.ivSearch:
            CommonUtils.hideKeyboardFrom(getActivity(),binding.searchFriendEd);
            searchImages(binding.searchFriendEd.getText());
            break;
            case R.id.ivSelect:
            if(binding.ivSelect.getVisibility()==View.VISIBLE)
            {
                dismiss();
            }
            break;


        }
    }

    private void publicGalleryApi() {
        if(CommonUtils.networkConnectionCheck(getActivity())) {
            try {
                ServicesInterface anInterface = ServicesConnection.getInstance().createService(getActivity());
                Call<CommonModelDataList> call = anInterface.listImages();
                ServicesConnection.getInstance().enqueueWithoutRetry(call, getActivity(), true, new Callback<CommonModelDataList>() {
                    @Override
                    public void onResponse(Call<CommonModelDataList> call, Response<CommonModelDataList> response) {
                        if(response.isSuccessful())
                        {
                            CommonModelDataList serverResponse=response.body();
                            if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Success.theValue()))
                            {
                                if(AllImagesSingleton.getInstance().size()>0)
                                {
                                    binding.ivSelect.setVisibility(View.VISIBLE);
                                }
                                imageData=serverResponse.getData();
                                publicGalleryAdapter=new PublicGalleryAdapter(getActivity(), getImageList(serverResponse.getData()),AddImagesBottomSheet.this);
                                GridLayoutManager manager = new GridLayoutManager(getActivity(),3);
                                binding.picturesRV.setLayoutManager(manager);
                                binding.picturesRV.setAdapter(publicGalleryAdapter);

                            }else if(serverResponse.getStatus().equalsIgnoreCase(ParamEnum.Failure.theValue()))
                            {
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

    private List<ResponseBean> getImageList(List<ResponseBean> data) {


        List<ResponseBean> beanList= new ArrayList<>();

        for(int i=0;i<data.size();i++)
        {
            for(int j=0;j<data.get(i).getImages().size();j++)
            {
                ResponseBean bean = new ResponseBean();
                bean.setPath(""+data.get(i).getImages().get(j));
                bean.setThemeName(data.get(i).getThemeName());
                bean.setId(data.get(i).getId());
                beanList.add(bean);
            }
        }


        for(int i=0;i<AllImagesSingleton.getInstance().size();i++)
        {
            for(int j=0;j<beanList.size();j++)
            {
                if(AllImagesSingleton.getInstance().get(i).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName())){
                if(beanList.get(j).getPath().equalsIgnoreCase(((ResponseBean) AllImagesSingleton.getInstance().get(i)).getPath())) {
                    if (((ResponseBean) AllImagesSingleton.getInstance().get(i)).isChecked()) {
                        beanList.get(j).setChecked(true);
                        break;
                    }
                }
                }
            }
        }

        Collections.shuffle(beanList);
        return beanList;
    }

    private void takePhotoIntent() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, TAKE_PICTURE);
        dismiss();

    }

    private void settingBackground(String type) {
         if(type.equalsIgnoreCase("Take Picture")) {
            binding.takePicLL.setBackground(getActivity().getResources().getDrawable(R.drawable.drawable_light_dark_purple_corners));
            binding.ivCamera.setImageResource(R.drawable.icam_s);
            binding.tvCamera.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));

            binding.publicGalleryLL.setBackground(getActivity().getResources().getDrawable(R.drawable.drawable_white_stroke_dot_corners));
            binding.ivpublicGalley.setImageResource(R.drawable.publicgal_un);
            binding.tvpublicGallery.setTextColor(ContextCompat.getColor(getActivity(),R.color.black));
            
            binding.galleryLL.setBackground(getActivity().getResources().getDrawable(R.drawable.drawable_white_stroke_dot_corners));
            binding.ivGallery.setImageResource(R.drawable.gal_un);
            binding.tvGallery.setTextColor(ContextCompat.getColor(getActivity(),R.color.black));


        }
        else if(type.equalsIgnoreCase("Public Gallery")) {
            binding.takePicLL.setBackground(getActivity().getResources().getDrawable(R.drawable.drawable_white_stroke_dot_corners));
            binding.ivCamera.setImageResource(R.drawable.icam_un);
            binding.tvCamera.setTextColor(ContextCompat.getColor(getActivity(),R.color.black));

            binding.publicGalleryLL.setBackground(getActivity().getResources().getDrawable(R.drawable.drawable_light_dark_purple_corners));
            binding.ivpublicGalley.setImageResource(R.drawable.publicgal_s);
            binding.tvpublicGallery.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
            
            binding.galleryLL.setBackground(getActivity().getResources().getDrawable(R.drawable.drawable_white_stroke_dot_corners));
            binding.ivGallery.setImageResource(R.drawable.gal_un);
            binding.tvGallery.setTextColor(ContextCompat.getColor(getActivity(),R.color.black));

        }
        else if(type.equalsIgnoreCase("Gallery")) {
            binding.takePicLL.setBackground(getActivity().getResources().getDrawable(R.drawable.drawable_white_stroke_dot_corners));
            binding.ivCamera.setImageResource(R.drawable.icam_un);
            binding.tvCamera.setTextColor(ContextCompat.getColor(getActivity(),R.color.black));

            binding.publicGalleryLL.setBackground(getActivity().getResources().getDrawable(R.drawable.drawable_white_stroke_dot_corners));
            binding.ivpublicGalley.setImageResource(R.drawable.publicgal_un);
            binding.tvpublicGallery.setTextColor(ContextCompat.getColor(getActivity(),R.color.black));


            binding.galleryLL.setBackground(getActivity().getResources().getDrawable(R.drawable.drawable_light_dark_purple_corners));
            binding.ivGallery.setImageResource(R.drawable.gal_s);
            binding.tvGallery.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));

        }
    }

    @Override
    public void onSocialPatnerSelected(String socialId) {

    }

    @Override
    public void onLanguageSelected(String langCode,String langauge,String description,String flag) {

    }

    @Override
    public void onObjectiveSelected(String objectiveId) {

    }

    @Override
    public void onMorePrecislySelected(String morePreciouslyId) {

    }

    @Override
    public void onSelectedImagesCalled() {
        if(AllImagesSingleton.getInstance().size()>0) {
            binding.ivSelect.setVisibility(View.VISIBLE);
        }else
        {
            binding.ivSelect.setVisibility(View.GONE);
        }
        LinearLayoutManager addImageManager=new LinearLayoutManager(getActivity());
        addImageManager.setOrientation(RecyclerView.HORIZONTAL);
        this.addImgRV.setLayoutManager(addImageManager);
        adapter=new AddImagesAdapter(getActivity(), AllImagesSingleton.getInstance(),this);
        addImgRV.setAdapter(adapter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        searchImages(s);
    }

    private void searchImages(CharSequence s) {
        if(!s.toString().equalsIgnoreCase("")) {
            GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
            binding.picturesRV.setLayoutManager(manager);
            binding.picturesRV.setAdapter(publicGalleryAdapter=new PublicGalleryAdapter(getActivity(), getImageList(imageData),AddImagesBottomSheet.this));
            publicGalleryAdapter.getFilter().filter(s);
        }
        else { publicGalleryApi(); }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onDelete() {
        if(AllImagesSingleton.getInstance().size()==0)
        {
           randomImagesLL.setVisibility(View.VISIBLE);
           noImageLL.setVisibility(View.VISIBLE);
           addImgRV.setVisibility(View.GONE);
        }

    }

    public class GalleryImagesUri extends AsyncTask< Void,Void , List<GalleryModel>>  {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        List<GalleryModel> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage = null;
        private Context context;
        public GalleryImagesUri(Context context)
        {
            this.context=context;
        }

        @Override
        protected List<GalleryModel> doInBackground(Void... voids) {
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
//            "bucket_display_name='Camera'"
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    absolutePathOfImage = cursor.getString(column_index_data);
                    File file=new File(absolutePathOfImage);
                    listOfAllImages.add(new GalleryModel(file.getAbsolutePath()));
                    cursor.moveToNext();

                }

            }

            for(int i=0;i<AllImagesSingleton.getInstance().size();i++)
            {
                for(int j=0;j<listOfAllImages.size();j++)
                {
                    if(AllImagesSingleton.getInstance().get(i).getClass().getSimpleName().equalsIgnoreCase(GalleryModel.class.getSimpleName())){
                        if(listOfAllImages.get(j).getPath().equalsIgnoreCase(((GalleryModel) AllImagesSingleton.getInstance().get(i)).getPath())) {
                            if (((GalleryModel) AllImagesSingleton.getInstance().get(i)).isChecked()) {
                                listOfAllImages.get(j).setChecked(true);
                                break;
                            }
                        }
                    }
                }
            }


            return listOfAllImages;

        }

        @Override
        protected void onPostExecute(List<GalleryModel> strings) {
            super.onPostExecute(strings);
            Collections.shuffle(strings);
            GridLayoutManager manager=new GridLayoutManager(context,3);
            binding.picturesRV.setLayoutManager(manager);
            binding.picturesRV.setAdapter(new PublicGalleryAdapter(context,strings, AddImagesBottomSheet.this));
        }

    }

    
}


