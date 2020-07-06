package site.transcendence.controller.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.Folder;
import java.util.List;

/**
 * Service is responsible for real-time checking for new messages in set time interval
 */
public class FolderUpdateService extends Service<Void> {

    private final List<Folder> serverFoldersList;
    private final long timeInterval = 10000; // Time interval (in milliseconds) of checking for new messages

    public FolderUpdateService(List<Folder> serverFoldersList) {
        this.serverFoldersList = serverFoldersList;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Infinite loop
                while(true){
                    try {
                        // Thread waits set amount of time
                        Thread.sleep(timeInterval);

                        // Checking if any FetchFoldersService are running
                        // FetchFoldersService are iterating serverFoldersList in another Thread causing exception so
                        // this service waits until none is currently running
                        //NOTE still it is not best solution, because FetchFoldersService can start running during this service running and can cause exceptions
                        //     there should be thread-safe solution be implemented
                        if (FetchFoldersService.noActiveServices()) {
                            // Iterating through list of folders
                            for (Folder serverFolder : serverFoldersList) {
                                // Checking if folders does not contain folders only and are open
                                if (serverFolder.getType() != Folder.HOLDS_FOLDERS && serverFolder.isOpen()) {
                                    // Getting message count
                                    //NOTE not sure how does it work and why it is needed for FetchFoldersService
                                    //     I assume that method updates message counter on every call but it does not explain why FetchFoldersService cannot do it on it's own
                                    serverFolder.getMessageCount();
                                }
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }
        };
    }



}
