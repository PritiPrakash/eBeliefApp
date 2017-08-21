package com.example.admin.ebeliefapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 25/1/17.
 */

public class SharedPrefUtil {

    private static final String BELIEF_SHARED_PREF = "com.example.admin.ebeliefapp";

    private static SharedPreferences mSharedPrefs;

    public synchronized static SharedPreferences getSharedPreferences(Context context) {
        if (mSharedPrefs == null) {
            mSharedPrefs = context.getSharedPreferences(BELIEF_SHARED_PREF,
                    Context.MODE_PRIVATE);
        }

        return mSharedPrefs;
    }
}
