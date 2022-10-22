package com.example.pingout;

public class Messages {

    String message;
    String senderId;
    long timestamp;

    public Messages() {
        //required for firebase
    }

    public Messages(String message, String senderId, long timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
