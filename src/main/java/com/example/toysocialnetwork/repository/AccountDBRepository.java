package com.example.toysocialnetwork.repository;

import com.example.toysocialnetwork.domain.Account;
import com.example.toysocialnetwork.domain.User;
import com.example.toysocialnetwork.domain.validators.Validator;
import com.example.toysocialnetwork.repository.paging.Page;
import com.example.toysocialnetwork.repository.paging.Pageable;
import com.example.toysocialnetwork.repository.paging.PagingRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDBRepository implements PagingRepository<Long, Account> {

    private String url;
    private String username;
    private String password;

    private Validator<Account> validator;

    public AccountDBRepository(String url, String username, String password, Validator<Account> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Account> findOne(Long aLong) {
        if (aLong == null) {
            throw new IllegalArgumentException("(findOne) Id cannot be null!");
        }

        String findOneSQL = "select * from accounts where acc_id = ?";
        try(Connection connection = DriverManager.getConnection(url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(findOneSQL)) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id_user = resultSet.getLong("id_user");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String state = resultSet.getString("acc_state");
                LocalDateTime registered_date = resultSet.getTimestamp("acc_date").toLocalDateTime();
                LocalDateTime locked_date = null;
                try {
                    locked_date = resultSet.getTimestamp("locked_date").toLocalDateTime();
                }catch (Exception ex){
                }
                Long locked_time = resultSet.getLong("locked_time");
                Account account = new Account(id_user, username, password,registered_date);
                account.setId(aLong);
                account.setState(state);
                account.setLocked_date(locked_date);
                account.setLocked_time(locked_time);
                return Optional.ofNullable(account);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String password_encryption(String password){
        String encryptedpassword = null;
        try
        {
            /* MessageDigest instance for MD5. */
            MessageDigest m = MessageDigest.getInstance("MD5");

            /* Add plain-text password bytes to digest using MD5 update() method. */
            m.update(password.getBytes());

            /* Convert the hash value into bytes */
            byte[] bytes = m.digest();

            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder s = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            /* Complete hashed password in hexadecimal format */
            encryptedpassword = s.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return encryptedpassword;
    }

    public Optional<Account> findOne(String username, String password){
        if(username == null || password == null){
            throw new IllegalArgumentException("(findOne) Username or password cannot be null!");
        }
        String md5password = password_encryption(password);
        String findOneSQL = "select * from accounts where username = ? and password = ?";
        try(Connection connection = DriverManager.getConnection(url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(findOneSQL)) {

            statement.setString(1, username);
            statement.setString(2, md5password);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long acc_id = resultSet.getLong("acc_id");
                Long id_user = resultSet.getLong("id_user");
                String state = resultSet.getString("acc_state");
                LocalDateTime registered_date = resultSet.getTimestamp("acc_date").toLocalDateTime();
                LocalDateTime locked_date = null;
                try {
                    locked_date = resultSet.getTimestamp("locked_date").toLocalDateTime();
                }catch (Exception ex){
                }
                Long locked_time = resultSet.getLong("locked_time");
                Account account = new Account(id_user, username, md5password,registered_date);
                account.setId(acc_id);
                account.setState(state);
                account.setLocked_date(locked_date);
                account.setLocked_time(locked_time);
                return Optional.ofNullable(account);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Account> findAll() {
        List<Account> accounts = new ArrayList<>();

        String findAllSQL = "select * from accounts order by acc_id asc";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(findAllSQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next())
            {
                Long acc_id = resultSet.getLong("acc_id");
                Long id_user = resultSet.getLong("id_user");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String state = resultSet.getString("acc_state");
                LocalDateTime registered_date = resultSet.getTimestamp("acc_date").toLocalDateTime();
                LocalDateTime locked_date = null;
                try {
                    locked_date = resultSet.getTimestamp("locked_date").toLocalDateTime();
                }catch (Exception ex){
                }
                Long locked_time = resultSet.getLong("locked_time");
                Account account = new Account(id_user, username, password,registered_date);
                account.setId(acc_id);
                account.setState(state);
                account.setLocked_date(locked_date);
                account.setLocked_time(locked_time);
                accounts.add(account);
            }
            return accounts;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Account> save(Account entity) {
        if (entity == null)
            throw new IllegalArgumentException("(save) Entity cannot be null!");

        validator.validate(entity);

        String insertSQL = "insert into accounts (id_user, username, password, acc_date) values (?, ?, ?, ?)";
        try (var connection = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {

            statement.setLong(1, entity.getUser_id());
            statement.setString(2, entity.getUsername());
            statement.setString(3, password_encryption(entity.getPassword()));
            statement.setTimestamp(4, Timestamp.valueOf(entity.getRegistered_date()));
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Account> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Account> update(Account entity) {
        return Optional.empty();
    }

    @Override
    public Page<Account> findAll(Pageable pageable) {
        return null;
    }
}
