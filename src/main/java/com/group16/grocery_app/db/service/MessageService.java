package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.MessageRepository;
import com.group16.grocery_app.model.Message;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;
import com.group16.grocery_app.model.ConversationUser;

public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService() {
        this.messageRepository = new MessageRepository();
    }

    public boolean sendMessage(int senderId, int receiverId, String content) {
        try {
            return messageRepository.sendMessage(senderId, receiverId, content);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<Message> getMessagesBetween(int userId1, int userId2) {
        try {
            return messageRepository.getMessagesBetween(userId1, userId2);
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    public ObservableList<String> getConversations(int userId) {
        try {
            return messageRepository.getConversations(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    public List<ConversationUser> getConversationUserIds(int userId) {
        try {
            return messageRepository.getConversationUserIds(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
}

