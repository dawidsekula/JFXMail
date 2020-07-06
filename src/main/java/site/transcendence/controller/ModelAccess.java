package site.transcendence.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import site.transcendence.model.EmailAccount;
import site.transcendence.model.EmailFolder;
import site.transcendence.model.EmailMessage;

import javax.mail.Folder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelAccess{

    private EmailMessage selectedMessage;
    private EmailFolder selectedFolder;
    private List<Folder> serverFoldersList;
    private Map<String, EmailAccount> emailAccounts;
    private ObservableList<String> emailAccountsAddresses;

    public EmailMessage getSelectedMessage() {
        return selectedMessage;
    }
    public void setSelectedMessage(EmailMessage selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    public EmailFolder getSelectedFolder() {
        return selectedFolder;
    }
    public void setSelectedFolder(EmailFolder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public List<Folder> getServerFoldersList() {
        if (serverFoldersList == null) serverFoldersList = new ArrayList<>();
        return serverFoldersList;
    }
    public void setServerFoldersList(List<Folder> serverFoldersList) {
        this.serverFoldersList = serverFoldersList;
    }
    public void addServerFolder(Folder serverFolder){
        getServerFoldersList().add(serverFolder);
    }

    private Map<String, EmailAccount> getEmailAccounts() {
        if (emailAccounts == null) emailAccounts = new HashMap<>();
        return emailAccounts;
    }

    public ObservableList<String> getEmailAccountsAddresses() {
        if (emailAccountsAddresses == null) emailAccountsAddresses = FXCollections.observableArrayList();
        return emailAccountsAddresses;
    }

    public EmailAccount getEmailAccount(String emailAddress){
        return getEmailAccounts().get(emailAddress);
    }

    public void addEmailAccount(EmailAccount emailAccount){
        getEmailAccounts().put(emailAccount.getEmailAddress(), emailAccount);
        getEmailAccountsAddresses().add(emailAccount.getEmailAddress());
    }












}
