package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.TaskRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginController extends BorderPane {

    private static final String FXML_FILE = "LoginController.fxml";

    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField loginField;

    private Dev dev;
    private TaskRepository api;

    public LoginController(TaskRepository api) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.api = api;
    }

    @FXML
    public void OnConnect(ActionEvent actionEvent) throws ExecutionException, InterruptedException {
        dev = api.loginDev(loginField.getText(), passwordField.getText());
    }
}
