package com.likewise.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.likewise.Model.CommonModelDataList;
import com.likewise.Model.GalleryModel;
import com.likewise.Model.Objective;
import com.likewise.Model.ResponseBean;
import com.likewise.Interface.OnItemClickListner;
import com.likewise.R;
import com.likewise.Utility.AllImagesSingleton;
import com.likewise.Utility.CommonUtils;
import com.likewise.Utility.ImageGlider;
import com.likewise.Utility.ParamEnum;
import com.likewise.databinding.AdapterPublicGalleryBinding;

import java.util.ArrayList;
import java.util.List;

import hari.bounceview.BounceView;
import retrofit2.Callback;

public class PublicGalleryAdapter extends RecyclerView.Adapter<PublicGalleryAdapter.MyViewHolder>implements Filterable {

    private Context context;
    private List<? extends Object> imgList;
    int checkPos=-1;
    private OnItemClickListner listner;


    public PublicGalleryAdapter(Context context, List<? extends Object> imgList,OnItemClickListner listner)
    {
        this.context=context;
        this.imgList=imgList;
        this.listner=listner;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterPublicGalleryBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_public_gallery,parent,false);
        return new PublicGalleryAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Gallery
        if(imgList.get(position).getClass().getSimpleName().equalsIgnoreCase(GalleryModel.class.getSimpleName())) {
            ImageGlider.setRoundImage(context, holder.binding.imageIv, holder.binding.progressBar, ((GalleryModel) imgList.get(position)).getPath());
            if((((GalleryModel) imgList.get(position)).isChecked())) holder.binding.checkBox.setVisibility(View.VISIBLE);
            else holder.binding.checkBox.setVisibility(View.GONE);
        }
        //Public Gallery
        else if(imgList.get(position).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName()))
        {
            ImageGlider.setRoundImage(context, holder.binding.imageIv, holder.binding.progressBar, ((ResponseBean) imgList.get(position)).getPath());
            if((((ResponseBean) imgList.get(position)).isChecked())) holder.binding.checkBox.setVisibility(View.VISIBLE);
            else holder.binding.checkBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {

        return imgList!=null?imgList.size():0;
    }

    @Override
    public Filter getFilter() {
        return new CustomFilter();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdapterPublicGalleryBinding binding;
        public MyViewHolder(final AdapterPublicGalleryBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            BounceView.addAnimTo(binding.mainCl);
            BounceView.addAnimTo(binding.imageIv);
            CommonUtils.showAnimation(binding.mainCl);

            binding.mainCl.setOnClickListener(this);
            binding.imageIv.setOnClickListener(this);
            binding.checkBox.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            switch (v.getId())
            {

                case R.id.mainCl:
                    if(AllImagesSingleton.getInstance().size()!=6) {
                        // Gallery
                        if (imgList.get(getAdapterPosition()).getClass().getSimpleName().equalsIgnoreCase(GalleryModel.class.getSimpleName())) {
                            if (checkDuplicate(AllImagesSingleton.getInstance(), getAdapterPosition())) {
                                notifyItemChanged(getAdapterPosition());
                                listner.onSelectedImagesCalled();
                            } else {
                                AllImagesSingleton.getInstance().add(imgList.get(getAdapterPosition()));
                                checkPos = getAdapterPosition();
                                ((GalleryModel) imgList.get(getAdapterPosition())).setChecked(true);
                                listner.onSelectedImagesCalled();
                                notifyItemChanged(getAdapterPosition());
                            }
                        }
                        // Public Gallery
                        else if (imgList.get(getAdapterPosition()).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName())) {
                            if (checkDuplicate(AllImagesSingleton.getInstance(), getAdapterPosition())) {
                                notifyItemChanged(getAdapterPosition());
                                listner.onSelectedImagesCalled();
                            }
                            else {
                                AllImagesSingleton.getInstance().add(imgList.get(getAdapterPosition()));
                                checkPos = getAdapterPosition();
                                ((ResponseBean) imgList.get(getAdapterPosition())).setChecked(true);
                                listner.onSelectedImagesCalled();
                                notifyItemChanged(getAdapterPosition());
                            }

                        }
                    }
                    else
                    {
                        Toast.makeText(context, "You can select upto 6 Images", Toast.LENGTH_SHORT).show();
                    }


                    break;




                case R.id.checkBox:
                    //Gallery
                    if(imgList.get(getAdapterPosition()).getClass().getSimpleName().equalsIgnoreCase(GalleryModel.class.getSimpleName()))
                    {
                        if(checkPos!=getAdapterPosition()) {
                            imgList.remove(getAdapterPosition());
                            notifyDataSetChanged();
                        }else
                        {
                            checkPos=getAdapterPosition();
                            ((GalleryModel)imgList.get(getAdapterPosition())).setChecked(true);
                            notifyDataSetChanged();

                        }

                    }
                    // Public Gallery
                    else if(imgList.get(getAdapterPosition()).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName()))
                    {
                        if(checkPos!=getAdapterPosition()) {
                            imgList.remove(getAdapterPosition());
                            notifyDataSetChanged();
                        }else
                        {
                            checkPos=getAdapterPosition();
                            ((ResponseBean)imgList.get(getAdapterPosition())).setChecked(true);
                            notifyDataSetChanged();

                        }

                    }



                    break;

                case R.id.imageIv:
                    binding.mainCl.performClick();
                    break;
            }
        }
    }

    private boolean checkDuplicate(List<? extends Object> list,int pos) {
        boolean ret=false;
        for(int i=0;i<list.size();i++)
        {
            //Public Gallery
            if((list.get(i).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName())) &&
                imgList.get(pos).getClass().getSimpleName().equalsIgnoreCase(ResponseBean.class.getSimpleName()))
            {
               if(((ResponseBean)list.get(i)).isChecked() && ((ResponseBean)imgList.get(pos)).isChecked())
               {
                   ret=true;
                   ((ResponseBean)imgList.get(pos)).setChecked(false);
                   AllImagesSingleton.getInstance().remove(i);
               }

            }
            //Gallery
            else if(list.get(i).getClass().getSimpleName().equalsIgnoreCase(GalleryModel.class.getSimpleName()) &&
                    imgList.get(pos).getClass().getSimpleName().equalsIgnoreCase(GalleryModel.class.getSimpleName()))
            {
                if(((GalleryModel)list.get(i)).isChecked() && ((GalleryModel)imgList.get(pos)).isChecked())
                {
                    ret=true;
                    ((GalleryModel)imgList.get(pos)).setChecked(false);
                    AllImagesSingleton.getInstance().remove(i);
                }

            }

        }
        return ret;

    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ResponseBean> filterList= new ArrayList<>();
            for(int i=0;i<imgList.size();i++)
            {

                for(int j=0;j<((ResponseBean)imgList.get(i)).getThemeName().size();j++)
                {
                    if(((ResponseBean)imgList.get(i)).getThemeName().get(j).toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        filterList.add(((ResponseBean) imgList.get(i)));
                    }

                }
            }

            results.values=filterList;
            results.count=filterList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            imgList= (List<? extends Object>) results.values;
            notifyDataSetChanged();

        }
    }
}
