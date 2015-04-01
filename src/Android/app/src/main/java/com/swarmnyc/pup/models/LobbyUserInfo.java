package com.swarmnyc.pup.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class LobbyUserInfo {
    String id;
    String name;
    String pictureUrl;
    Boolean isOwner;
    Boolean isLeave;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public Boolean getIsLeave() {
        return isLeave;
    }

    public LobbyUserInfo(JSONObject json) throws JSONException {
        id = json.getString("id");
        name = json.getString("name");
        pictureUrl = json.optString("pictureUrl");
        isOwner = json.getBoolean("isOwner");
        isLeave = json.getBoolean("isLeave");
    }

    public static Hashtable<String, LobbyUserInfo> FromJsonArray(JSONArray jsonArray) throws JSONException {
        Hashtable<String, LobbyUserInfo> list = new Hashtable<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                LobbyUserInfo obj = new LobbyUserInfo(jsonArray.getJSONObject(i));
                list.put(obj.getId(), obj);
            }
        }

        return list;
    }
}
