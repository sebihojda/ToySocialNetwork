package com.example.toysocialnetwork.controller;

import com.example.toysocialnetwork.controller.TSNAction.TSN_Action;
import com.example.toysocialnetwork.domain.Message;
import com.example.toysocialnetwork.service.MessageService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

public class MessageEntityController {
    @FXML
    TextField fromField;
    @FXML
    TextField toField;
    @FXML
    TextField messageField;
    @FXML
    TextField sentAtField;
    @FXML
    TextField replyField;

    private MessageService messageService;
    private Stage modalStage;
    private ObservableList<Message> sourceList;

    public void setService(MessageService messageService, Stage modalStage, ObservableList<Message> sourceList){
        this.messageService = messageService;
        this.modalStage = modalStage;
        this.sourceList = sourceList;
    }

    @FXML
    public void handleCancel(){
        modalStage.close();
    }

    @FXML
    public void handleSave(){
        Long newFrom = null;
        String newTo = null;
        List<Long> to_split = null;
        String newMessage = null;
        String newSentAt = null;
        LocalDateTime sent_at = null;
        String newReply = null;
        List<Long> reply_split = null;
        try {
            newFrom = Long.valueOf(fromField.getText());
            newTo = toField.getText();
            to_split = Arrays.stream(newTo.split(",")).map(Long::parseLong).toList();
            newMessage = messageField.getText();
            newSentAt = sentAtField.getText();
            if(newSentAt.equals("now"))
                sent_at = LocalDateTime.now();
            else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                sent_at = LocalDateTime.parse(newSentAt, formatter);
            }
            newReply = replyField.getText();
            reply_split = Arrays.stream(newReply.split(",")).map(Long::parseLong).toList();
        }
        catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.ERROR, "Save Failure","Error:\n" + e.getMessage());
            return;
        }
        try{
            //System.out.println(newFrom + " " + to_split + " " + newMessage + " " + sent_at + " " + reply_split);
            messageService.add(newFrom, to_split, newMessage, sent_at, reply_split);
            TSN_Action.showMessage(null, Alert.AlertType.INFORMATION, "Save Status", "Save: Success");
            handleCancel();
        }catch (Exception e){
            TSN_Action.showMessage(null, Alert.AlertType.WARNING, "Saving Failure", "Failure:\n" + e.getMessage() + "\nPlease try again!");
        }
    }

}
