package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
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
    @FXML
    public Button editButton;

    private Dev dev;
    private ProjectController projectController;
    private SimpleBooleanProperty editable;

    public ProjectDevController(@NotNull ProjectController projectController, @NotNull Dev dev, boolean editable) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.editable = new SimpleBooleanProperty(editable);
        editButton.visibleProperty().bind(this.editable);
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
        firstNameLabel.setText( dev.getFirstname());
        lastNameLabel.setText( dev.getLastname());
    }

    public void OnView(ActionEvent actionEvent) throws IOException {
        DevEditorDialog devEditorDialog = new DevEditorDialog(dev);
        Optional<Dev> res = devEditorDialog.showAndWait();
        if(res.isPresent()){
            updateUI();
        }
        else if( devEditorDialog.isShouldBeDelete()){
            projectController.removeDev(this);
        }
    }

    public Dev getDev() {
        return dev;
    }
}

