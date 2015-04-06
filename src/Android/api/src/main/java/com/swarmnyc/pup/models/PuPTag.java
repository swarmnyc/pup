package com.swarmnyc.pup.models;

public class PuPTag {
    String key;
    String value;

    /*public PuPTag(JSONObject json) throws JSONException {
        key = json.getString("key");
        value = json.getString("value");
    }*/

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    /*public static Hashtable<String, PuPTag> FromJsonArray(JSONArray jsonArray) throws JSONException {
        Hashtable<String, PuPTag> list = new Hashtable<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            PuPTag obj = new PuPTag(jsonArray.getJSONObject(i));
            list.put(obj.getKey(), obj);
        }

        return list;
    }*/
}
