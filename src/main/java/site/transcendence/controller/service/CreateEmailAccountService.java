package site.transcendence.controller.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import site.transcendence.controller.ModelAccess;
import site.transcendence.model.ConnectionState;
import site.transcendence.model.EmailAccount;
import site.transcendence.model.EmailFolder;

/**
 * Class service is responsible for creating a local email account instance with given credentials and
 * instance of treeView's root (EmailFolder.class)
 *
 *
 */
public class CreateEmailAccountService extends Service<Integer>{

    private final String emailAddress;
    private final String emailPassword;
    private final EmailFolder emailFolderRoot;

    private ModelAccess modelAccess;

    public CreateEmailAccountService(String emailAddress, String emailPassword, EmailFolder emailFolderRoot, ModelAccess modelAccess) {
        this.emailAddress = emailAddress;
        this.emailPassword = emailPassword;
        this.emailFolderRoot = emailFolderRoot;
        this.modelAccess = modelAccess;
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<>() {
            @Override
            protected Integer call() throws Exception {
                // Creating new email account with passed credentials
                EmailAccount emailAccount = new EmailAccount(emailAddress, emailPassword);

                // Checking status of created EmailAccount
                if (emailAccount.getConnectionState() == ConnectionState.LOGIN_STATE_SUCCEED){
                    // NOTE
                    modelAccess.addEmailAccount(emailAccount);

                    // If account is successfully created and connected, EmailFolder instance with the name of
                    // account's email address is created
                    EmailFolder emailFolder = new EmailFolder(emailAddress, null);
                    // Adding created folder to main root
                    emailFolderRoot.getChildren().add(emailFolder);

                    // Getting folders from the server
                    FetchFoldersService fetchFoldersService = new FetchFoldersService(emailFolder, emailAccount, modelAccess);
                    // Starting service
                    fetchFoldersService.start();
                }
                // Returning account's connection state
                // NOTE not real use of it for now
                return emailAccount.getConnectionState().getValue();
            }
        };
    }






}
