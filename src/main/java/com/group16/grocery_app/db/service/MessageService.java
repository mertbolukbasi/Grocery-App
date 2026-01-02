package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.MessageRepository;
import com.group16.grocery_app.model.Message;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;
import com.group16.grocery_app.model.ConversationUser;

/**
 * Service class for message-related operations.
 * Provides a higher-level API over {@link MessageRepository} and handles SQL exceptions internally.
 *
 * @author Yiğit Emre Ünlüçerçi
 */
public class MessageService {
    private final MessageRepository messageRepository;

    /**
     * Creates a new MessageService instance and initializes its repository dependency.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    public MessageService() {
        this.messageRepository = new MessageRepository();
    }

    /**
     * Sends a message from one user to another.
     *
     * @param senderId sender user ID
     * @param receiverId receiver user ID
     * @param content message content
     * @return true if the message is sent successfully; false if an error occurs
     * @author Yiğit Emre Ünlüçerçi
     */
    public boolean sendMessage(int senderId, int receiverId, String content) {
        try {
            return messageRepository.sendMessage(senderId, receiverId, content);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all messages exchanged between two users ordered by send time.
     *
     * @param userId1 first user ID
     * @param userId2 second user ID
     * @return observable list of messages; returns an empty list if an error occurs
     * @author Yiğit Emre Ünlüçerçi
     */
    public ObservableList<Message> getMessagesBetween(int userId1, int userId2) {
        try {
            return messageRepository.getMessagesBetween(userId1, userId2);
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    /**
     * Lists usernames of all users who have an existing conversation with the given user.
     *
     * @param userId the user ID to list conversation partners for
     * @return observable list of usernames; returns an empty list if an error occurs
     * @author Yiğit Emre Ünlüçerçi
     */
    public ObservableList<String> getConversations(int userId) {
        try {
            return messageRepository.getConversations(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    /**
     * Returns conversation partners for the given user as (username, userId) pairs.
     *
     * @param userId the user ID to list conversation partners for
     * @return list of ConversationUser objects; returns an empty list if an error occurs
     * @author Yiğit Emre Ünlüçerçi
     */
    public List<ConversationUser> getConversationUserIds(int userId) {
        try {
            return messageRepository.getConversationUserIds(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
}
