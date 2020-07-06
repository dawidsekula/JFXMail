package site.transcendence.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmailMessage {

    private final SimpleStringProperty subject;
    private final SimpleStringProperty from;
    private final SimpleObjectProperty<MessageSize> size;
    private final SimpleBooleanProperty unread;
    private final SimpleObjectProperty<Date> date;

    private final Message messageReference;
    private String renderedMessage;
    private List<MimeBodyPart> attachmentsList;
    private StringBuffer attachmentNames;

    // TODO class should be more like Message class
    public EmailMessage(String subject, String from, long size, boolean isUnread, Message messageReference, Date date){
        this.subject = new SimpleStringProperty(subject);
        this.from = new SimpleStringProperty(from);
        this.size = new SimpleObjectProperty<>(new MessageSize(size));
        this.unread = new SimpleBooleanProperty(isUnread);
        this.messageReference = messageReference;
        this.date = new SimpleObjectProperty<>(date);

        this.renderedMessage = "";
        this.attachmentsList = new ArrayList<>();
        this.attachmentNames = new StringBuffer();
    }

    public String getSubject() {
        return subject.get();
    }

    public String getFrom() {
        return from.get();
    }

    public MessageSize getSize() {
        return size.get();
    }

    public SimpleBooleanProperty getUnreadProperty() {
        return unread;
    }

    public Date getDate() {
        return date.get();
    }

    public boolean isUnread() {
        return unread.get();
    }

    public Message getMessageReference() {
        return messageReference;
    }

    public List<MimeBodyPart> getAttachmentsList() {
        return attachmentsList;
    }

    public StringBuffer getAttachmentNames() {
        return attachmentNames;
    }

    public void addAttachment(MimeBodyPart mimeBodyPart){
        try {
            attachmentsList.add(mimeBodyPart);
            attachmentNames.append(mimeBodyPart.getFileName() + "; ");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean hasAttachments(){
        return attachmentsList.size() > 0;
    }

    public void clearAttachments(){
        attachmentsList.clear();
        attachmentNames.setLength(0);
    }

    public String getRenderedMessage() {
        return renderedMessage;
    }

    public void setRenderedMessage(String renderedMessage) {
        this.renderedMessage = renderedMessage;
    }


}
