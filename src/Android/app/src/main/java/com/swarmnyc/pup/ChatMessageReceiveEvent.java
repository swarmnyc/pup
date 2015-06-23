package com.swarmnyc.pup;

import com.swarmnyc.pup.chat.ChatMessage;

import java.util.List;

public class ChatMessageReceiveEvent {
    private String lobbyId;
    private List<ChatMessage> messages;

    public ChatMessageReceiveEvent(String lobbyId, List<ChatMessage> messages) {
        this.lobbyId = lobbyId;
        this.messages = messages;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public String getLobbyId() {
        return lobbyId;
    }
}
