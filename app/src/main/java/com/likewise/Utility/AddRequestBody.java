package com.likewise.Utility;



import android.text.TextUtils;

import com.likewise.Model.CreateGameModel;
import com.likewise.Model.ResponseBean;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AddRequestBody<T> {

    private MediaType mediaType = MediaType.parse("text/plain");

    private Map<String, RequestBody> requestBodyMap = new HashMap<>();

    public AddRequestBody(T data)
    {
        if(data instanceof ResponseBean) {
            requestBodyMap.put("name", RequestBody.create(mediaType, ((ResponseBean) data).getName()));
            requestBodyMap.put("username", RequestBody.create(mediaType, ((ResponseBean) data).getUsername()));
            requestBodyMap.put("dob", RequestBody.create(mediaType, ((ResponseBean) data).getDob()));
            requestBodyMap.put("email", RequestBody.create(mediaType, ((ResponseBean) data).getEmail()));
            requestBodyMap.put("gender", RequestBody.create(mediaType, ((ResponseBean) data).getGender()));
            requestBodyMap.put("aboutus", RequestBody.create(mediaType, ((ResponseBean) data).getAboutus()));
            requestBodyMap.put("nationalit", RequestBody.create(mediaType, ((ResponseBean) data).getNationalit()));
            requestBodyMap.put("state", RequestBody.create(mediaType, ((ResponseBean) data).getState()));
            requestBodyMap.put("city", RequestBody.create(mediaType, ((ResponseBean) data).getCity()));
            requestBodyMap.put("country", RequestBody.create(mediaType, ((ResponseBean) data).getCountry()));
            requestBodyMap.put("interest", RequestBody.create(mediaType, ((ResponseBean) data).getInterest()));
            requestBodyMap.put("defaultLangCode", RequestBody.create(mediaType, ((ResponseBean) data).getDefaultLangCode()));
            if(((ResponseBean) data).getLanguageCode()!=null) {
                for (int i = 0; i < ((ResponseBean) data).getLanguageCode().size(); i++) {
                    requestBodyMap.put("languageCode[" + i + "]", RequestBody.create(mediaType, ((ResponseBean) data).getLanguageCode().get(i)));
                }
            }
        }
        else if(data instanceof CreateGameModel)
        {
            requestBodyMap.put("mode", RequestBody.create(mediaType, ((CreateGameModel) data).getMode()));
            requestBodyMap.put("type", RequestBody.create(mediaType, ((CreateGameModel) data).getType()));
            requestBodyMap.put("languageCode", RequestBody.create(mediaType, ((CreateGameModel) data).getLangCode()));
            requestBodyMap.put("customInstructions", RequestBody.create(mediaType, ((CreateGameModel) data).getCustomInst()));
            requestBodyMap.put("morePrecisely", RequestBody.create(mediaType, ((CreateGameModel) data).getMorePreciouslyId()));
            requestBodyMap.put("objective", RequestBody.create(mediaType, ((CreateGameModel) data).getObjectiveId()));
            requestBodyMap.put("socialId", RequestBody.create(mediaType, ((CreateGameModel) data).getSocialId()));
            if(((CreateGameModel) data).getGallerImgList()!=null) {
                for (int i = 0; i < ((CreateGameModel) data).getGallerImgList().size(); i++) {
                    requestBodyMap.put("publicGallery[" + i + "]", RequestBody.create(mediaType, ((CreateGameModel) data).getGallerImgList().get(i)));
                }
            }
        }
    }

    public Map<String, RequestBody> getBody()
    {
        return requestBodyMap;
    }
}
