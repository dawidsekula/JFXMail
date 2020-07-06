package site.transcendence.controller.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import site.transcendence.model.ConnectionState;
import site.transcendence.model.EmailAccount;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;

public class MessageSendService extends Service<Integer> {

    private EmailAccount emailAccount;
    private String subject;
    private String recipient;
    private String content;
    private List<File> attachments;

    private int result;

    public MessageSendService(EmailAccount emailAccount, String subject, String recipient, String content, List<File> attachments) {
        this.emailAccount = emailAccount;
        this.subject = subject;
        this.recipient = recipient;
        this.content = content;
        this.attachments = attachments;
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                System.out.println("MSS called");
                try {
                    Session session = emailAccount.getSession();
                    MimeMessage message = new MimeMessage(session);

                    message.setFrom(emailAccount.getEmailAddress());
                    message.setSubject(subject);
                    message.setRecipients(Message.RecipientType.TO, recipient);

                    Multipart multipart = new MimeMultipart();
                    BodyPart bodyPart = new MimeBodyPart();
                    bodyPart.setContent(content, "text/html");
                    multipart.addBodyPart(bodyPart);

                    if (attachments.size() > 0) {
                        for (File file : attachments) {
                            MimeBodyPart attachment = new MimeBodyPart();
                            DataSource dataSource = new FileDataSource(file.getAbsoluteFile());

                            attachment.setDataHandler(new DataHandler(dataSource));
                            attachment.setFileName(file.getName());

                            multipart.addBodyPart(attachment);
                        }
                    }

                    message.setContent(multipart);

                    Transport transport = session.getTransport();
                    transport.connect(emailAccount.getProperties().getProperty("outgoingHost"),
                            emailAccount.getEmailAddress(),
                            emailAccount.getEmailPassword());

                    System.out.println("Trying to send message...");
                    transport.sendMessage(message, message.getAllRecipients());
                    System.out.println("SENT!");

                    transport.close();

                    result = ConnectionState.MESSAGE_SENT_OK.getValue(); // TODO refactor that
                } catch (Exception e) {
                    result = ConnectionState.MESSAGE_SENT_FAILURE.getValue();
                    System.err.println(e.getLocalizedMessage());
                }

                System.out.println("MSS ending work");
                return result; // NOTE not sure what for, for nothing I guess
            }
        };
    }
}
