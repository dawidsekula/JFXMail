package site.transcendence.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import site.transcendence.controller.service.CreateEmailAccountService;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateAccountView extends Controller implements Initializable {

    @FXML
    private PasswordField emailPasswordField;
    @FXML
    private TextField emailAddressField;

    @FXML
    private TextField smtpField;
    @FXML
    private TextField imapField;


    @FXML
    private ToggleButton downloadMessagesEnabled;
    @FXML
    private TextField lastDaysLabel;

    @FXML
    private ChoiceBox<String> defaultServersPicker;

    public CreateAccountView(ModelAccess modelAccess){
        super(modelAccess);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // NOTE temporary default data
        // TODO delete this after full implementation
        smtpField.setText("smtp.gmail.com");
        imapField.setText("imap.gmail.com");

        emailAddressField.setText(System.getenv("sampleEmailAddress"));
        emailPasswordField.setText(System.getenv("sampleEmailPassword"));

    }

    @FXML
    void createAccountAction() {
        // TODO check if already exists

        CreateEmailAccountService accountService = new CreateEmailAccountService(
                emailAddressField.getText(),
                emailPasswordField.getText(),
                getModelAccess().getEmailFolderRoot(),
                getModelAccess()
        );
        accountService.start();

        // Multithreading
        // wait until all services end
        // if error - show label error, if succeed close window


    }

    @FXML
    void testConnectionAction(ActionEvent event) {

    }




}
