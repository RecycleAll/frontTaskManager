package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class PasswordController {

    private static final String FXML_FILE = "PasswordController.fxml";
    public FlowPane devsFlowPane;

    public static PasswordController loadNew(Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.load();
        PasswordController passwordController = fxmlLoader.getController();
        passwordController.setDev(dev);
        return passwordController;
    }

    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField newPasswordField;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public DialogPane dialogPane;

    private Dev dev;

    @FXML
    public void initialize(){
        Button applyButton = (Button) dialogPane.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if( dev == null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Internal error dev is null", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( !dev.getPassword().equals(passwordField.getText()))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong password", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( !newPasswordField.getText().equals( confirmPasswordField.getText()))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "new password mismatch", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else
            {
                dev.setPassword(newPasswordField.getText());
            }
        });

    }

    public void setDev(Dev dev) {
        this.dev = dev;
    }
}
