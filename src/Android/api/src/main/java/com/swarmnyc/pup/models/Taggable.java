package com.swarmnyc.pup.models;

import java.util.ArrayList;
import java.util.List;

public abstract class Taggable {
    private List<PuPTag> tags;

    public Taggable() {
        tags = new ArrayList<>();
    }

    public List<PuPTag> getTags() {
        return tags;
    }

    public void setTags(List<PuPTag> tags) {
        this.tags = tags;
    }

    public String getTagValue(String key) {
        for (PuPTag tag : tags) {
            if (tag.key.equals(key)) {
                return tag.value;
            }
        }

        return null;
    }
}
