package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import io.taskmanager.core.repository.RepositoryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends BorderPane {

    private static final String FXML_FILE = "LoginController.fxml";

    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField emailField;

    private final App app;
    private Dev dev;
    private final RepositoryManager repositoryManager;

    public LoginController(RepositoryManager repositoryManager, App app) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.repositoryManager = repositoryManager;
        this.app = app;
    }

    public void reset() {
        passwordField.setText("");
        emailField.setText("");
        dev = null;
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml loader
    public void OnConnect(ActionEvent actionEvent) throws Exception {
        //System.err.println(emailField.getText());
        //System.err.println(passwordField.getText());
        int devID = repositoryManager.getRepository().loginDev(emailField.getText(), passwordField.getText());
        if (devID >= 1) {
            dev = repositoryManager.getDev(devID, true);
            //System.err.println("longed dev: " + dev);
            if (dev != null) {
                app.setDevViewerScene(dev);
            }
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml loader
    public void onRegister(ActionEvent actionEvent) throws Exception {
        DevEditorDialog dialog = new DevEditorDialog(null, false, true);
        Optional<Dev> res = dialog.showAndWait();
        if (res.isPresent()) {
            if (repositoryManager.getRepository().registerDev(res.get())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You have been correctly registered! Please login", ButtonType.OK);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to register! try again", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }
}
