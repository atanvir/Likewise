package com.likewise.Model;

import java.util.List;

public class CreateGameModel {

    private String mode;
    private String langCode;
    private String customInst;
    private String morePreciouslyId;
    private String ObjectiveId;
    private String socialId;
    private List<String> gallerImgList;
    private String type;




    public CreateGameModel(String mode, String langCode, String customInst, String morePreciouslyId, String objectiveId, String socialId,List<String> gallerImgList,String type) {
        this.mode = mode;
        this.langCode = langCode;
        this.customInst = customInst;
        this.morePreciouslyId = morePreciouslyId;
        this.ObjectiveId = objectiveId;
        this.socialId = socialId;
        this.gallerImgList=gallerImgList;
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getGallerImgList() {
        return gallerImgList;
    }

    public void setGallerImgList(List<String> gallerImgList) {
        this.gallerImgList = gallerImgList;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getCustomInst() {
        return customInst;
    }

    public void setCustomInst(String customInst) {
        this.customInst = customInst;
    }

    public String getMorePreciouslyId() {
        return morePreciouslyId;
    }

    public void setMorePreciouslyId(String morePreciouslyId) {
        this.morePreciouslyId = morePreciouslyId;
    }

    public String getObjectiveId() {
        return ObjectiveId;
    }

    public void setObjectiveId(String objectiveId) {
        ObjectiveId = objectiveId;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }
}
