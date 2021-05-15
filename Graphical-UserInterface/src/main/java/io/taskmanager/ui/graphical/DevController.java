package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

public class DevController {

    private static final String FXML_FILE = "DevController.fxml";

    public static DevController loadNew(Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.load();
        DevController devController = fxmlLoader.getController();
        devController.setDev(dev);
        return devController;
    }
    public static DevController loadNew() throws IOException {
        return loadNew(null);
    }

    @FXML
    private DialogPane dialogPane;
    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField githubField;

    private boolean isNewDev = false;
    private Dev dev;

    @FXML
    public void initialize() {
        if( !isNewDev) {
            ButtonType removeButton = new ButtonType("delete", ButtonBar.ButtonData.OTHER);
            dialogPane.getButtonTypes().add(removeButton);
        }

        Button applyButton = (Button) dialogPane.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if( dev == null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Internal error dev is null", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( firstNameField.getText().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your first name can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( lastNameField.getText().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your last name can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( emailField.getText().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your email can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( githubField.getText().isEmpty() )
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your github id can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else
            {
                try {
                    dev.setGithub_id( Integer.parseInt(githubField.getText()));
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "\""+githubField.getText()+"\" is not a number", ButtonType.OK);
                    alert.showAndWait();
                    actionEvent.consume();
                }
            }

            dev.setFirstName( firstNameField.getText());
            dev.setLastName( lastNameField.getText());
            dev.setEmail( emailField.getText());

        });
    }

    public void setDev(Dev newDev){
        if( newDev == null){
            this.dev = new Dev();
            isNewDev = true;
        }else{
            this.dev = newDev;
        }

        firstNameField.setText(this.dev.getFirstName());
        lastNameField.setText(this.dev.getLastName());
        emailField.setText(this.dev.getEmail());
        githubField.setText( String.valueOf(this.dev.getGithub_id()));

    }

    public void OnPasswordChange(ActionEvent actionEvent) throws IOException {
        PasswordChangeDialog dialog = new PasswordChangeDialog(dev);
        dialog.showAndWait();
    }

    public DialogPane getDialogPane() {
        return dialogPane;
    }

    public Dev getDev() {
        return dev;
    }


}
