package com.binbill.seller;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SharedPref {

    public static final String SHARED_PREF = "BinBill_Seller_Pref";
    public static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String SELLER_ID = "SELLER_ID";

    private static SharedPreferences mSharedPref;

    private static SharedPreferences getSharedPref(Context context) {
        if (mSharedPref == null) {
            mSharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        }
        return mSharedPref;
    }

    public static boolean hasKey(Context context, String pref) {
        SharedPreferences preferences = getSharedPref(context);
        if (preferences != null && !TextUtils.isEmpty(pref)) {
            return preferences.contains(pref);
        }
        return false;
    }

    public static void removePref(Context context, String pref) {
        getSharedPref(context).edit().remove(pref).commit();
    }

    public static void putString(Context context, String pref, String value) {
        getSharedPref(context).edit().putString(pref, value).commit();
    }

    public static String getString(Context context, String pref) {
        String value = getSharedPref(context).getString(pref, "");
        return value;
    }

    public static void putFloat(Context context, String pref, float value) {
        getSharedPref(context).edit().putFloat(pref, value).commit();
    }

    public static float getFloat(Context context, String pref) {
        return getSharedPref(context).getFloat(pref, -1);
    }

    public static void putBoolean(Context context, String pref, boolean value) {
        getSharedPref(context).edit().putBoolean(pref, value).commit();
    }

    public static boolean getBoolean(Context context, String pref) {
        return getSharedPref(context).getBoolean(pref, false);
    }

    public static void putLong(Context context, String pref, long value) {
        getSharedPref(context).edit().putLong(pref, value).commit();
    }

    public static long getLong(Context context, String pref) {
        return getSharedPref(context).getLong(pref, -1);
    }

    public static void putInt(Context context, String pref, int value) {
        getSharedPref(context).edit().putInt(pref, value).commit();
    }

    public static int getInt(Context context, String pref) {
        return getSharedPref(context).getInt(pref, -1);
    }

}
