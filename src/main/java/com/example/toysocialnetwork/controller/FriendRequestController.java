package com.example.toysocialnetwork.controller;

import com.example.toysocialnetwork.controller.TSNAction.TSN_Action;
import com.example.toysocialnetwork.domain.Friendship;
import com.example.toysocialnetwork.domain.Tuple;
import com.example.toysocialnetwork.domain.User;
import com.example.toysocialnetwork.dto.FriendshipDTO;
import com.example.toysocialnetwork.observer.Observer;
import com.example.toysocialnetwork.service.FriendshipService;
import com.example.toysocialnetwork.service.UserService;
import com.example.toysocialnetwork.utils.AdminEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

public class FriendRequestController implements Observer<AdminEvent> {
    @FXML
    TableView<User> usersTableView;
    @FXML
    TableColumn<User, String> userFirstNameColumn;
    @FXML
    TableColumn<User, String> userLastNameColumn;

    private UserService userService;
    private FriendshipService friendshipService;
    private User user;
    public Stage modalStage;
    private ObservableList<FriendshipDTO> friends_model = FXCollections.observableArrayList();
    private ObservableList<User> users_model = FXCollections.observableArrayList();

    public void setFriendRequestService(UserService userService, FriendshipService friendshipService, User user, Stage dialogStage, ObservableList<FriendshipDTO> friends_model){
        this.userService = userService;
        userService.addObserver(this);
        this.friendshipService = friendshipService;
        friendshipService.addObserver(this);
        this.friends_model = friends_model;
        this.user = user;
        initPendingData();
        initUsersData();
        this.modalStage = dialogStage;
    }

    public void initUsersData(){
        Iterable<User> usersIterable = userService.getAll();
        List<User> friends = friends_model.stream().map(FriendshipDTO::getFriend).toList();
        List<User> usersList = new java.util.ArrayList<>(StreamSupport.stream(usersIterable.spliterator(), false).filter(user -> !friends.contains(user) && !pendings_model.stream().map(FriendshipDTO::getFriend).toList().contains(user)).toList());
        usersList.remove(this.user);
        users_model.setAll(usersList);
    }

    @FXML
    TableView<FriendshipDTO> pendingTableView;
    @FXML
    TableColumn<FriendshipDTO, String> pendingColumn;
    private ObservableList<FriendshipDTO> pendings_model = FXCollections.observableArrayList();

    public FriendshipDTO toFriendshipDTO(Friendship friendship){
        User friend = null;
        if(friendship.getId().getRight() == user.getId()){
            friend = userService.findOne(friendship.getId().getLeft());
        }else {
            friend = userService.findOne(friendship.getId().getRight());
        }
        String friendName = friend.getFirstName() + " " + friend.getLastName();
        FriendshipDTO friendshipDTO = new FriendshipDTO(friend, friendName);
        friendshipDTO.setIdFirstUser(friendship.getIdFirstUser());
        friendshipDTO.setIdSecondUser(friendship.getIdSecondUser());
        friendshipDTO.setId(new Tuple<>(friendship.getIdFirstUser(), friendship.getIdSecondUser()));
        friendshipDTO.setFriendsFrom(friendship.getFriendsFrom());
        friendshipDTO.setState(friendship.getState());
        return friendshipDTO;
        //return new FriendshipDTO(firstUserName, secondUserName/*,friendship.getIdFirstUser(),friendship.getIdSecondUser(), friendship.getFriendsFrom(), friendship.getState()*/);
    }

    public void initPendingData(){
        List<Friendship> friendshipList = StreamSupport.stream(friendshipService.getAll().spliterator(), false).toList();
        List<FriendshipDTO> friendshipDTOList = friendshipList.stream().filter(friendship -> friendship.getId().getRight() == user.getId() && friendship.getState().equals("pending")).map(this::toFriendshipDTO).toList();
        pendings_model.setAll(friendshipDTOList);
    }

    @FXML
    public void initialize(){
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        usersTableView.setItems(users_model);
        pendingColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendName"));
        pendingTableView.setItems(pendings_model);
    }
    @FXML
    public void handleSendRequest(){
        User friend = usersTableView.getSelectionModel().getSelectedItem();
        if(friend == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "User Selection Error", "Please select a user before sending a request!");
        }
        else{
            try {
                friendshipService.add(this.user.getId(), friend.getId(), LocalDateTime.now());
                //initUsersData();
            }catch (Exception e){
                TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Send Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
            }
        }
    }

    @FXML
    public void handleAcceptEvent(){
        FriendshipDTO selectedFriendshipDTO = pendingTableView.getSelectionModel().getSelectedItem();
        if(selectedFriendshipDTO == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Accept Selection Error", "Please select a pending friend before trying to accept one!");
        }
        else{
            friendshipService.update(selectedFriendshipDTO.getIdFirstUser(), selectedFriendshipDTO.getIdSecondUser(), selectedFriendshipDTO.getFriendsFrom(), "approved");
            //initPendingData();
        }
    }

    @FXML
    public void handleRejectEvent(){
        FriendshipDTO selectedFriendshipDTO = pendingTableView.getSelectionModel().getSelectedItem();
        if(selectedFriendshipDTO == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Reject Selection Error", "Please select a pending friend before trying to reject one!");
        }
        else{
            friendshipService.update(selectedFriendshipDTO.getIdFirstUser(), selectedFriendshipDTO.getIdSecondUser(), selectedFriendshipDTO.getFriendsFrom(), "rejected");
            //initPendingData();
        }
    }

    @Override
    public void update(AdminEvent adminEvent) {
        if(adminEvent.getServiceType().toString().equals("Friendship")){
            initPendingData();
            initUsersData();
        }
    }
}
