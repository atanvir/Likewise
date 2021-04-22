package com.likewise.Utility;

import com.likewise.Model.GalleryModel;
import com.likewise.Model.ResponseBean;

import java.util.ArrayList;
import java.util.List;

public class AllImagesSingleton {

    public static List<Object> list;


    private AllImagesSingleton()
    {

    }

    public static  List<Object> getInstance()
    {
        if(list==null)
        {
            list = new ArrayList<>();

        }

        return list;
    }

    public static void removeAll()
    {
        if(list!=null) list.removeAll(list);

    }

    public static void add(Object beans)
    {
        list.add(beans);
    }



}
