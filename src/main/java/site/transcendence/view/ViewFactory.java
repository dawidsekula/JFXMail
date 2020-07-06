package site.transcendence.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import site.transcendence.controller.*;

import javax.naming.OperationNotSupportedException;
import java.util.Objects;

public class ViewFactory {

    private static ViewFactory INSTANCE;

    private final String defaultCssPath = "css/style.css";
    private final String mainViewPath = "site/transcendence/controller/MainView.fxml";
    private final String emailDetailsPath = "site/transcendence/controller/EmailDetails.fxml";
    private final String composeMessagePath = "site/transcendence/controller/ComposeMessage.fxml";
    private final String createAccountPath = "site/transcendence/controller/CreateAccount.fxml";

    private final String iconsPath = "/images/icons/";

    private final ModelAccess modelAccess;
    private boolean isMainViewInitialized = false;
    private MainView mainView;
    private EmailDetailsView emailDetailsView;
    private ComposeMessageView composeMessageView;
    private CreateAccountView createAccountView;

    private ViewFactory() {
        this.modelAccess = new ModelAccess();
    }

    public static ViewFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewFactory();
        }
        return INSTANCE;
    }

    public Scene getMainScene() throws OperationNotSupportedException {
        if (!isMainViewInitialized) {
            mainView = new MainView(modelAccess);
            Scene mainScene = initializeScene(mainViewPath, mainView);
            isMainViewInitialized = true;
            return mainScene;
        }else{
            throw new OperationNotSupportedException("Main scene is already initialized");
        }
    }

    public Scene getEmailDetailsScene() {
        emailDetailsView = new EmailDetailsView(modelAccess);
        return initializeScene(emailDetailsPath, emailDetailsView);
    }

    public Scene getComposeMessageScene(){
        composeMessageView = new ComposeMessageView(modelAccess);
        return initializeScene(composeMessagePath, composeMessageView);
    }

    public Scene getCreateAccountScene(){
        createAccountView = new CreateAccountView(modelAccess);
        return initializeScene(createAccountPath, createAccountView);
    }

    public ImageView getIcon(String iconFileName) {
        int iconWidth = 24;
        int iconHeight = 20;

        ImageView iconView;

        try {
            Image iconImage = new Image(getClass().getResourceAsStream(iconsPath + iconFileName), iconWidth, iconHeight, false, false);
            iconView = new ImageView(iconImage);
        } catch (NullPointerException e) {
            System.err.println("Icon not found: " + iconFileName);
            iconView = null;
        }

        return iconView;
    }

    private String getDefaultCss() {
        String stylePath = Objects.requireNonNull(
                getClass().getClassLoader().getResource(defaultCssPath)).toExternalForm();

        if (stylePath == null) {
            System.err.println("Default CSS file not found");
        }

        return stylePath;
    }

    private Scene initializeScene(String fxmlPath, Controller controller) {
        FXMLLoader fxmlLoader;
        Parent parent;
        Scene scene;

        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource(fxmlPath));
            fxmlLoader.setController(controller);
            parent = fxmlLoader.load();

            scene = new Scene(parent);
            scene.getStylesheets().add(getDefaultCss());
        } catch (Exception e) {
            throw new RuntimeException("Scene not loaded: " + e.getLocalizedMessage());
        }

        return scene;
    }


}
