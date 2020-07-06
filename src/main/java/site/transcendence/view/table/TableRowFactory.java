package site.transcendence.view.table;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableRow;
import site.transcendence.model.EmailMessage;

public class TableRowFactory extends TableRow<EmailMessage> {

    private final SimpleBooleanProperty unread = new SimpleBooleanProperty();
    private EmailMessage selectedRow = null;

    public TableRowFactory(){
        super();
        // Clicking on the row calls listener
        itemProperty().addListener(
                // Observing selected row and listening for it's updates
                (ObservableValue<? extends EmailMessage> observableValue, EmailMessage oldValue, EmailMessage newValue) -> {
                    // Unbinding old value
                    unread.unbind();
                    // Updated EmailMessage cannot be null to operate on it
                    if (newValue != null){
                        // Binding value from updated EmailMessage
                        // 'unread' calls it's listener, then listener calls updateItem() and applies bold effect to the row
                        unread.bind(newValue.getUnreadProperty());
                        // Setting row with applied bold style
                        selectedRow = newValue;
                    }
                });

        // Adding listener to 'unread' property
        unread.addListener(
                (ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) -> {
                    // Called listener calls updateItem() method which attaches style to the row
                    if (selectedRow != null && selectedRow==getItem()){
                        updateItem(getItem(), isEmpty());
                    }
                });
    }

    @Override
    protected void updateItem(EmailMessage item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            // If email is unread row is bolded
            if (item.isUnread()) {
                setStyle("-fx-font-weight: bold");
            }
            // Else no special style is set
            else {
                setStyle("");
            }
        }
    }
}
