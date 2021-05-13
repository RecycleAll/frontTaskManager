package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

import java.io.IOException;

public class DevController {

    private static final String FXML_FILE = "DevController.fxml";

    public static DevController loadNew(Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        DialogPane pane = fxmlLoader.load();
        DevController devController = fxmlLoader.getController();
        devController.setDev(dev);
        devController.dialogPane = pane;
        return devController;
    }
    public static DevController loadNew() throws IOException {
        return loadNew(null);
    }

    private DialogPane dialogPane;

    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField githubField;

    private Dev dev;

    public void setDev(Dev newDev){
        if( newDev == null){
            this.dev = new Dev();
        }else{
            this.dev = new Dev(newDev);
        }

        firstNameField.setText(this.dev.getFirstName());
        firstNameField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            dev.setFirstName(newValue);
        });

        lastNameField.setText(this.dev.getLastName());
        lastNameField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            dev.setLastName(newValue);
        });

        emailField.setText(this.dev.getEmail());
        emailField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            dev.setEmail(newValue);
        });

        githubField.setText( String.valueOf(this.dev.getGithub_id()));
        githubField.textProperty().addListener((observableValue, oldValue, newValue) -> {

            if( !newValue.isEmpty()) {
                try {
                    dev.setGithub_id(Integer.parseInt(newValue));
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "\""+newValue + "\" is not a valid number", ButtonType.OK);
                    alert.showAndWait();
                    githubField.setText(oldValue);
                }
            }
        });

    }

    public void OnPasswordChange(ActionEvent actionEvent) {
    }

    public DialogPane getDialogPane() {
        return dialogPane;
    }

    public Dev getDev() {
        return dev;
    }


}
