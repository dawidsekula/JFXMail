package site.transcendence.model;

import javax.mail.*;
import java.util.Properties;

import static site.transcendence.model.ConnectionState.*;

/**
 * Class represents an email account with given credentials
 *
 * Properties are temporarily hardcoded in constructor and need further TODO
 * Also constructor needs some refactor TODO
 */
public class EmailAccount {

    private String emailAddress;
    private String emailPassword;
    private Properties properties;

    private Store store;
    private Session session;
    private ConnectionState connectionState;

    public EmailAccount(String emailAddress, String emailPassword){
        this.emailAddress = emailAddress;
        this.emailPassword = emailPassword;

        // Setting account properties
        // NOTE this is temporary location
        // TODO move it to independent method
        this.properties = new Properties();
        this.properties.put("mail.store.protocol", "imaps");
        this.properties.put("mail.transport.protocol", "smtps");
        this.properties.put("mail.smtps.host", "smtp.gmail.com");
        this.properties.put("mail.smtps.auth", "true");
        this.properties.put("incomingHost", "imap.gmail.com");
        this.properties.put("outgoingHost", "smtp.gmail.com");

        // Creating authentication with passed credentials
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddress, emailPassword);
            }
        };

        // Establishing session with created properties and authentication
        session = Session.getInstance(properties, authenticator);

        try {
            // Getting store
            // Store is class that models a message store and its access protocol for storing and retrieving messages
            this.store = session.getStore();
            // Actual connection
            this.store.connect(
                    this.properties.getProperty("incomingHost"), emailAddress, emailPassword);
            // NOTE this is just for logging and is temporary
            System.out.println("Email account constructed successfully");
            // Setting connection state to success
            connectionState = LOGIN_STATE_SUCCEED;
        }catch (Exception e){
            // NOTE this is just for logging and is temporary
            System.err.println("Email account construction failed");
            // If some exception happen then connectionState is failed by credentials
            // TODO not very elegant and needs refactor, also not clear what to do next and how to fix it if failed
            connectionState = LOGIN_STATE_FAILED_BY_CREDENTIALS;
        }
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }
}
