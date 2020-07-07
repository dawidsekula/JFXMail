package site.transcendence;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import site.transcendence.view.ViewFactory;

import javax.naming.OperationNotSupportedException;

/**
 * JFXMail Application
 */
public class JFXMail extends Application {

    @Override
    public void start(Stage stage) throws OperationNotSupportedException {
        Scene scene = ViewFactory.getInstance().getMainScene();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}