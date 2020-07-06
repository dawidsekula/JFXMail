package site.transcendence.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import site.transcendence.model.EmailMessage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Needs some improvements in view layer and logic here (i.e. see attachments to download)
 */
public class EmailDetailsView extends Controller implements Initializable {

    @FXML
    public WebView detailsWebView;
    @FXML
    public Label subject;
    @FXML
    public Label from;

    public EmailDetailsView(ModelAccess modelAccess) {
        super(modelAccess);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EmailMessage selectedMessage = getModelAccess().getSelectedMessage();

        subject.setText(selectedMessage.getSubject());
        from.setText(selectedMessage.getFrom());
        detailsWebView.getEngine().loadContent(selectedMessage.getRenderedMessage());

    }

}
