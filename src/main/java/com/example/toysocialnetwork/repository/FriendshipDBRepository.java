package com.example.toysocialnetwork.repository;

import com.example.toysocialnetwork.domain.Friendship;
import com.example.toysocialnetwork.domain.Tuple;
import com.example.toysocialnetwork.domain.validators.Validator;
import com.example.toysocialnetwork.repository.paging.Page;
import com.example.toysocialnetwork.repository.paging.Pageable;
import com.example.toysocialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDBRepository implements PagingRepository<Tuple<Long,Long>, Friendship> /*RepositoryOptional<Tuple<Long,Long>, Friendship>*/ {
    private String url;
    private String username;
    private String password;

    private Validator<Friendship> validator;

    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        if(id == null)
            throw new IllegalArgumentException("(findOne) Id cannot be null!");

        String findOneSQL = "select * from friendships where id1 = ? and id2 = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(findOneSQL)){

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                String state = resultSet.getString("state");
                Friendship friendship = new Friendship(id.getLeft(), id.getRight(), friendsFrom);
                friendship.setId(id);
                friendship.setState(state);
                return Optional.ofNullable(friendship);
            }
            return Optional.empty();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();

        String findAllSQL = "select * from friendships";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(findAllSQL);
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                String state = resultSet.getString("state");
                Friendship friendship = new Friendship(id1, id2, friendsFrom);
                friendship.setId(new Tuple<>(id1, id2));
                friendship.setState(state);
                friendships.add(friendship);
            }
            return friendships;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if(entity == null){
            throw new IllegalArgumentException("(save) Entity cannot be null!");
        }
        validator.validate(entity);

        String insertSQL = "insert into friendships (id1, id2, friends_from, state) values (?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(insertSQL)){

            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(2, entity.getId().getRight());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setString(4,entity.getState());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        if(id == null){
            throw new IllegalArgumentException("(delete) Id cannot be null!");
        }

        String deleteSQL = "delete from friendships where id1 = ? and id2 = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(deleteSQL)){

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            Optional<Friendship> foundFriendship = findOne(id);
            int response = 0;
            if(foundFriendship.isPresent()){
                response = statement.executeUpdate();
            }
            return response == 0 ? Optional.empty() : foundFriendship;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if(entity == null){
            throw new IllegalArgumentException("(update) Entity cannot be null!");
        }
        validator.validate(entity);

        String updateSQL = "update friendships set friends_from = ?, state = ? where id1 = ? and id2 = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(updateSQL)){

            statement.setTimestamp(1, Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setString(2, entity.getState());
            statement.setLong(3, entity.getId().getLeft());
            statement.setLong(4, entity.getId().getRight());
            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private int getNumberOfElements(){
        int numberOfElements = 0;
        try (var connection = DriverManager.getConnection(url, this.username, this.password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from friendships");
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

    private Page<Friendship> findAllOnPage(Pageable pageable){
        int numberOfElements = getNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize()*pageable.getPageNumber();
        //System.out.println(offset + " ?>= " + numberOfElements);
        if(offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);
        // prereq: create empty set
        List<Friendship> friendships = new ArrayList<>();
        // pas 1: conectarea la baza de date
        try (var connection = DriverManager.getConnection(url, this.username, this.password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select * from friendships limit ? offset ?")) {

            statement.setInt(2, offset);
            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();
            // pas 3: process result set
            while (resultSet.next()){
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                String state = resultSet.getString("state");
                Friendship friendship = new Friendship(id1, id2, friendsFrom);
                friendship.setId(new Tuple<>(id1, id2));
                friendship.setState(state);
                friendships.add(friendship);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // pas 3: return result list
        return new Page<>(friendships, numberOfElements);
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        return findAllOnPage(pageable);
    }

}
