package site.transcendence.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import site.transcendence.controller.service.MessageSendService;
import site.transcendence.model.ConnectionState;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ComposeMessageView extends Controller implements Initializable {

    @FXML
    private ChoiceBox<String> senderChoice;
    @FXML
    private TextField recipientField;
    @FXML
    private TextField subjectField;

    @FXML
    private Label attachmentsLabel;
    private List<File> attachmentsList;

    @FXML
    private HTMLEditor messageEditor;

    @FXML
    private Label statusLabel;

    public ComposeMessageView(ModelAccess modelAccess) {
        super(modelAccess);
        attachmentsList = new ArrayList<>();
    }

    @FXML
    void attachButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null){
            attachmentsLabel.setText(""); // NOTE temporary FIXME
            attachmentsList.add(selectedFile);
            attachmentsLabel.setText(attachmentsLabel.getText() + selectedFile.getName() + "; ");
        }
    }

    @FXML
    void sendMessageAction() {

        MessageSendService messageSendService = new MessageSendService(
            getModelAccess().getEmailAccount(senderChoice.getValue()),
                subjectField.getText(),
                recipientField.getText(),
                messageEditor.getHtmlText(),
                attachmentsList
        );

        messageSendService.restart();

        messageSendService.setOnSucceeded(event -> {
            if (messageSendService.getValue() == ConnectionState.MESSAGE_SENT_OK.getValue()){
                statusLabel.setText("Email sent successfully!");
            }else{
                statusLabel.setText("There was an error while sending this message...");
            }
        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        senderChoice.setItems(getModelAccess().getEmailAccountsAddresses());
        senderChoice.setValue(getModelAccess().getEmailAccountsAddresses().get(0));
    }


}
