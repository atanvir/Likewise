package com.likewise.SharedPrefrence;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.likewise.Model.CommonModelDataObject;
import com.likewise.Model.ResponseBean;
import com.likewise.Utility.ParamEnum;

import java.util.List;

public class SPrefrenceValues {

    //All Users
    public static void setPrefrences(Context context, CommonModelDataObject data)
    {
        SharedPreferenceWriter.getInstance(context).writeBooleanValue(SPreferenceKey.IS_LOGIN,true);
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.JWT_TOKEN,data.getData().getJwtToken());
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.PROFILE_PIC,data.getData().getProfilePic());
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.NAME,data.getData().getName());
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.USER_TYPE,data.getData().getType());
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.ID,data.getData().getId());
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.COINS,data.getData().getCoins());
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.POINTS,""+data.getData().getTotalPoints());
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.LANGUAGE_CODE, TextUtils.join(",",data.getData().getLanguageCode().toArray()));
        SharedPreferenceWriter.getInstance(context).writeBooleanValue(SPreferenceKey.SOUND, true);
        SharedPreferenceWriter.getInstance(context).writeBooleanValue(SPreferenceKey.VIBRATE, true);
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.DEFAULT_LANGUAGE_CODE, data.getData().getDefaultLangCode());
        SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.SOCIAL_ID, data.getData().getSocialId());
    }

    public static void removePrefrences(Context context)
    {
        SharedPreferenceWriter.getInstance(context).clearPreferenceValues("");
    }

}
