package com.swarmnyc.pup;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Hashtable;

public class Config {
    private static SharedPreferences data;
    private static Context context;
    private static Hashtable<Object, Object> resources;
    private static boolean isLoggedin;
    private static String userEmail;

    public static void init(Context context) {
        Config.context = context;
        Config.data = context.getSharedPreferences("Config", Context.MODE_MULTI_PROCESS);
        Config.resources = new Hashtable<>();

        isLoggedin = !TextUtils.isEmpty(getUserToken());
    }

    public static boolean isLoggedIn() {
        return isLoggedin;
    }

    public static String getUserToken() {
        long timeSpan = data.getLong("access_token_expires_in", 0) - System.currentTimeMillis();
        if (timeSpan > 0) {
            return data.getString("access_token", null);
        } else {
            return null;
        }
    }

    public static void setUser(String userId, String token, long expireIn) {
        SharedPreferences.Editor editor = data.edit();

        isLoggedin = true;
        editor.putString("user_id", userId);
        //editor.putString("user_chatId", charId);
        //editor.putString("user_email", email);
        editor.putString("access_token", token);
        editor.putLong("access_token_expires_in", System.currentTimeMillis() + expireIn);

        editor.apply();
    }


    public static void removeUser() {
        SharedPreferences.Editor editor = data.edit();

        isLoggedin = false;

        editor.remove("user_id");
        //editor.remove("user_email");
        //editor.remove("user_chatId");
        editor.remove("access_token_expires_in");
        editor.remove("access_token");

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

    public static String getUserId() {
        return data.getString("user_id", null);
    }

//    public static String getUserEmail() {
//        return data.getString("user_email", null);
//    }
}
