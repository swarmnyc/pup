package com.swarmnyc.pup;

import com.google.gson.Gson;
import com.quickblox.core.helper.StringUtils;
import com.swarmnyc.pup.models.LoggedInUser;

public class User {
    private static final String KEY_USER_EXPIRES = "UserExpires";
    private static final String KEY_USER = "User";

    public static LoggedInUser current;
    private static Gson gson = new Gson();

    public static void init() {
        Long expires = Config.getLong(KEY_USER_EXPIRES);
        if (expires > System.currentTimeMillis()){
            String json = Config.getString(KEY_USER);

            if (!StringUtils.isEmpty(json)) {
                current = gson.fromJson(json, LoggedInUser.class);
            }
        }
    }

    public static void login(LoggedInUser current) {
        User.current = current;

        Config.setLong(KEY_USER_EXPIRES, System.currentTimeMillis() + current.getExpiresIn());
        Config.setString(KEY_USER, gson.toJson(current));
    }

    public static void Logout() {
        User.current = null;

        Config.remove(KEY_USER_EXPIRES);
    }

    public static boolean isLoggedIn() {
        return current != null;
    }


}
