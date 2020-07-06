package site.transcendence.controller.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import site.transcendence.model.EmailMessage;

import javax.mail.internet.MimeBodyPart;

public class AttachmentService extends Service<Void> {

    private final String DOWNLOAD_DIRECTORY = "D:/Downloads/";
    private ProgressBar progressBar;
    private Label label;

    private EmailMessage emailMessage;

    public AttachmentService(ProgressBar progressBar, Label label) {
        this.progressBar = progressBar;
        this.label = label;
        visibleProgressStatus(false);

        this.setOnRunning(event -> visibleProgressStatus(true));
        this.setOnSucceeded(event -> visibleProgressStatus(false));
    }

    public void setEmailMessage(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }

    public void visibleProgressStatus(boolean value){
        progressBar.setVisible(value);
        label.setVisible(value);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Attachment service called");
                for (MimeBodyPart attachment : emailMessage.getAttachmentsList()){

                    updateProgress(emailMessage.getAttachmentsList().indexOf(attachment), emailMessage.getAttachmentsList().size());


                    System.out.println("Attachments: " + emailMessage.getAttachmentNames().toString() + " || And now working on " + attachment.getFileName());
                    System.out.println("Saving file " + attachment.getFileName() + " at " + DOWNLOAD_DIRECTORY + "...");
                    attachment.saveFile(DOWNLOAD_DIRECTORY + attachment.getFileName());
                    System.out.println("File " + attachment.getFileName() + " saved");
                }

                return null;
            }
        };
    }

}
