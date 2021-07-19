package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.RepositoryConflictHandler;
import io.taskmanager.test.RepositoryEditionConflict;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class DevEditorGrid extends GridPane implements IObjectEditor<Dev>{

    private static final String FXML_FILE = "DevEditorGrid.fxml";

    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField githubField;

    private final SimpleBooleanProperty isEditable;

    private Dev dev;

    public DevEditorGrid(Dev dev, boolean editable) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        isEditable = new SimpleBooleanProperty(editable);
        firstNameField.editableProperty().bind(isEditable);
        lastNameField.editableProperty().bind(isEditable);
        emailField.editableProperty().bind(isEditable);
        githubField.editableProperty().bind(isEditable);
        setDev(dev);
    }

    public DevEditorGrid(Dev dev) throws IOException {
        this(dev, true);
    }

    public DevEditorGrid() throws IOException {
        this(null, true);
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

    @Override
    public boolean validateChange(){
        if( dev == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Internal error dev is null", ButtonType.OK);
            alert.showAndWait();
        }
        else if( firstNameField.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Your first name can't be empty", ButtonType.OK);
            alert.showAndWait();
        }
        else if( lastNameField.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Your last name can't be empty", ButtonType.OK);
            alert.showAndWait();
        }
        else if( emailField.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Your email can't be empty", ButtonType.OK);
            alert.showAndWait();
        }
        else if( githubField.getText().isEmpty() )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Your github id can't be empty", ButtonType.OK);
            alert.showAndWait();
        }
        else
        {
            try {
                dev.setGithub_id( Integer.parseInt( githubField.getText()));
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "\""+githubField.getText()+"\" is not a number", ButtonType.OK);
                alert.showAndWait();
                return false;
            }

            dev.setFirstname( firstNameField.getText());
            dev.setLastname( lastNameField.getText());
            dev.setEmail( emailField.getText());

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
        if( validateChange()) {
            return dev;
        }else {
            return null;
        }
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

}
