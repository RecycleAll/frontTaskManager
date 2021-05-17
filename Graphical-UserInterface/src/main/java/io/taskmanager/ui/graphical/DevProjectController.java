package io.taskmanager.ui.graphical;

import io.taskmanager.test.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DevProjectController extends AnchorPane {

    private static final String FXML_FILE = "DevProjectController.fxml";

    @FXML
    public Label projectNameLabel;

    private Project project;

    public DevProjectController(@NotNull Project project) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setProject(project);
    }

    public void setProject(Project project) {
        this.project = project;
        updateUI();
    }

    public void updateUI(){
        projectNameLabel.setText(project.getName());
    }

    public Project getProject() {
        return project;
    }

    @FXML
    public void OnMouseClicked(MouseEvent mouseEvent){
        System.out.println("test");
    }

    @FXML
    public void OnEdit(ActionEvent actionEvent){

    }

}
