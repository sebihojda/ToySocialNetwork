package com.example.toysocialnetwork.repository;

import com.example.toysocialnetwork.domain.User;
import com.example.toysocialnetwork.domain.validators.Validator;
import com.example.toysocialnetwork.repository.paging.Page;
import com.example.toysocialnetwork.repository.paging.Pageable;
import com.example.toysocialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDBRepository implements PagingRepository<Long, User>{
    private String url;
    private String username;
    private String password;

    private Validator<User> validator;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<User> findOne(Long longID) {
        if (longID == null) {
            throw new IllegalArgumentException("(findOne) Id cannot be null!");
        }

        String findOneSQL = "select * from users where user_id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(findOneSQL)) {

            statement.setLong(1, longID);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phone_number");
                LocalDateTime dateOfBirth = resultSet.getTimestamp("date_of_birth").toLocalDateTime();
                String gender = resultSet.getString("gender");
                String country = resultSet.getString("country");
                String city = resultSet.getString("city");
                User user = new User(firstName, lastName, email, phoneNumber, dateOfBirth, gender, country, city);
                user.setId(longID);
                return Optional.ofNullable(user);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*public Optional<User> findOne(String email, String phoneNumber) {
        if(email == null || phoneNumber == null){
            throw new IllegalArgumentException("(findOne) Email or phoneNumber cannot be null!");
        }

        String findOneSQL = "select * from users where email = ? and phone_number = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(findOneSQL)) {

            statement.setString(1, email);
            statement.setString(2, phoneNumber);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long longID = resultSet.getLong("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                LocalDateTime dateOfBirth = resultSet.getTimestamp("date_of_birth").toLocalDateTime();
                String gender = resultSet.getString("gender");
                String country = resultSet.getString("country");
                String city = resultSet.getString("city");
                User user = new User(firstName, lastName, email, phoneNumber, dateOfBirth, gender, country, city);
                user.setId(longID);
                return Optional.ofNullable(user);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    public Optional<Long> findOne(String email, String phoneNumber){
        if(email == null || phoneNumber == null){
            throw new IllegalArgumentException("(findOne) Email or phoneNumber cannot be null!");
        }
        String findOneSQL = "select * from users where email = ? and phone_number = ?";
        try(Connection connection = DriverManager.getConnection(url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(findOneSQL)) {

            statement.setString(1, email);
            statement.setString(2, phoneNumber);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id_user = resultSet.getLong("user_id");
                return Optional.ofNullable(id_user);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();

        String findAllSQL = "select * from users order by user_id asc";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(findAllSQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next())
            {
                Long user_id = resultSet.getLong("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phone_number");
                LocalDateTime dateOfBirth = resultSet.getTimestamp("date_of_birth").toLocalDateTime();
                String gender = resultSet.getString("gender");
                String country = resultSet.getString("country");
                String city = resultSet.getString("city");
                User user = new User(firstName, lastName, email, phoneNumber, dateOfBirth, gender, country, city);
                user.setId(user_id);
                users.add(user);
            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> save(User user) {
        if (user == null)
            throw new IllegalArgumentException("(save) User cannot be null!");

        validator.validate(user);

        String insertSQL = "insert into users (first_name, last_name, email, date_of_birth, gender, country, city, phone_number) values (?, ?, ?, ?, ?, ?, ?, ?)";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {

            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setDate(4, Date.valueOf(user.getDateOfBirth().toLocalDate()));
            statement.setString(5, user.getGender());
            statement.setString(6, user.getCountry());
            statement.setString(7, user.getCity());
            statement.setString(8, user.getPhoneNumber());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(user) : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> delete(Long longID) {
        if (longID == null) {
            throw new IllegalArgumentException("(delete) Id cannot be null!");
        }

        String deleteSQL = "delete from users where user_id = ?";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(deleteSQL)) {

            statement.setLong(1, longID);
            Optional<User> foundUser = findOne(longID);
            int response = 0;
            if (foundUser.isPresent()) {
                response = statement.executeUpdate();
            }
            return response == 0 ? Optional.empty() : foundUser;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User user) {
        if(user == null){
            throw new IllegalArgumentException("(update) User cannot be null!");
        }
        validator.validate(user);

        String updateSQL="update users set first_name = ?, last_name = ?, email = ?, date_of_birth = ?, gender = ?, country = ?, city = ?, phone_number = ?  where user_id = ?";
        try(var connection = DriverManager.getConnection(url, this.username, this.password);
            PreparedStatement statement=connection.prepareStatement(updateSQL)) {

            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setDate(4, Date.valueOf(user.getDateOfBirth().toLocalDate()));
            statement.setString(5, user.getGender());
            statement.setString(6, user.getCountry());
            statement.setString(7, user.getCity());
            statement.setString(8, user.getPhoneNumber());
            statement.setLong(9, user.getId());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(user) : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private int getNumberOfElements(){
        int numberOfElements = 0;
        try (var connection = DriverManager.getConnection(url, this.username, this.password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from users");
             ResultSet resultSet = statement.executeQuery()) {

            // pas 3: process result set
            while (resultSet.next()){
                numberOfElements = resultSet.getInt("count");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return numberOfElements;
    }

    private Page<User> findAllOnPage(Pageable pageable){
        int numberOfElements = getNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize()*pageable.getPageNumber();
        //System.out.println(offset + " ?>= " + numberOfElements);
        if(offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);
        // prereq: create empty set
        List<User> users = new ArrayList<>();
        // pas 1: conectarea la baza de date
        try (var connection = DriverManager.getConnection(url, this.username, this.password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?")) {

            statement.setInt(2, offset);
            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();
            // pas 3: process result set
            while (resultSet.next()){
                Long user_id = resultSet.getLong("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phone_number");
                LocalDateTime dateOfBirth = resultSet.getTimestamp("date_of_birth").toLocalDateTime();
                String gender = resultSet.getString("gender");
                String country = resultSet.getString("country");
                String city = resultSet.getString("city");
                User user = new User(firstName, lastName, email, phoneNumber, dateOfBirth, gender, country, city);
                user.setId(user_id);
                // add to set of users
                users.add(user);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // pas 3: return result list
        return new Page<>(users, numberOfElements);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return findAllOnPage(pageable);
    }
}
