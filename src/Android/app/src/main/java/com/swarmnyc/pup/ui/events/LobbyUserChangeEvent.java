package com.swarmnyc.pup.ui.events;

public class LobbyUserChangeEvent
{
    private String lobbyId;

    public LobbyUserChangeEvent(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getLobbyId() {
        return lobbyId;
    }
}
