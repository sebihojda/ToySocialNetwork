package com.example.toysocialnetwork.controller.TSNAction;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class TSN_Action {
    public static void showMessage(Stage stage, Alert.AlertType alertType, String title, String message) {
        Alert tsn_Alert = new Alert(alertType);
        tsn_Alert.initOwner(stage);
        tsn_Alert.setContentText(message);
        tsn_Alert.setTitle(title);
        tsn_Alert.showAndWait();
    }
}
