package com.example.smiletogether_dentalapp.Model;

public class Message {
    private String idTransmitter;
    private String idReceiver;
    private String text;
    private boolean messageRead;

    public Message() {
    }

    public Message(String idTransmitter, String idReceiver, String text, boolean messageRead) {
        this.idTransmitter = idTransmitter;
        this.idReceiver = idReceiver;
        this.text = text;
        this.messageRead = messageRead;
    }

    public String getIdTransmitter() {
        return idTransmitter;
    }

    public void setIdTransmitter(String idTransmitter) {
        this.idTransmitter = idTransmitter;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMessageRead() {
        return messageRead;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }

    @Override
    public String toString() {
        return "Message{" +
                "idTransmitter='" + idTransmitter + '\'' +
                ", idReceiver='" + idReceiver + '\'' +
                ", text='" + text + '\'' +
                ", messageRead=" + messageRead +
                '}';
    }
}
