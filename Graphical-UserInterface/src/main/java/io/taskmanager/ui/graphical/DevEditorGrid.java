package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class DevEditorGrid extends GridPane {

    private static final String FXML_FILE = "DevEditorGrid.fxml";

    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField githubField;

    private Dev dev;

    public DevEditorGrid(Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setDev(dev);
    }

    public void setDev(Dev newDev){
        if( newDev == null){
            this.dev = new Dev();
        }else{
            this.dev = newDev;
        }

        firstNameField.setText(this.dev.getFirstname());
        lastNameField.setText(this.dev.getLastname());
        emailField.setText(this.dev.getEmail());
        githubField.setText( String.valueOf(this.dev.getGithub_id()));
    }

    public void OnPasswordChange(ActionEvent actionEvent) throws IOException {
        PasswordChangeDialog dialog = new PasswordChangeDialog(dev);
        dialog.showAndWait();
    }

    public String getFirstNameString() {
        return firstNameField.getText();
    }

    public String getLastNameString() {
        return lastNameField.getText();
    }

    public String getEmailString() {
        return emailField.getText();
    }

    public String getGithubString() {
        return githubField.getText();
    }

    public Dev getDev() {
        return dev;
    }
}
