package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

public class PasswordController extends DialogPane {

    private static final String FXML_FILE = "PasswordController.fxml";

    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField newPasswordField;
    @FXML
    public PasswordField confirmPasswordField;

    private Dev dev;

    public PasswordController(Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.dev = dev;
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml loader
    public void initialize() {
        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if (dev == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Internal error dev is null", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            } else if (!dev.getPassword().equals(passwordField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong password", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            } else if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "new password mismatch", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            } else {
                dev.setPassword(newPasswordField.getText());
            }
        });

    }

    public void setDev(Dev dev) {
        this.dev = dev;
    }
}
