package com.auxluffy.mobileplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/6/23.
 */
public class CacheUtils {
    public static void putString(Context context, String key, String value){
        SharedPreferences sp = context.getSharedPreferences("VideoCache",Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("VideoCache",Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }
}
