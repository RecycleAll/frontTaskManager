package io.taskmanager.ui.graphical;

import io.taskmanager.test.Column;
import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.Optional;

public class ProjectController extends BorderPane{

    private static final String FXML_FILE = "ProjectController.fxml";

    @FXML
    public VBox devsVBox;
    @FXML
    public Label projectTitle;
    @FXML
    public HBox columnHBox;

    private Project project;

    public ProjectController(Project project) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setProject(project);
    }

    public void setProject(Project newProject) throws IOException {
        if( newProject == null){
            this.project = new Project();
        }else {
            this.project = newProject;
        }

        projectTitle.setText(project.getName());

        for (Dev dev:project.getDevs()) {
            devsVBox.getChildren().add( new ProjectDevController(this, dev) );
        }

        for (Column col:project.getColumns()) {
            columnHBox.getChildren().add( new ProjectColumnController(this, col));
        }

    }

    public Project getProject() {
        return project;
    }

    public void removeColumn(ProjectColumnController projectColumnController){
        project.removeColumn(projectColumnController.getColumn());
        columnHBox.getChildren().remove(projectColumnController);
    }

    private void addColumn(Column column) throws IOException {
        project.addColumn(column);
        columnHBox.getChildren().add( new ProjectColumnController(this, column));
    }

    public void removeDev( ProjectDevController dev){
        project.removeDev(dev.getDev());
        devsVBox.getChildren().remove(dev);
    }

    private void addDev(Dev dev) throws IOException {
        project.addDev(dev);
        devsVBox.getChildren().add( new ProjectDevController(this, dev) );
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddColumn(ActionEvent actionEvent) throws IOException {
        ColumnEditorDialog dialog = new ColumnEditorDialog();
        Optional<Column> res = dialog.showAndWait();
        if(res.isPresent()){
            addColumn(res.get());
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddDev(ActionEvent actionEvent) throws IOException {
        DevEditorDialog devEditorDialog = new DevEditorDialog();
        Optional<Dev> res = devEditorDialog.showAndWait();
        if(res.isPresent()){
            this.addDev(res.get());
        }
    }
}

