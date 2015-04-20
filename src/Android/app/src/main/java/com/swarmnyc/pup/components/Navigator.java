package com.swarmnyc.pup.components;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.swarmnyc.pup.R;

public class Navigator {
    private static FragmentActivity activity;

    public static void init(FragmentActivity activity){
        Navigator.activity = activity;
    }

    public static void To(Fragment fragment){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        String tag = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate (tag, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(tag)
                    .commit();
        }
    }

    public static void ToWithoutBack(Fragment fragment){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        String tag = fragment.getClass().getName();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment ,tag)
                .addToBackStack(null)
                .commit();
    }
}
