package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;

public class DevViewerController extends TabPane {

    private static final String FXML_FILE = "DevViewerController.fxml";

    @FXML
    public TextField firstNameLabel;
    @FXML
    public TextField lastNameLabel;
    @FXML
    public VBox projectVBox;

    private Dev dev;

    public DevViewerController(Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
    }

    public void setDev(Dev dev){
        this.dev = dev;

    }

    private void updateUI(){
        firstNameLabel.setText(dev.getFirstname());
        lastNameLabel.setText(dev.getLastname().toUpperCase(Locale.ROOT));

        projectVBox.getChildren().clear();
        for (Project project :dev.getProjects()) {
            projectVBox.getChildren().add( new Label(project.getName()));
        }

    }

    @FXML
    public void OnEdit(ActionEvent actionEvent){

    }
}

