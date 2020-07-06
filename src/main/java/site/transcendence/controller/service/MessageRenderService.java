package site.transcendence.controller.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;
import site.transcendence.model.EmailMessage;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;

/**
 * Service responsible for proper message's content rendering
 *
 * TODO refactor whole class
 * TODO get rid of souts used for testing
 */
public class MessageRenderService extends Service<Void> {

    private EmailMessage emailMessage;
    private final WebEngine webEngine;
    private final StringBuffer stringBuffer;

    public MessageRenderService(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.stringBuffer = new StringBuffer();
        this.setOnSucceeded(event -> {
            webEngine.loadContent(stringBuffer.toString());
            emailMessage.setRenderedMessage(stringBuffer.toString()); //
        });
    }

    public void setEmailMessage(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }

    // TODO refactor this mess method
    private void renderMessage() {
        // Resetting stringBuffer
        stringBuffer.setLength(0);
        // Resetting attachments
        emailMessage.clearAttachments();

        // Getting Message
        Message message = emailMessage.getMessageReference();

        try {
            // Getting message's type
            String messageType = message.getContentType();

            if (messageType.contains("TEXT/HTML") ||
                    messageType.contains("TEXT/PLAIN") ||
                    messageType.contains("text")) {
                stringBuffer.append(message.getContent().toString());
            } else if (messageType.contains("multipart")) {

                Multipart multipart = (Multipart) message.getContent();
                System.out.println("Multiparty: " + multipart.getCount());


                for (int i = multipart.getCount() - 1; i >= 0; i--) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    String contentType = bodyPart.getContentType();
                    System.out.println(i +" = BodyPart: " + bodyPart.getFileName());

                    if (contentType.contains("TEXT/HTML") ||
                            contentType.contains("TEXT/PLAIN") ||
                            contentType.toLowerCase().contains("text") ||
                            contentType.toLowerCase().contains("mixed")) {

                        System.out.println("BodyPart is text");
                        // TODO check why == 0
                        if (stringBuffer.length() == 0) {
                            stringBuffer.append(bodyPart.getContent().toString());
                            System.out.println("BodyPart is text - added");
                        }

                    } else if (contentType.toLowerCase().contains("application") ||
                            contentType.toLowerCase().contains("image") ||
                            contentType.toLowerCase().contains("audio")) {
                        System.out.println("BodyPart is mime");
                        MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;
                        emailMessage.addAttachment(mimeBodyPart);
                        System.out.println("Mime added " + mimeBodyPart.getFileName());

                    } else if (contentType.contains("multipart")) {
                        Multipart anotherMultipart = (Multipart) bodyPart.getContent();

                        System.out.println("Another multipart size " + multipart.getCount());
                        for (int j = anotherMultipart.getCount() - 1; j >= 0; j--) {
                            BodyPart anotherBodyPart = anotherMultipart.getBodyPart(j);

                            System.out.println("Another bodypart is " + anotherBodyPart.getFileName());
                            // FIXME adding the same text as in parent probably
                            if (anotherBodyPart.getContentType().contains("TEXT/HTML") ||
                                    anotherBodyPart.getContentType().contains("TEXT/PLAIN")) {
                                //stringBuffer.append(anotherBodyPart.getContent().toString());

                                System.out.println("Another bodypart plain/html: " + anotherBodyPart.getContent().toString());
                                // NOTE test
                                if (stringBuffer.length() == 0) {
                                    stringBuffer.append(anotherBodyPart.getContent().toString());
                                    System.out.println("And another bodypart added");
                                }

                            }
                        }
                    }
                }
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                renderMessage();
                return null;
            }
        };
    }


}
