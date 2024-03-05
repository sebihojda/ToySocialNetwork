package com.example.toysocialnetwork.controller;

import com.example.toysocialnetwork.controller.TSNAction.TSN_Action;
import com.example.toysocialnetwork.domain.User;
import com.example.toysocialnetwork.service.UserService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.StreamSupport;

public class UserEntityController {

    @FXML
    TextField firstNameField;
    @FXML
    TextField lastNameField;
    @FXML
    TextField emailField;
    @FXML
    TextField phoneField;
    @FXML
    TextField birthdayField;
    @FXML
    TextField genderField;
    @FXML
    TextField countryField;
    @FXML
    TextField cityField;

    private UserService userService;
    private User user;
    private Stage modalStage;

    public void setUserService(UserService userService, User user, Stage modalStage){
        this.userService = userService;
        this.user = user;
        this.modalStage = modalStage;
        if(user != null){
            initializeTextFields(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getDateOfBirth(), user.getGender(), user.getCountry(), user.getCity());
        }
    }

    private void initializeTextFields(String firstName, String lastName, String email, String phoneNumber, LocalDateTime dateOfBirth, String gender, String country, String city) {
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        emailField.setText(email);
        phoneField.setText(phoneNumber);
        birthdayField.setText(dateOfBirth.toLocalDate().toString());
        genderField.setText(gender);
        countryField.setText(country);
        cityField.setText(city);
    }

    @FXML
    public void handleCancel(){
        modalStage.close();
    }

    @FXML
    public void handleSave(){
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
            email = emailField.getText();
            phone = phoneField.getText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dOB =  LocalDate.parse(birthdayField.getText(), formatter);
            dateOfBirth = dOB.atStartOfDay();
            gender = genderField.getText();
            country = countryField.getText();
            city = cityField.getText();
        }
        catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Save Error","Error:\n" + e.getMessage());
            return;
        }

        if(this.user == null){
            try{
                userService.add(firstName, lastName, email, phone, dateOfBirth, gender, country, city);
                TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Save Status", "Save: Success");
                handleCancel();
            }catch (Exception e){
                TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Save Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
            }
        }else {
            try {
                userService.update(user.getId(), firstName, lastName, email, phone, dateOfBirth, gender, country, city);
                TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Update Status", "Update: Success");
                handleCancel();
            }catch (Exception e){
                TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Update Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
            }
        }
    }
}
