package com.swarmnyc.pup.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public abstract class Taggable {
    private Hashtable<String, PuPTag> tags;

    public Taggable() {
        tags = new Hashtable<>();
    }

    public Taggable(JSONObject json) throws JSONException {
        tags = PuPTag.FromJsonArray(json.getJSONArray("tags"));
    }

    public String getTagValue(String key) {
        if (tags.containsKey(key)) {
            return tags.get(key).value;
        } else {
            return null;
        }
    }
}
