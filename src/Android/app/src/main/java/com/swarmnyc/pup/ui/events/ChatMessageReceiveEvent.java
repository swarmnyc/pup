package com.swarmnyc.pup.ui.events;

import com.swarmnyc.pup.module.chat.ChatMessage;

import java.util.List;

public class ChatMessageReceiveEvent {
    private String lobbyId;
    private boolean           m_newMessage;
    private List<ChatMessage> messages;
    public boolean handled = false;

    public ChatMessageReceiveEvent( String lobbyId, boolean newMessage, List<ChatMessage> messages )
    {
        this.lobbyId = lobbyId;
        this.m_newMessage = newMessage;
        this.messages = messages;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public boolean isNewMessage()
    {
        return m_newMessage;
    }
}
