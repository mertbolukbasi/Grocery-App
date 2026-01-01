package com.group16.grocery_app.db.repository;

import com.group16.grocery_app.db.Database;
import com.group16.grocery_app.model.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.group16.grocery_app.model.ConversationUser;

public class MessageRepository {
    private final Connection connection;

    public MessageRepository() {
        this.connection = Database.getInstance().getConnection();
    }

    public boolean sendMessage(int senderId, int receiverId, String content) throws SQLException {
        String query = "INSERT INTO Messages (senderID, receiverID, content, sent_time) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public ObservableList<Message> getMessagesBetween(int userId1, int userId2) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM Messages WHERE (senderID = ? AND receiverID = ?) OR (senderID = ? AND receiverID = ?) ORDER BY sent_time ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Timestamp sentTime = rs.getTimestamp("sent_time");
                LocalDateTime sentDateTime = sentTime != null ? sentTime.toLocalDateTime() : null;

                messages.add(new Message(
                        rs.getInt("messageID"),
                        rs.getInt("senderID"),
                        rs.getInt("receiverID"),
                        rs.getString("content"),
                        sentDateTime
                ));
            }
        }

        return FXCollections.observableArrayList(messages);
    }

    public ObservableList<String> getConversations(int userId) throws SQLException {
        List<String> conversations = new ArrayList<>();
        String query = "SELECT DISTINCT u.username, u.userID FROM UserInfo u " +
                "INNER JOIN Messages m ON (u.userID = m.senderID OR u.userID = m.receiverID) " +
                "WHERE (m.senderID = ? OR m.receiverID = ?) AND u.userID != ? " +
                "ORDER BY u.username";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                conversations.add(rs.getString("username"));
            }
        }

        return FXCollections.observableArrayList(conversations);
    }

    public List<ConversationUser> getConversationUserIds(int userId) throws SQLException {
        List<ConversationUser> userIdList = new ArrayList<>();
        String query = "SELECT DISTINCT u.username, u.userID FROM UserInfo u " +
                "INNER JOIN Messages m ON (u.userID = m.senderID OR u.userID = m.receiverID) " +
                "WHERE (m.senderID = ? OR m.receiverID = ?) AND u.userID != ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userIdList.add(new ConversationUser(rs.getString("username"), rs.getInt("userID")));
            }
        }

        return userIdList;
    }
}

