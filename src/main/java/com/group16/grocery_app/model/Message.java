package com.group16.grocery_app.model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private LocalDateTime sentTime;

    public Message(int id, int senderId, int receiverId, String content, LocalDateTime sentTime) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentTime = sentTime;
    }

    public int getId() { return id; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public LocalDateTime getSentTime() { return sentTime; }
}

