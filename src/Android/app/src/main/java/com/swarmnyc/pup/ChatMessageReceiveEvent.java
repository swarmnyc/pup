package com.swarmnyc.pup;

import com.swarmnyc.pup.chat.ChatMessage;

import java.util.List;

public class ChatMessageReceiveEvent {
    private String  lobbyId;
    private boolean m_newMessage;
    private List<ChatMessage> messages;

    public ChatMessageReceiveEvent( String lobbyId, boolean newMessage, List<ChatMessage> messages )
    {
        this.lobbyId = lobbyId;
        m_newMessage = newMessage;
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
