package com.likewise.Interface;

import java.util.List;

public interface OnItemClickListner {

    // create game's recycle view
    void onSocialPatnerSelected(String socialId);
    void onLanguageSelected(String langCode,String langName,String description,String flagImage);
    void onObjectiveSelected(String objectiveId);
    void onMorePrecislySelected(String morePreciouslyId);
    void onSelectedImagesCalled();

}
