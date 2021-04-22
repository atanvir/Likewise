package com.likewise.Utility;

import com.likewise.Model.ResponseBean;

import java.util.ArrayList;
import java.util.List;

public class SelectCountry {
    public static List<ResponseBean> list;


    private SelectCountry()
    {

    }

    public static  List<ResponseBean> getInstance()
    {
        if(list==null)
        {
            list = new ArrayList<>();

        }

        return list;
    }

    public static void removeAll(List<ResponseBean> beans)
    {
        if(beans!=null) list.removeAll(beans);

    }





    public static void add(List<ResponseBean> beanList){
        list=beanList;
    }

    public static void remove(String id)
    {
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).getId().equalsIgnoreCase(id))
            {
                list.remove(list.get(i));
            }
        }

    }


}
