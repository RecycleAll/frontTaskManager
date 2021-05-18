package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.TaskRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginController extends BorderPane {

    private static final String FXML_FILE = "LoginController.fxml";

    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField loginField;

    private Stage stage;
    private Dev dev;
    private TaskRepository api;

    public LoginController(TaskRepository api, Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.api = api;
        this.stage = stage;
    }

    @FXML
    public void OnConnect(ActionEvent actionEvent) throws ExecutionException, InterruptedException, IOException {
        dev = api.loginDev(loginField.getText(), passwordField.getText());
        System.out.println("longed dev: "+dev);
        if(dev != null){
            stage.setScene( new Scene( new DevViewerController(dev)));
        }
    }
}
