package com.example.toysocialnetwork;

import com.example.toysocialnetwork.config.DatabaseConnectionConfig;
import com.example.toysocialnetwork.controller.MainController;
import com.example.toysocialnetwork.domain.validators.AccountValidator;
import com.example.toysocialnetwork.domain.validators.FriendshipValidator;
import com.example.toysocialnetwork.domain.validators.UserValidator;
import com.example.toysocialnetwork.repository.AccountDBRepository;
import com.example.toysocialnetwork.repository.FriendshipDBRepository;
import com.example.toysocialnetwork.repository.MessageDBRepository;
import com.example.toysocialnetwork.repository.UserDBRepository;
import com.example.toysocialnetwork.service.AccountService;
import com.example.toysocialnetwork.service.FriendshipService;
import com.example.toysocialnetwork.service.MessageService;
import com.example.toysocialnetwork.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TSNApplication extends Application {

    private AccountDBRepository accountDBRepository;
    private AccountService accountService;
    private UserDBRepository userDBRepository;
    private UserService userService;
    private FriendshipDBRepository friendshipDBRepository;
    private FriendshipService friendshipService;
    private MessageDBRepository messageDBRepository;
    private MessageService messageService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        accountDBRepository = new AccountDBRepository(DatabaseConnectionConfig.DB_CONNECTION_URL, DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD, new AccountValidator());
        userDBRepository = new UserDBRepository(DatabaseConnectionConfig.DB_CONNECTION_URL, DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD, new UserValidator());
        friendshipDBRepository = new FriendshipDBRepository(DatabaseConnectionConfig.DB_CONNECTION_URL, DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD, new FriendshipValidator());
        messageDBRepository = new MessageDBRepository(DatabaseConnectionConfig.DB_CONNECTION_URL, DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
        accountService = new AccountService(accountDBRepository);
        userService = new UserService(userDBRepository,friendshipDBRepository);
        friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository);
        messageService = new MessageService(messageDBRepository);
        initView(primaryStage);
        primaryStage.setTitle("ToySocialNetwork");
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TSNApplication.class.getResource("main-view.fxml"));
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1024, 768));
        MainController mainController = fxmlLoader.getController();
        mainController.setMainService(accountService, userService, friendshipService, messageService);
    }

    public static void main(String[] args) {
        launch();
    }
}
