package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;

public class DevViewerController extends TabPane {

    private static final String FXML_FILE = "DevViewerController.fxml";

    @FXML
    public Label firstNameLabel;
    @FXML
    public Label lastNameLabel;
    @FXML
    public VBox projectVBox;

    private Dev dev;

    public DevViewerController(Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setDev(dev);
    }

    public void setDev(Dev dev) throws IOException {
        this.dev = dev;
        updateUI();
    }

    private void updateUI() throws IOException {
        firstNameLabel.setText(dev.getFirstName());
        lastNameLabel.setText(dev.getLastName().toUpperCase(Locale.ROOT));

        projectVBox.getChildren().clear();
        for (Project project :dev.getProjects()) {
            projectVBox.getChildren().add( new DevProjectController(project));
        }

    }

    @FXML
    public void OnEdit(ActionEvent actionEvent){

    }
}

