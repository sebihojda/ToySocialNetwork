package com.example.toysocialnetwork.controller;

import com.example.toysocialnetwork.TSNApplication;
import com.example.toysocialnetwork.controller.TSNAction.TSN_Action;
import com.example.toysocialnetwork.domain.Account;
import com.example.toysocialnetwork.domain.User;
import com.example.toysocialnetwork.service.AccountService;
import com.example.toysocialnetwork.service.FriendshipService;
import com.example.toysocialnetwork.service.MessageService;
import com.example.toysocialnetwork.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;

    private User user;

    private AccountService accountService;

    private UserService userService;

    private FriendshipService friendshipService;

    private MessageService messageService;

    public void setMainService(AccountService accountService, UserService userService, FriendshipService friendshipService, MessageService messageService){
        this.accountService = accountService;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
    }

    public void handleSignUpEvent(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(TSNApplication.class.getResource("sign-up-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("SignUp Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            dialogStage.setScene(scene);

            SignUpController signUpController = fxmlLoader.getController();
            signUpController.setService(accountService ,userService, dialogStage);
            dialogStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleLoginEvent(){
        String username = null;
        String password = null;
        try {
            username = usernameField.getText();
            password = passwordField.getText();
        }
        catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Login Input Failure","Error:\n" + e.getMessage());
            return;
        }

        if(username.equals("admin") && password.equals("admin")){
            initAdminPage();
        }else {
            try {
                Account account = accountService.findOne(username, password);
                this.user = userService.findOne(account.getUser_id());
                initUserPage(this.user);
            }catch (Exception e){
                TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Login Input Failure","Error:\n" + e.getMessage());
            }
        }
    }

    public void initAdminPage(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(TSNApplication.class.getResource("admin-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Admin Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            dialogStage.setScene(scene);

            AdminController adminController = fxmlLoader.getController();
            adminController.setService(userService, friendshipService, messageService,accountService, dialogStage);
            dialogStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void initUserPage(User user){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(TSNApplication.class.getResource("user-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(user.getFirstName() + " " + user.getLastName() + " Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            dialogStage.setScene(scene);

            UserController userController = fxmlLoader.getController();
            userController.setService(userService, friendshipService, messageService, user, dialogStage);
            dialogStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
