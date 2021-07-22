package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import io.taskmanager.ui.graphical.conflict.IObjectEditor;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Objects;

public class DevEditorGrid extends GridPane implements IObjectEditor<Dev> {

    private static final String FXML_FILE = "DevEditorGrid.fxml";

    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField githubField;
    @FXML
    public Button changePasswordButton;

    public TextField passwordField;

    private Dev dev;

    public DevEditorGrid(Dev dev, boolean editable, boolean register) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();

        SimpleBooleanProperty isEditable = new SimpleBooleanProperty(editable);
        firstNameField.editableProperty().bind(isEditable);
        lastNameField.editableProperty().bind(isEditable);
        emailField.editableProperty().bind(isEditable);
        githubField.editableProperty().bind(isEditable);

        if (register) {
            this.getChildren().remove(changePasswordButton);
            passwordField = new TextField();
            this.add(passwordField, 1, 4);
        }

        setDev(dev);
    }

    public DevEditorGrid(Dev dev, boolean editable) throws IOException {
        this(dev, editable, false);
    }

    public DevEditorGrid(Dev dev) throws IOException {
        this(dev, true, false);
    }

    public DevEditorGrid() throws IOException {
        this(null, true, false);
    }

    public void setDev(Dev newDev) {
        this.dev = Objects.requireNonNullElseGet(newDev, Dev::new);

        firstNameField.setText(this.dev.getFirstname());
        lastNameField.setText(this.dev.getLastname());
        emailField.setText(this.dev.getEmail());
        githubField.setText(String.valueOf(this.dev.getGithub_id()));
    }

    @Override
    public boolean validateChange() {
        if (dev == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Internal error dev is null", ButtonType.OK);
            alert.showAndWait();
        } else if (firstNameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Your first name can't be empty", ButtonType.OK);
            alert.showAndWait();
        } else if (lastNameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Your last name can't be empty", ButtonType.OK);
            alert.showAndWait();
        } else if (emailField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Your email can't be empty", ButtonType.OK);
            alert.showAndWait();
        } else if (githubField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Your github id can't be empty", ButtonType.OK);
            alert.showAndWait();
        } else if (passwordField != null && passwordField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Your password can't be empty", ButtonType.OK);
            alert.showAndWait();
        } else {

            try {
                dev.setGithub_id(Integer.parseInt(githubField.getText()));
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "\"" + githubField.getText() + "\" is not a number", ButtonType.OK);
                alert.showAndWait();
                return false;
            }

            dev.setFirstname(firstNameField.getText());
            dev.setLastname(lastNameField.getText());
            dev.setEmail(emailField.getText());
            if (passwordField != null) {
                dev.setPassword(passwordField.getText());
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean applyChange() {
        return false;
    }

    @Override
    public Dev getEditedObject() {
        if (validateChange()) {
            return dev;
        } else {
            return null;
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by FXML
    public void OnPasswordChange(ActionEvent actionEvent) throws IOException {
        PasswordChangeDialog dialog = new PasswordChangeDialog(dev);
        dialog.showAndWait();
    }

}
