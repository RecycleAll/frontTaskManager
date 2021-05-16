package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

public class ProjectDevController extends AnchorPane{

    private static final String FXML_FILE = "ProjectDevViewer.fxml";

    @FXML
    public Label firstNameLabel;
    @FXML
    public Label lastNameLabel;

    private Dev dev;
    private ProjectController projectController;

    public ProjectDevController(@NotNull ProjectController projectController, @NotNull Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setDev(dev);
        setProject(projectController);
    }

    public void setProject(@NotNull ProjectController projectController) {
        this.projectController = projectController;
    }

    public void setDev(@NotNull Dev dev) {
        this.dev = dev;
        updateUI();
    }

    public void updateUI(){
        firstNameLabel.setText( dev.getFirstName());
        lastNameLabel.setText( dev.getLastName());
    }

    public void OnView(ActionEvent actionEvent) throws IOException {
        DevDialog devDialog = new DevDialog(dev);
        Optional<Dev> res = devDialog.showAndWait();
        if(res.isPresent()){
            updateUI();
        }
        else if( devDialog.isShouldBeDelete()){
            projectController.removeDev(this);
        }
    }

    public Dev getDev() {
        return dev;
    }
}

