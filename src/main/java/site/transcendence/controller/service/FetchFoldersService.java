package site.transcendence.controller.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import site.transcendence.controller.ModelAccess;
import site.transcendence.model.EmailAccount;
import site.transcendence.model.EmailFolder;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

/**
 * Service is responsible for downloading folders of given EmailAccount from the server
 */
public class FetchFoldersService extends Service<Void> {

    private final EmailFolder emailFolderRoot;
    private final EmailAccount emailAccount;

    private final ModelAccess modelAccess;
    private static int ACTIVE_SERVICES_COUNT = 0;

    public FetchFoldersService(EmailFolder emailFolderRoot, EmailAccount emailAccount, ModelAccess modelAccess) {
        this.emailFolderRoot = emailFolderRoot;
        this.emailAccount = emailAccount;
        this.modelAccess = modelAccess;

        // NOTE not sure about this, what if cancelled or something else
        this.setOnSucceeded(event -> ACTIVE_SERVICES_COUNT--);
    }

    public static boolean noActiveServices(){
        return ACTIVE_SERVICES_COUNT == 0;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                //
                ACTIVE_SERVICES_COUNT++;

                // Null check for EmailAccount
                if (emailAccount != null) {
                    // Getting folders array from the server
                    Folder[] foldersAtTheServer = emailAccount
                            .getStore()
                            .getDefaultFolder()
                            .list();

                    // TODO create this stuff to separate method to not duplicate code dealing with subfolders
                    // Iterating folders we got from the server
                    for (Folder serverFolder : foldersAtTheServer) {
                        // Adding folder to modelAccess
                        modelAccess.addServerFolder(serverFolder);

                        // Creating EmailFolder instance based on server's one
                        EmailFolder localFolder = new EmailFolder(
                                serverFolder.getName(),
                                "folder.png", // FIXME get rid of that
                                serverFolder.getFullName());
                        // Setting expanded as default
                        localFolder.setExpanded(true);
                        // Adding folder to the account's root
                        emailFolderRoot.getChildren().add(localFolder);

                        // Adding listener of new messages
                        addMessageListenerToFolder(serverFolder, localFolder); // TODO consistency in methods (LOCAL, SERVER) or (SERVER, LOCAL)

                        // Downloading messages for given folder
                        FetchMessagesService fetchFolderMessages = new FetchMessagesService(localFolder, serverFolder); // TODO consistency in methods (LOCAL, SERVER) or (SERVER, LOCAL)
                        // Starting service on separate thread
                        fetchFolderMessages.start();

                        // Iterating folder's subfolders
                        for (Folder serverSubfolder : serverFolder.list()) {
                            // Adding folder to modelAccess
                            modelAccess.addServerFolder(serverSubfolder);

                            // Creating EmailFolder instance based on server's one
                            EmailFolder localSubfolder = new EmailFolder(
                                    serverSubfolder.getName(),
                                    "folder.png", // FIXME get rid of that
                                    serverSubfolder.getFullName());
                            // Setting expanded as default
                            localSubfolder.setExpanded(true);
                            // Adding subfolder as a folder's child
                            localFolder.getChildren().add(localSubfolder);

                            // Adding listener of new messages
                            addMessageListenerToFolder(serverSubfolder, localSubfolder); // TODO consistency in methods (LOCAL, SERVER) or (SERVER, LOCAL)

                            // Downloading messages for given subfolder
                            FetchMessagesService fetchSubfolderMessages = new FetchMessagesService(localSubfolder, serverSubfolder); // TODO consistency in methods (LOCAL, SERVER) or (SERVER, LOCAL)
                            // Starting service on separate thread
                            fetchSubfolderMessages.start();
                        }
                    }
                }
                return null;
            }
        };
    }

    private void addMessageListenerToFolder(Folder serverFolder, EmailFolder localFolder){ // TODO consistency in methods (LOCAL, SERVER) or (SERVER, LOCAL)
        // Adding message count listener to the server's folder
        serverFolder.addMessageCountListener(new MessageCountAdapter() {
            @Override
            // Method describes behaviour on MessageCountEvent
            // TODO check more on that in documentation
            public void messagesAdded(MessageCountEvent e) {
                // New messages array
                Message[] newMessages = e.getMessages();
                // Iterating new messages
                for (int i = 0; i < newMessages.length; i++){
                    try {
                        // Array of new messages contain last messages in serverFolder.getMessages() array
                        // and serverFolder.getMessageCount() returns total number of messages in folder
                        // so currentMessage is 'i' last message in serverFolder's messages list
                        Message currentMessage = serverFolder.getMessage(serverFolder.getMessageCount() - i);
                        // Adding new email to local folder
                        localFolder.addEmail(currentMessage);

                        // NOTE 04.024 0:07:20 adding position of message position but it should be indicated by sorting, not 'always on top'

                    } catch (MessagingException messagingException) {
                        messagingException.printStackTrace();
                    }

                }

            }
        });

    }










}
