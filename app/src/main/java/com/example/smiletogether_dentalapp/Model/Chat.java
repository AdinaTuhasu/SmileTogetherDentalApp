package com.example.smiletogether_dentalapp.Model;

import java.util.List;

public class Chat {
    private String IdConversation;
    private List<Message> messages;

    public Chat() {
    }

    public Chat(String idConversation, List<Message> messages) {
        IdConversation = idConversation;
        this.messages = messages;
    }

    public String getIdConversation() {
        return IdConversation;
    }

    public void setIdConversation(String idConversation) {
        IdConversation = idConversation;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "IdConversation='" + IdConversation + '\'' +
                ", messages=" + messages +
                '}';
    }
}
