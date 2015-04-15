package com.swarmnyc.pup;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.swarmnyc.pup.models.LoggedInUser;

import java.util.Hashtable;

public class Config {
    private static SharedPreferences data;
    private static Context context;
    private static Hashtable<Object, Object> resources;

    public static void init(Context context) {
        Config.context = context;
        Config.data = context.getSharedPreferences("Config", Context.MODE_MULTI_PROCESS);
        Config.resources = new Hashtable<>();
    }

    public static String getString(String key) {
        return data.getString(key, null);
    }

    public static Long getLong(String key) {
        return data.getLong(key, 0);
    }

    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setLong(String key, long value) {
        SharedPreferences.Editor editor = data.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = data.edit();
        editor.remove(key);
        editor.apply();
    }

    public static String getConfigString(int id) {
        if (!Config.resources.containsKey(id)) {
            Config.resources.put(id, context.getResources().getString(id));
        }

        return (String) Config.resources.get(id);
    }

    public static String getConfigString(String name) {
        int id = context.getResources().getIdentifier(name, "string", Config.class.getPackage().getName());

        return getConfigString(id);
    }
}
