package site.transcendence.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import site.transcendence.view.ViewFactory;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

public class EmailFolder extends TreeItem<String> {

    private boolean topElement;
    private int unreadMessages;
    private String folderName;
    private String folderFullPath;
    private ObservableList<EmailMessage> data = FXCollections.observableArrayList();

    // TODO: refactor constructors with better icon implementation and isRoot/topElement
    public EmailFolder(String folderName, String iconFileName) {
        super(folderName, ViewFactory.getInstance().getIcon(iconFileName));
        this.folderName = folderName;
        this.folderFullPath = folderName;
        this.data = null;
        this.topElement = true;
        this.setExpanded(true);
    }

    // TODO: should be just boolean pointing if this is 'main' folder
    public EmailFolder(String folderName, String iconFileName, String folderFullPath) {
        super(folderName, ViewFactory.getInstance().getIcon(iconFileName));
        this.folderName = folderName;
        this.folderFullPath = folderFullPath;
    }

    private void updateValue() {
        if (unreadMessages > 0) {
            setValue(folderName + getParsedUnreadMessages());
        } else {
            setValue(folderName);
        }
    }

    private String getParsedUnreadMessages() {
        return "(" + this.unreadMessages + ")";
    }

    public void incrementUnreadMessages(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Incrementation amount has to be positive");
        } else {
            unreadMessages += amount;
            updateValue();
        }
    }

    public void decrementUnreadMessages(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Decrement amount has to be positive");
        } else if (amount > unreadMessages) {
            throw new IllegalArgumentException("Passed amount is greater than unread messages amount");
        } else {
            unreadMessages -= amount;
            updateValue();
        }
    }

    public void addEmail(Message message) throws MessagingException {
        // Message is unread when does not contain Flag.SEEN
        boolean isUnread = !(message.getFlags().contains(Flags.Flag.SEEN));

        EmailMessage emailMessage = new EmailMessage(
                message.getSubject(),
                // TODO: change model class of emailMessage to array to
                message.getFrom()[0].toString(),
                message.getSize(),
                isUnread,
                message,
                message.getReceivedDate());

        data.add(emailMessage);

        if (isUnread) {
            incrementUnreadMessages(1);
        }


    }

    public boolean isTopElement() {
        return this.topElement;
    }

    public ObservableList<EmailMessage> getData() {
        return data;
    }


}
