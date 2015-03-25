package com.swarmnyc.pup.chat;

public class ChatMessage {

    private String userId;
    private String body;

    public ChatMessage(String userId, String body) {
        this.userId = userId;
        this.body = body;
    }

    public String getUserId() {
        return userId;
    }

    public String getBody() {
        return body;
    }
}
