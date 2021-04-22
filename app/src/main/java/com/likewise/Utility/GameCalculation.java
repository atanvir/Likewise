package com.likewise.Utility;

import android.util.Log;

public class GameCalculation {

    public  static long points(int basePoints,int all_match)
    {
       double matches= (double) all_match/100;
        double percent= (1-matches);
        if(percent==0.0)
        {
         percent=1.0;
        }
        double mat= percent*basePoints;
        Log.e("perMatchPoint", ""+Math.round(mat));
        return Math.round(mat);


    }

    public static double score(int streaks,double points,int bonus)
    {
        if(streaks>0)
        {
            return (points*bonus*streaks);
        }else
        {
            return  (points*bonus);


        }
    }

}
