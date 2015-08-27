package com.swarmnyc.pup.module.viewmodels;

import com.swarmnyc.pup.module.models.Lobby;

import java.util.List;

public class LobbySearchResult {
    private int[]       counts;
    private List<Lobby> result;

    public int[] getCounts()
    {
        return counts;
    }

    public List<Lobby> getResult()
    {
        return result;
    }
}
