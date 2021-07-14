package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.RepositoryManager;
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
    public TextField emailField;

    private App app;
    private Dev dev;
    private RepositoryManager repositoryManager;

    public LoginController(RepositoryManager repositoryManager, App app) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.repositoryManager = repositoryManager;
        this.app = app;
    }

    public void reset(){
        passwordField.setText("");
        emailField.setText("");
        dev = null;
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml loader
    public void OnConnect(ActionEvent actionEvent) throws Exception {
        System.out.println(emailField.getText());
        System.out.println(passwordField.getText());
        int devID = repositoryManager.getRepository().loginDev(emailField.getText(), passwordField.getText());
        dev = repositoryManager.getDev(devID, true);
        System.out.println("longed dev: "+dev);
        if(dev != null){
            app.setDevViewerScene(dev);
        }
    }
}
