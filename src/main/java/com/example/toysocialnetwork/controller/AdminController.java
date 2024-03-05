package com.example.toysocialnetwork.controller;

import com.example.toysocialnetwork.TSNApplication;
import com.example.toysocialnetwork.controller.TSNAction.TSN_Action;
import com.example.toysocialnetwork.domain.*;
import com.example.toysocialnetwork.observer.Observer;
import com.example.toysocialnetwork.repository.paging.Page;
import com.example.toysocialnetwork.repository.paging.Pageable;
import com.example.toysocialnetwork.service.AccountService;
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
import java.util.List;
import java.util.stream.StreamSupport;

public class AdminController implements Observer<AdminEvent> {

    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private AccountService accountService;
    public Stage modalStage;

    public void setService(UserService userService, FriendshipService friendshipService, MessageService messageService,AccountService accountService, Stage dialogStage) {
        this.userService = userService;
        userService.addObserver(this);
        initUsersData();
        this.friendshipService = friendshipService;
        friendshipService.addObserver(this);
        initFriendshipsData();
        this.messageService = messageService;
        messageService.addObserver(this);
        initMessagesData();
        this.accountService = accountService;
        initAccountsData();
        modalStage = dialogStage;
    }

    @FXML
    TableView<User> userTableView;
    @FXML
    TableColumn<User, Long> userIdColumn;
    @FXML
    TableColumn<User, String> userFirstNameColumn;
    @FXML
    TableColumn<User, String> userLastNameColumn;
    @FXML
    TableColumn<User, String> userEmailColumn;
    @FXML
    TableColumn<User, LocalDateTime> userDateOfBirthColumn;
    @FXML
    TableColumn<User, String> userGenderColumn;
    @FXML
    TableColumn<User, String> userCountryColumn;
    @FXML
    TableColumn<User, String> userCityColumn;
    @FXML
    TableColumn<User, String> userPhoneNumberColumn;
    ObservableList<User> users_model = FXCollections.observableArrayList();

    public int currentPage = 0;
    public int elementsPerPage = 5;
    public int totalNumberOfElements = 0;

    @FXML
    TextField page;

    @FXML
    Button previousButton;

    @FXML
    Button nextButton;

    public void initUsersData(){
        Page<User> usersPage = userService.usersOnPage(new Pageable(currentPage, elementsPerPage));
        totalNumberOfElements = usersPage.getTotalNumberOfElements();
        handlePagingNavigationChecks();
        List<User> userList = StreamSupport.stream(usersPage.getElementsOnPage().spliterator(), false).toList();
        //System.out.println(userList);
        users_model.setAll(userList);
        /*Iterable<User> usersIterable = userService.getAll();
        List<User> usersList = StreamSupport.stream(usersIterable.spliterator(), false).toList();
        users_model.setAll(usersList);*/
    }

    public void elementsPerPageChanged(){
        try {
            this.elementsPerPage = Integer.parseInt(page.getText());
        }
        catch (Exception e){
            this.elementsPerPage = 5;
        }
        this.currentPage = 0;
        initUsersData();
    }

    public void goToPreviousPage(){
        currentPage--;
        initUsersData();
    }

    public void goToNextPage(){
        currentPage++;
        initUsersData();
    }

    private void handlePagingNavigationChecks(){
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * elementsPerPage >= totalNumberOfElements);
    }

    @FXML
    TableView<Friendship> friendshipTableView;
    @FXML
    TableColumn<Friendship, String> friendshipIdFirstUserColumn;
    @FXML
    TableColumn<Friendship, String> friendshipIdSecondUserColumn;
    @FXML
    TableColumn<Friendship, LocalDateTime> friendshipFriendsFromColumn;
    @FXML
    TableColumn<Friendship, String> friendshipStateColumn;
    ObservableList<Friendship> friendship_model = FXCollections.observableArrayList();

    public int currentPageF = 0;
    public int elementsPerPageF = 5;
    public int totalNumberOfElementsF = 0;

    @FXML
    TextField pageF;

    @FXML
    Button previousButtonF;

    @FXML
    Button nextButtonF;

    public void initFriendshipsData(){
        Page<Friendship> friendshipsPage = friendshipService.friendshipsOnPage(new Pageable(currentPageF, elementsPerPageF));
        totalNumberOfElementsF = friendshipsPage.getTotalNumberOfElements();
        handlePagingNavigationChecksF();
        List<Friendship> friendshipList = StreamSupport.stream(friendshipsPage.getElementsOnPage().spliterator(), false).toList();
        //System.out.println(userList);
        friendship_model.setAll(friendshipList);
        /*Iterable<Friendship> friendshipIterable = friendshipService.getAll();
        List<Friendship> friendshipsList = StreamSupport.stream(friendshipIterable.spliterator(), false).toList();
        friendship_model.setAll(friendshipsList);*/
    }

    public void elementsPerPageChangedF(){
        try {
            this.elementsPerPageF = Integer.parseInt(pageF.getText());
        }
        catch (Exception e){
            this.elementsPerPageF = 5;
        }
        this.currentPageF = 0;
        initFriendshipsData();
    }

    public void goToPreviousPageF(){
        currentPageF--;
        initFriendshipsData();
    }

    public void goToNextPageF(){
        currentPageF++;
        initFriendshipsData();
    }

    private void handlePagingNavigationChecksF(){
        previousButtonF.setDisable(currentPageF == 0);
        nextButtonF.setDisable((currentPageF + 1) * elementsPerPageF >= totalNumberOfElementsF);
    }

    @FXML
    TableView<Message> messageTableView;
    @FXML
    TableColumn<Message, Long> messageIdColumn;
    @FXML
    TableColumn<Message, Long> messageFromColumn;
    @FXML
    TableColumn<Message, List<Long>> messageToColumn;
    @FXML
    TableColumn<Message, String> messageMessageColumn;
    @FXML
    TableColumn<Message, LocalDateTime> messageSentAtColumn;
    @FXML
    TableColumn<Message, List<Long>> messageReplyColumn;
    ObservableList<Message> message_model = FXCollections.observableArrayList();

    public void initMessagesData(){
        Iterable<Message> messageIterable = messageService.getAll();
        List<Message> messagesList = StreamSupport.stream(messageIterable.spliterator(), false).toList();
        message_model.setAll(messagesList);
    }

    @FXML
    TableView<Account> accountsTableView;
    @FXML
    TableColumn<Account, Long> accountAccIdColumn;
    @FXML
    TableColumn<Account, Long> accountIdUserColumn;
    @FXML
    TableColumn<Account, String> accountUsernameColumn;
    @FXML
    TableColumn<Account, String> accountPasswordColumn;
    @FXML
    TableColumn<Account, String> accountAccStateColumn;
    @FXML
    TableColumn<Account, LocalDateTime> accountAccDateColumn;
    @FXML
    TableColumn<Account, LocalDateTime> accountLockedDateColumn;
    @FXML
    TableColumn<Account, Long> accountLockedTimeColumn;

    ObservableList<Account> accounts_model = FXCollections.observableArrayList();

    public void initAccountsData(){
        Iterable<Account> accountIterable = accountService.getAll();
        List<Account> accountsList = StreamSupport.stream(accountIterable.spliterator(), false).toList();
        accounts_model.setAll(accountsList);
    }

    @FXML
    public void initialize(){
        userIdColumn.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        userDateOfBirthColumn.setCellValueFactory(new PropertyValueFactory<User, LocalDateTime>("dateOfBirth"));
        userGenderColumn.setCellValueFactory(new PropertyValueFactory<User, String>("gender"));
        userCountryColumn.setCellValueFactory(new PropertyValueFactory<User, String>("country"));
        userCityColumn.setCellValueFactory(new PropertyValueFactory<User, String>("city"));
        userPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<User, String>("phoneNumber"));
        userTableView.setItems(users_model);
        userTableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        friendshipIdFirstUserColumn.setCellValueFactory(new PropertyValueFactory<Friendship, String>("idFirstUser"));
        friendshipIdSecondUserColumn.setCellValueFactory(new PropertyValueFactory<Friendship, String>("idSecondUser"));
        friendshipFriendsFromColumn.setCellValueFactory(new PropertyValueFactory<Friendship, LocalDateTime>("friendsFrom"));
        friendshipStateColumn.setCellValueFactory(new PropertyValueFactory<Friendship, String>("State"));
        friendshipTableView.setItems(friendship_model);
        messageIdColumn.setCellValueFactory(new PropertyValueFactory<Message, Long>("id"));
        messageFromColumn.setCellValueFactory(new PropertyValueFactory<Message, Long>("from"));
        messageToColumn.setCellValueFactory(new PropertyValueFactory<Message, List<Long>>("to"));
        messageMessageColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));
        messageSentAtColumn.setCellValueFactory(new PropertyValueFactory<Message, LocalDateTime>("sent_at"));
        messageReplyColumn.setCellValueFactory(new PropertyValueFactory<Message, List<Long>>("reply"));
        messageTableView.setItems(message_model);
        accountAccIdColumn.setCellValueFactory(new PropertyValueFactory<Account, Long>("id"));
        accountIdUserColumn.setCellValueFactory(new PropertyValueFactory<Account, Long>("user_id"));
        accountUsernameColumn.setCellValueFactory(new PropertyValueFactory<Account, String>("username"));
        accountPasswordColumn.setCellValueFactory(new PropertyValueFactory<Account, String>("password"));
        accountAccStateColumn.setCellValueFactory(new PropertyValueFactory<Account, String>("state"));
        accountAccDateColumn.setCellValueFactory(new PropertyValueFactory<Account, LocalDateTime>("registered_date"));
        accountLockedDateColumn.setCellValueFactory(new PropertyValueFactory<Account, LocalDateTime>("locked_date"));
        accountLockedTimeColumn.setCellValueFactory(new PropertyValueFactory<Account, Long>("locked_time"));
        accountsTableView.setItems(accounts_model);
    }

    public void initUserEditor(User user){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(TSNApplication.class.getResource("user-entity-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Edit");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            dialogStage.setScene(scene);

            UserEntityController userEntityController = fxmlLoader.getController();
            userEntityController.setUserService(userService, user, dialogStage);
            dialogStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateEvent(){
        initUserEditor(null);
    }

    @FXML
    public void handleDeleteEvent(){
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if(selectedUser == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Delete Error", "Please select a user before pressing delete!");
        }
        else{
            Long selectedUserId = selectedUser.getId();
            try {
                userService.remove(selectedUserId);
                initFriendshipsData();
                TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Delete Status", "Delete: Success");
            }catch (Exception e){
                TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Delete Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
            }
        }
    }

    @FXML
    public void handleUpdateEvent(){
        User toBeUpdated = userTableView.getSelectionModel().getSelectedItem();
        if(toBeUpdated == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Update Error", "Please select a user before trying to update one!");
            return;
        }
        initUserEditor(toBeUpdated);
    }

    @FXML
    public void handleCreateFriendshipEvent(){
        List<User> selectedUsers = userTableView.getSelectionModel().getSelectedItems();
        User firstUser = null;
        User secondUser = null;
        Long firstUserId = null;
        Long secondUserId = null;
        try{
            firstUser = selectedUsers.get(0);
            secondUser = selectedUsers.get(1);
            firstUserId = firstUser.getId();
            secondUserId = secondUser.getId();
        }catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Save Error", "Please select two users before creating a new friendship!");
            return;
        }
        try{
            LocalDateTime friendsFrom = LocalDateTime.now();
            friendshipService.add(firstUserId, secondUserId, friendsFrom);
            TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Save Status", "Save: Success");
        }catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Save Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
        }
    }

    @FXML
    public void handleDeleteFriendshipEvent(){
        Friendship selectedFriendship = friendshipTableView.getSelectionModel().getSelectedItem();
        if(selectedFriendship == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Delete Error", "Please select a friendship before trying to delete one!");
        }
        else{
            Tuple<Long,Long> oldId = new Tuple<>(selectedFriendship.getIdFirstUser(), selectedFriendship.getIdSecondUser());
            try {
                friendshipService.remove(oldId);
                TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Delete Status", "Delete: Success");
            }catch (Exception e){
                TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Delete Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
            }
        }
    }

    @FXML
    public void handleAcceptEvent(){
        Friendship selectedFriendship = friendshipTableView.getSelectionModel().getSelectedItem();
        if(selectedFriendship == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Accept Error", "Please select a friendship before trying change the state of one!");
            return;
        }
        try {
            friendshipService.update(selectedFriendship.getIdFirstUser(), selectedFriendship.getIdSecondUser(), selectedFriendship.getFriendsFrom(), "approved");
            TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Accept Status", "Accept: Success");
        }catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Accept Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
        }
    }

    @FXML
    public void handleRejectEvent(){
        Friendship selectedFriendship = friendshipTableView.getSelectionModel().getSelectedItem();
        if(selectedFriendship == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Reject Error", "Please select a friendship before trying change the state of one!");
            return;
        }
        try{
            friendshipService.update(selectedFriendship.getIdFirstUser(), selectedFriendship.getIdSecondUser(), selectedFriendship.getFriendsFrom(), "rejected");
            TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Reject Status", "Reject: Success");
        }catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Reject Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
        }
    }

    @FXML
    public void handleCNEvent(){
        int cn = friendshipService.communities_number();
        TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Communities Number Status", "Numarul comunitatilor este: " + cn);
    }

    @FXML
    public void handleMSCEvent(){
        List<User> users = friendshipService.most_sociable_community();
        List<Long> idUsers = users.stream().map(Entity::getId).toList();
        TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Most Sociable Community Status", "Cea mai populata comunitate este: " + idUsers);
    }

    public void initMessageEditor(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(TSNApplication.class.getResource("message-entity-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Message Edit");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            dialogStage.setScene(scene);

            MessageEntityController messageEntityController = fxmlLoader.getController();
            messageEntityController.setService(messageService, dialogStage, message_model);
            dialogStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateMessageEvent() { initMessageEditor(); }

    @FXML
    public void handleDeleteMessageEvent(){
        Message selectedMessage = messageTableView.getSelectionModel().getSelectedItem();
        if(selectedMessage == null){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Delete Error", "Please select a message before trying to delete one!");
        }
        else{
            try {
                messageService.remove(selectedMessage.getId());
                TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Delete Status", "Delete: Success");
            }catch (Exception e){
                TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Delete Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
            }
        }
    }

    @Override
    public void update(AdminEvent adminEvent) {
        if(adminEvent.getServiceType().toString().equals("User"))
            initUsersData();
        else if(adminEvent.getServiceType().toString().equals("Friendship"))
            initFriendshipsData();
        else if(adminEvent.getServiceType().toString().equals("Message"))
            initMessagesData();
        else
            initAccountsData();
    }
}
