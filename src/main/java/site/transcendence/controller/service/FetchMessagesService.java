package site.transcendence.controller.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import site.transcendence.model.EmailFolder;

import javax.mail.Folder;
import javax.mail.Message;

/**
 * Service is responsible for downloading messages from the server to given folder
 *
 * The same messages in different folders are not treated as the same one FIXME
 *
 */
public class FetchMessagesService extends Service<Void> {

    private final EmailFolder localFolder;
    private final Folder serverFolder;

    public FetchMessagesService(EmailFolder localFolder, Folder serverFolder) {
        this.localFolder = localFolder;
        this.serverFolder = serverFolder;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Checking if folder does not contain just folders
                if (serverFolder.getType() != Folder.HOLDS_FOLDERS){
                    // Disallowing state and content of thus folder to be modified
                    serverFolder.open(Folder.READ_ONLY);
                }
                // Checking message amount in folder
                int folderSize = serverFolder.getMessageCount();

                // Iterating through messages
                // NOTE check if it should not be in opposite direction iteration but should not if table's sorting works correct
                for (int i = folderSize; i > 0; i--){
                    // Getting message from the server's folder
                    Message currentMessage = serverFolder.getMessage(i);
                    // And adding it to the local one
                    localFolder.addEmail(currentMessage);
                }
                return null;
            }
        };
    }

}
