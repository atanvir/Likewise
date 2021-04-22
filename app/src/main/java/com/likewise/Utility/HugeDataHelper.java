package com.likewise.Utility;

import com.likewise.Model.PlayGameResponseModel;

public class HugeDataHelper {
    public static HugeDataHelper hugeDataHelper;
    public PlayGameResponseModel.Data data;
    private HugeDataHelper(){}
    public static HugeDataHelper getInstance(){
        if(hugeDataHelper==null) {hugeDataHelper=new HugeDataHelper();}
        return hugeDataHelper;
    }

    public void setHugeData(PlayGameResponseModel.Data data){
        this.data=data;
    }
    public PlayGameResponseModel.Data getHugeData(){
        return this.data;
    }

}
