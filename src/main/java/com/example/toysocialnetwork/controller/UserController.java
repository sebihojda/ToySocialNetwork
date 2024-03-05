package com.example.toysocialnetwork.controller;

import com.example.toysocialnetwork.TSNApplication;
import com.example.toysocialnetwork.controller.TSNAction.TSN_Action;
import com.example.toysocialnetwork.domain.*;
import com.example.toysocialnetwork.dto.FriendshipDTO;
import com.example.toysocialnetwork.observer.Observer;
import com.example.toysocialnetwork.service.FriendshipService;
import com.example.toysocialnetwork.service.MessageService;
import com.example.toysocialnetwork.service.UserService;
import com.example.toysocialnetwork.utils.AdminEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class UserController implements Observer<AdminEvent> {

    private UserService userService;

    private FriendshipService friendshipService;

    private MessageService messageService;

    private User user;

    public Stage modalStage;

    public void setService(UserService userService, FriendshipService friendshipService, MessageService messageService, User user, Stage dialogStage) {
        this.userService = userService;
        userService.addObserver(this);
        this.friendshipService = friendshipService;
        friendshipService.addObserver(this);
        this.messageService = messageService;
        messageService.addObserver(this);
        this.user = user;
        initFriendsData();
        this.modalStage = dialogStage;
    }

    @FXML
    TableView<FriendshipDTO> friendsTableView;
    @FXML
    TableColumn<FriendshipDTO, String> friendNameColumn;
    ObservableList<FriendshipDTO> friends_model = FXCollections.observableArrayList();

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

    public void initFriendsData(){
        List<Friendship> friendshipList = StreamSupport.stream(friendshipService.getAll().spliterator(), false).toList();
        List<FriendshipDTO> friendshipDTOList = friendshipList.stream().filter(friendship -> (friendship.getId().getLeft() == user.getId() || friendship.getId().getRight() == user.getId()) && friendship.getState().equals("approved")).map(this::toFriendshipDTO).toList();
        friends_model.setAll(friendshipDTOList);
    }

    @FXML
    ListView<String> messageListView;
    ObservableList<String> messages_model_list = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        friendNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendName"));
        friendsTableView.setItems(friends_model);
        friendsTableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        //friendsTableView.getSelectionModel().clearAndSelect(0);
    }

    public void initFriendRequestEditor(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(TSNApplication.class.getResource("friend-request-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("FriendRequest Edit");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            dialogStage.setScene(scene);

            FriendRequestController friendRequestController = fxmlLoader.getController();
            friendRequestController.setFriendRequestService(userService, friendshipService, user, dialogStage, friends_model);
            dialogStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void handleFriendRequestEvent(){
        initFriendRequestEditor();
    }

    @FXML
    public void handleRemoveEvent(){
        FriendshipDTO selected_friend = friendsTableView.getSelectionModel().getSelectedItem();
        if(selected_friend == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Remove Error", "Please select a friend before trying to remove one!");
        }
        else{
            //Tuple<Long,Long> oldId = new Tuple<>(selected_friend.getFriend().getId(), this.user.getId());
            try {
                try {
                    friendshipService.remove(new Tuple<>(selected_friend.getFriend().getId(), this.user.getId()));
                }catch (Exception e) {
                    friendshipService.remove(new Tuple<>(this.user.getId(), selected_friend.getFriend().getId()));
                }
                //friends_model.remove(selected_friend);
                TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Remove Status", "Remove: Success");
            }catch (Exception e){
                TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Remove Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
            }
        }
    }

    @FXML
    public void handleRefreshEvent(){
        initFriendsData();
    }

    @FXML
    public void handleLoadEvent(){
        FriendshipDTO friend = friendsTableView.getSelectionModel().getSelectedItem();
        if(friend == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Friend Selection Error", "Please select a friend before loading the list!");
        }
        else{
            List<String> chat = messageService.chat(this.user, friend.getFriend());
            // ar merge un SELECT TOP 5 in repo... numa zic...
            //List<String> last5msgs = chat.subList(Math.max(chat.size() - 5, 0), chat.size());
            //messages_model_list.setAll(last5msgs);
            messages_model_list.setAll(chat);
            messageListView.setItems(messages_model_list);
        }
    }

    @FXML
    TextField sendField;
    public void handleSendEvent(){
        List<FriendshipDTO> friends = friendsTableView.getSelectionModel().getSelectedItems();
        if(friends == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Friend Selection Error", "Please select a friend before trying to send a message!");
        }
        else{
            String message = sendField.getText();
            if (message != null) {
                List<Long> to = new ArrayList<>();
                List<Long> reply = new ArrayList<>();
                friends.forEach(friend -> {
                    to.add(friend.getFriend().getId());
                    List<Message> messages = messageService.sorted_messages(this.user, friend.getFriend());
                    if (!messages.isEmpty())
                        reply.add(messages.get(messages.size() - 1).getId());
                    else
                        reply.add(0L);
                });
                messageService.add(this.user.getId(), to, message, LocalDateTime.now(), reply);
                //handleLoadEvent();
            } else {
                TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Message Error", "Message can not be null!");
            }
        }
    }

    @Override
    public void update(AdminEvent adminEvent) {
        if(adminEvent.getServiceType().toString().equals("Friendship"))
            initFriendsData();
        else if(adminEvent.getServiceType().toString().equals("Message")){
            handleLoadEvent();
        }
    }

}
