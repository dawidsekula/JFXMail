package site.transcendence.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import site.transcendence.controller.service.AttachmentService;
import site.transcendence.controller.service.CreateEmailAccountService;
import site.transcendence.controller.service.FolderUpdateService;
import site.transcendence.controller.service.MessageRenderService;
import site.transcendence.model.EmailFolder;
import site.transcendence.model.EmailMessage;
import site.transcendence.model.MessageSize;
import site.transcendence.view.ViewFactory;
import site.transcendence.view.table.TableRowFactory;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class MainView extends Controller implements Initializable {

    @FXML
    public WebView emailWebView;

    @FXML
    public TableView<EmailMessage> emailsTableView;
    @FXML
    public TableColumn<EmailMessage, String> colSubject;
    @FXML
    public TableColumn<EmailMessage, String> colFrom;
    @FXML
    public TableColumn<EmailMessage, MessageSize> colSize;
    @FXML
    public TableColumn<EmailMessage, Boolean> colUnread;
    @FXML
    private TableColumn<EmailMessage, Date> colDate;

    @FXML
    public TreeView<String> foldersTreeView;

    @FXML
    private Label downloadAttachmentsLabel;
    @FXML
    private ProgressBar downloadAttachmentsProgress;
    @FXML
    private Button downloadAttachmentsButton;

    private final ViewFactory viewFactory = ViewFactory.getInstance();
    private MessageRenderService messageRenderService;
    private AttachmentService attachmentService;

    public MainView(ModelAccess modelAccess) {
        super(modelAccess);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * Services
         */
        FolderUpdateService folderUpdateService = new FolderUpdateService(getModelAccess().getServerFoldersList());
        folderUpdateService.start();

        this.messageRenderService = new MessageRenderService(emailWebView.getEngine());
        this.attachmentService = new AttachmentService(downloadAttachmentsProgress, downloadAttachmentsLabel);
        this.downloadAttachmentsProgress.progressProperty().bind(attachmentService.progressProperty());

        /**
         * Left-sided view with the view of email accounts and it's folders with emails
         */
        /**
         * Initializing structure of the tree
         */
        EmailFolder emailFolderRoot = new EmailFolder("", null);
        getModelAccess().setEmailFolderRoot(emailFolderRoot);

        foldersTreeView.setRoot(emailFolderRoot);
        foldersTreeView.setShowRoot(false);

        /**
         * Initializing behaviour of the tree
         */
        // Action: Clicking on the folders' view or folder item
        // Result: Displaying its content in emails' table
        foldersTreeView.setOnMouseClicked(event -> {
            // Getting selected item
            EmailFolder selectedFolder = (EmailFolder) foldersTreeView.getSelectionModel().getSelectedItem();
            // If selected folder which is not top element (email account address)
            if (selectedFolder != null && !selectedFolder.isTopElement()) { //TODO: different view for clicking on top element
                // Creating list of emails
                ObservableList<EmailMessage> listOfMessages = selectedFolder.getData();
                // Setting list to emailsTable view
                emailsTableView.setItems(listOfMessages);
                // Setting selected folder to modelAccess
                getModelAccess().setSelectedFolder(selectedFolder);
                // Setting selected message to null
                getModelAccess().setSelectedMessage(null);
            }
        });

        /**
         * Top view with the view of email messages
         */
        /**
         * Initializing structure of table
         */

        // Mapping columns
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colUnread.setCellValueFactory(value -> value.getValue().getUnreadProperty());
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        // Changing cell to checkBox instead of plain text
        colUnread.setCellFactory(CheckBoxTableCell.forTableColumn(colUnread)); //FIXME: changing email status does not use changeEmailStatus() method which is causing problems

        // Setting column as editable
        colUnread.setEditable(true);
        // Setting table as editable
        emailsTableView.setEditable(true);
        //TODO: implement multiple row select for checking as read/unread
        //      remember to proper modification in context menu

        // TODO: changing email status in 'unread' column should fire changeEmailStatus() method
        //       selected folder has to listen on row change

        // Creating context menu for email in table row
        ContextMenu tableRowContextMenu = new ContextMenu();
        // Creating context items
        MenuItem showMessageDetails = new MenuItem("Show details");
        MenuItem setAsUnread = new MenuItem("Set as unread");
        // Adding items to context
        tableRowContextMenu.getItems().addAll(showMessageDetails, setAsUnread);
        // Applying created context menu to the table view
        emailsTableView.setContextMenu(tableRowContextMenu);
        // Setting custom row factory
        emailsTableView.setRowFactory(e -> new TableRowFactory());

        /**
         * Initializing behaviour inside table with email messages
         */
        // Action: Clicking inside the table with left or right click
        // Result: LEFT - Loading message (if selected) to messageWebView on the bottom and setting it's status as 'read'
        //         RIGHT - Displaying context menu which is disabled if no message is selected
        emailsTableView.setOnMouseClicked(event -> {
            // Getting selected message
            EmailMessage emailMessage = emailsTableView.getSelectionModel().getSelectedItem();
            // If selected message is not null
            if (emailMessage != null) {
                // Enabling items in context menu focused on message
                showMessageDetails.setDisable(false);
                setAsUnread.setDisable(false);

                // Passing selected message to modelAccess
                getModelAccess().setSelectedMessage(emailMessage);
                // Loading it's content to emailWebView
                messageRenderService.setEmailMessage(emailMessage);
                messageRenderService.restart();

                // If selected message is 'unread'
                if (emailMessage.isUnread()) {
                    // Clicking on in will cause changing it's status
                    changeEmailStatus();
                }
            }
            // If none of message is selected
            else {
                // Disabling items in context menu focused on message
                showMessageDetails.setDisable(true);
                setAsUnread.setDisable(true);
            }

        });

        // Action: Clicking 'Show details' in the context menu
        // Result: Displaying new window with details of selected message
        showMessageDetails.setOnAction(event -> {
            // NOTE: context is disabled if there is no selected message so no need to check if selected message is not null
            // Getting scene
            Scene detailsScene = viewFactory.getEmailDetailsScene();
            // Creating new stage
            Stage stage = new Stage();
            // Setting scene to stage
            stage.setScene(detailsScene);
            // Displaying stage
            stage.show();
        });

        // Action: Clicking 'Set as unread' in the context menu
        // Result: Sets email as unread
        setAsUnread.setOnAction(event -> {
            // Getting selected message
            EmailMessage emailMessage = getModelAccess().getSelectedMessage();
            // If selected message is not unread (is read)
            if (!emailMessage.isUnread()) {
                // Clicking 'Set as unread' will cause changing it's status
                changeEmailStatus();
            }
        });

    }

    // Action: Clicking 'Read/Unread message' button
    // Result: Changing email status 'read <-> unread'
    @FXML
    public void changeEmailStatus() {
        // Getting selected folder
        EmailFolder selectedFolder = getModelAccess().getSelectedFolder();
        //Getting selected message
        EmailMessage emailMessage = getModelAccess().getSelectedMessage();

        // If message and selected folders are not null
        if (emailMessage != null && selectedFolder != null) {
            // Checking if message is unread
            boolean isUnread = emailMessage.isUnread();
            // If current status is 'unread'
            if (isUnread) {
                // Then action will cause changing it's status to read (unread = false)
                emailMessage.getUnreadProperty().set(false);
                // Selected folder decrements it's unread messages count
                selectedFolder.decrementUnreadMessages(1);
            } else {
                // Else action will cause changing it's status to unread (unread = true)
                emailMessage.getUnreadProperty().set(true);
                // And folder increments it's unread messages count
                selectedFolder.incrementUnreadMessages(1);
            }
        }
    }

    // Action: Clicking 'Get messages' button
    // Result: Downloading new messages from server
    // NOTE this is disabled since auto download is ON, it should be active anyway TODO
    @FXML
    void getMessages(ActionEvent event) {
//        Service<Void> emailService = new Service<Void>() {
//            @Override
//            protected Task<Void> createTask() {
//                return new Task<Void>() {
//                    @Override
//                    protected Void call() throws Exception {
//
//                        EmailFolder selectedFolder = getModelAccess().getSelectedFolder();
//                        if (selectedFolder != null) {
//                            ObservableList<EmailMessage> emailMessages = selectedFolder.getData();
//                            AppProperties.GET_EMAIL_ACCOUNT().addEmailsToData(emailMessages);
//                        }
//
//                        return null;
//                    }
//                };
//            }
//        };
//        emailService.start();

    }


    @FXML
    void downloadAttachments(ActionEvent event) {
        EmailMessage emailMessage = emailsTableView.getSelectionModel().getSelectedItem();
        if (emailMessage != null && emailMessage.hasAttachments()){
            attachmentService.setEmailMessage(emailMessage);
            attachmentService.restart();
        }
    }

    // FIXME clicking on the button causes exception if no accounts are created NOTE temporary added if statement
    // Exception in thread "JavaFX Application Thread" java.lang.RuntimeException: java.lang.reflect.InvocationTargetException
    // Caused by: java.lang.reflect.InvocationTargetException
    @FXML
    void createEmailAction(ActionEvent event) {
        if (getModelAccess().getEmailAccountsAddresses().size() > 0) {
            Stage stage = new Stage();
            Scene scene = ViewFactory.getInstance().getComposeMessageScene();

            stage.setScene(scene);
            stage.show();
        }
    }


    @FXML
    void addNewAccount(ActionEvent event) {
        Stage stage = new Stage();
        Scene scene = ViewFactory.getInstance().getCreateAccountScene();

        stage.setScene(scene);

        stage.setHeight(380);
        stage.setWidth(740);
        stage.setResizable(false);
        stage.setTitle("Create account");
        //stage.initStyle(StageStyle.UNDECORATED); //TODO after implementing 'Cancel' button
        //stage.initOwner();
        stage.initModality(Modality.APPLICATION_MODAL);



        stage.show();
    }

}
