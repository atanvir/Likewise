package com.likewise.Utility;

import com.android.billingclient.api.SkuDetails;

import java.util.Comparator;

public class CustomCoinSorting implements Comparator<SkuDetails> {

    @Override
    public int compare(SkuDetails o1, SkuDetails o2) {
        String coinsText1= o1.getTitle().split("coins")[0].trim();
        int coin1= Integer.parseInt(coinsText1.replace("+",""));
        String coinsText2= o2.getTitle().split("coins")[0].trim();
        int coin2= Integer.parseInt(coinsText2.replace("+",""));
        if(coin1>coin2) return +1;
        else if(coin1<coin2) return -1;
        else return 0;
    }
}
