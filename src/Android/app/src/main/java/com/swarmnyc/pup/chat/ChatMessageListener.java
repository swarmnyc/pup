package com.swarmnyc.pup.chat;

import java.util.List;

public interface ChatMessageListener {
    void receive(List<ChatMessage> message);
}
