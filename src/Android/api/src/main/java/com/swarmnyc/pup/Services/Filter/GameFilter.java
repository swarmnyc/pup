package com.swarmnyc.pup.Services.Filter;

import com.swarmnyc.pup.models.GamePlatform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GameFilter extends BaseFilter {
    HashSet<GamePlatform> platformList = new HashSet<>();


    public void setPlatformList( final List<GamePlatform> platformList )
    {
        this.platformList.clear();
        this.platformList.addAll( platformList );
    }

    public void addGamePlatform(GamePlatform platform) {
        platformList.add(platform);
    }

    public void removeGamePlatform( GamePlatform platform ) {
        platformList.remove(platform);
    }

    public HashSet<GamePlatform> getPlatforms() {
        return platformList;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();

        return map;
    }
}