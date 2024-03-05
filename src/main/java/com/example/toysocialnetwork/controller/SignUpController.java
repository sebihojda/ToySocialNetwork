package com.example.toysocialnetwork.controller;

import com.example.toysocialnetwork.controller.TSNAction.TSN_Action;
import com.example.toysocialnetwork.service.AccountService;
import com.example.toysocialnetwork.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public class SignUpController {

    @FXML
    TextField firstNameField;
    @FXML
    TextField lastNameField;
    @FXML
    TextField usernameField;
    @FXML
    TextField emailField;
    @FXML
    TextField phoneField;
    @FXML
    TextField yourPasswordField;
    @FXML
    TextField reEnterPasswordField;
    @FXML
    DatePicker birthdayDatePicker;
    @FXML
    ComboBox<String> genderComboBox;
    @FXML
    TextField countryField;
    @FXML
    TextField cityField;

    ObservableList<String> gender_model = FXCollections.observableArrayList();

    private AccountService accountService;

    private UserService userService;

    private Stage modalStage;

    public void setService(AccountService accountService ,UserService userService, Stage modalStage) {
        this.accountService = accountService;
        this.userService = userService;
        this.modalStage = modalStage;
        gender_model.setAll(Arrays.asList("Male","Female", "X"));
        genderComboBox.setItems(gender_model);
    }

    @FXML
    public void handleCancel(){
        modalStage.close();
    }

    @FXML
    public void handleDoneEvent(){
        String firstName = null;
        String lastName = null;
        String username = null;
        String email = null;
        String phone = null;
        String password = null;
        LocalDateTime dateOfBirth = null;
        String gender = null;
        String country = null;
        String city = null;
        try {
           firstName = firstNameField.getText();
           lastName = lastNameField.getText();
           username = usernameField.getText();
           email = emailField.getText();
           phone = phoneField.getText();
           if(yourPasswordField.getText().equals(reEnterPasswordField.getText()))
               password = yourPasswordField.getText();
           else
               TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Sign Up Failure", "Failure:\nPasswords conflicting!\n");
           dateOfBirth = birthdayDatePicker.getValue().atStartOfDay();
           gender = genderComboBox.getValue();
           country = countryField.getText();
           city = cityField.getText();
        }
        catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Sign Up Error","Error:\n" + e.getMessage());
            return;
        }
        try {
            userService.add(firstName, lastName, email, phone, dateOfBirth, gender, country, city);
            try{
                accountService.add(userService.findOne(email, phone), username, password, null, LocalDateTime.now(), null, null);
                TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Sign Up Status", "Sign Up: Success");
                handleCancel();
            }
            catch (Exception e){
                userService.remove(userService.findOne(email, phone));
                TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Sign Up Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
            }
        }catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Sign Up Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
        }
    }
}
