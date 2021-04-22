package com.likewise.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.likewise.Fragment.GameFragment;
import com.likewise.Fragment.SelectLanguageFragment;

import io.socket.client.Socket;

public class GameFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private String type;
    private Socket socket;
    private FragmentManager fm;

    public GameFragmentPagerAdapter(FragmentManager fm, String type, Socket socket) {
        super(fm);
        this.fm=fm;
        this.type=type;
        this.socket=socket;
    }


     @NonNull
    @Override
    public Fragment getItem(int position) {
        if(type.equalsIgnoreCase("Language")){
           return new SelectLanguageFragment(position);
        }else
        {
            Fragment fragment=new GameFragment(socket);
            Bundle bundle=new Bundle();
            bundle.putString("type",type);
            bundle.putString("name", ""+getPageTitle(position));
            fragment.setArguments(bundle);
            return fragment;
        }

    }




    @Override
    public int getCount() {
        if(type.equalsIgnoreCase("Matched"))
        {
            return 3;
        }else
        {
            return 2;
        }

    }

     @Override
     public int getItemPosition(@NonNull Object object) {
       return POSITION_NONE;
     }

     @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (type.equalsIgnoreCase("Matched")) {

            if (position == 0) {
                return "All";
            } else if (position == 1) {
                return "Friends";
            } else {
                return "Random";
            }

        }else if(type.equalsIgnoreCase("Language"))
        {
            if (position == 0) {
                return "POPULAR";
            } else {
                return "OTHERS";
            }

        }else {
            if (position == 0) {
                return "Current";
            } else {
                return "Completed";
            }
        }
    }

    public void setData(String type)
    {
        this.type = type;
        notifyDataSetChanged();
    }

}
