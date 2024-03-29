package com.likewise.Utility;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.likewise.R;


public class SwitchFragment {

    public static void changeFragment(FragmentManager fragmentManager, Fragment fragment, boolean saveInBackstack, boolean animate) {
        String backStateName = fragment.getClass().getName();
        try {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (animate) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            transaction.replace(R.id.replaceFrame,fragment,backStateName);
            if (saveInBackstack) {
                transaction.addToBackStack(backStateName);
            }

            transaction.commitAllowingStateLoss();


        } catch (IllegalStateException e) {
        }
    }






}
