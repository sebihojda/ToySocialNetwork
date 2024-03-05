package com.example.toysocialnetwork.repository;

import com.example.toysocialnetwork.domain.Message;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MessageDBRepository implements RepositoryOptional<Long, Message>{

    private String url;
    private String username;
    private String password;

    public MessageDBRepository(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Long aLong) {
        if (aLong == null) {
            throw new IllegalArgumentException("(findOne) Id cannot be null!");
        }

        String findOneSQL = "select * from messages where id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(findOneSQL)) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long from = Long.valueOf(resultSet.getString("from_c"));
                String to = resultSet.getString("to_c");
                List<Long> to_split = Arrays.stream(to.split(",")).map(Long::parseLong).toList();
                String message = resultSet.getString("message_c");
                LocalDateTime sent_at = resultSet.getTimestamp("sent_at").toLocalDateTime();
                String reply = resultSet.getString("reply");
                List<Long> reply_split = Arrays.stream(reply.split(",")).map(Long::parseLong).toList();
                Message new_message = new Message(from, to_split, message, sent_at, reply_split);
                new_message.setId(aLong);
                return Optional.ofNullable(new_message);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        String findAllSQL = "select * from messages order by id asc";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(findAllSQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                Long from = Long.valueOf(resultSet.getString("from_c"));
                String to = resultSet.getString("to_c");
                List<Long> to_split = Arrays.stream(to.split(",")).map(Long::parseLong).toList();
                String message = resultSet.getString("message_c");
                LocalDateTime sent_at = resultSet.getTimestamp("sent_at").toLocalDateTime();
                String reply = resultSet.getString("reply");
                List<Long> reply_split = Arrays.stream(reply.split(",")).map(Long::parseLong).toList();
                Message new_message = new Message(/*id,*/ from, to_split, message, sent_at, reply_split);
                new_message.setId(id);
                messages.add(new_message);
            }
            return messages;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        if (entity == null)
            throw new IllegalArgumentException("(save) Entity cannot be null!");
        //validator.validate(entity);

        String insertSQL = "insert into messages (from_c, to_c, message_c, sent_at, reply) values (?, ?, ?, ?, ?)";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {

            //System.out.println(entity.getFrom());
            statement.setLong(1, entity.getFrom());
            List<String> list_string_to = entity.getTo().stream().map(l -> Long.toString(l)).toList();
            String to = String.join(",", list_string_to);
            //System.out.println(to);
            statement.setString(2, to);
            //System.out.println(entity.getMessage());
            statement.setString(3, entity.getMessage());
            //System.out.println(entity.getSent_at().format(formatter));
            statement.setTimestamp(4, Timestamp.valueOf(entity.getSent_at()));
            List<String> list_string_reply = entity.getReply().stream().map(l -> Long.toString(l)).toList();
            String reply = String.join(",", list_string_reply);
            //System.out.println(reply);
            statement.setString(5, reply);
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        if (aLong == null) {
            throw new IllegalArgumentException("(delete) Id cannot be null!");
        }

        String deleteSQL = "delete from messages where id = ?";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(deleteSQL)) {

            statement.setLong(1, aLong);
            Optional<Message> foundMessage = findOne(aLong);
            int response = 0;
            if (foundMessage.isPresent()) {
                response = statement.executeUpdate();
            }
            return response == 0 ? Optional.empty() : foundMessage;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {
        if(entity == null){
            throw new IllegalArgumentException("(update) Entity cannot be null!");
        }
        //validator.validate(entity);

        String updateSQL="update messages set from_c = ?, to_c = ?, message_c = ?, sent_at = ?, reply = ? where id = ?";
        try(var connection = DriverManager.getConnection(url, this.username, this.password);
            PreparedStatement statement=connection.prepareStatement(updateSQL)) {

            statement.setLong(1, entity.getFrom());
            List<String> list_string_to = entity.getTo().stream().map(l -> Long.toString(l)).toList();
            String to = String.join(",", list_string_to);
            statement.setString(2, to);
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getSent_at()));
            List<String> list_string_reply = entity.getReply().stream().map(l -> Long.toString(l)).toList();
            String reply = String.join(",", list_string_reply);
            statement.setString(5, reply);
            statement.setLong(6, entity.getId());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
