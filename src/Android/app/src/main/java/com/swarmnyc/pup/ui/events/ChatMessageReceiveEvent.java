package com.swarmnyc.pup.ui.events;

import com.swarmnyc.pup.module.chat.ChatMessage;

import java.util.List;

public class ChatMessageReceiveEvent {
    private String            roomId;
    private boolean           m_newMessage;
    private List<ChatMessage> messages;

    public ChatMessageReceiveEvent( String roomId, boolean newMessage, List<ChatMessage> messages )
    {
        this.roomId = roomId;
        this.m_newMessage = newMessage;
        this.messages = messages;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isNewMessage()
    {
        return m_newMessage;
    }
}
