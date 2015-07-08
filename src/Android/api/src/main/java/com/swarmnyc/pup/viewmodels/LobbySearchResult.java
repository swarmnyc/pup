package com.swarmnyc.pup.viewmodels;

import com.swarmnyc.pup.models.Lobby;

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
