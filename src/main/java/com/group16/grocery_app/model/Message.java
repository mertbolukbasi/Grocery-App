package com.group16.grocery_app.model;

import java.time.LocalDateTime;

/**
 * Model class representing a chat message between two users.
 * Stores sender/receiver IDs, message content, and send timestamp.
 *
 * @author Yiğit Emre Ünlüçerçi
 */
public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private LocalDateTime sentTime;

    /**
     * Creates a new Message instance.
     *
     * @param id unique message ID
     * @param senderId sender user ID
     * @param receiverId receiver user ID
     * @param content message content
     * @param sentTime time when the message was sent
     * @author Yiğit Emre Ünlüçerçi
     */

    public Message(int id, int senderId, int receiverId, String content, LocalDateTime sentTime) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentTime = sentTime;
    }

    /**
     * Returns the message ID.
     *
     * @return message ID
     * @author Yiğit Emre Ünlüçerçi
     */
    public int getId() { return id; }

    /**
     * Returns the sender user ID.
     *
     * @return sender ID
     * @author Yiğit Emre Ünlüçerçi
     */
    public int getSenderId() { return senderId; }

    /**
     * Returns the receiver user ID.
     *
     * @return receiver ID
     * @author Yiğit Emre Ünlüçerçi
     */
    public int getReceiverId() { return receiverId; }

    /**
     * Returns the message content.
     *
     * @return message content
     * @author Yiğit Emre Ünlüçerçi
     */
    public String getContent() { return content; }

    /**
     * Returns the time when the message was sent.
     *
     * @return send time
     * @author Yiğit Emre Ünlüçerçi
     */
    public LocalDateTime getSentTime() { return sentTime; }
}
