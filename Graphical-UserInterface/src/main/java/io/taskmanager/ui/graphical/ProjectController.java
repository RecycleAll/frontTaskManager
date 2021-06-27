package io.taskmanager.ui.graphical;

import io.taskmanager.test.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ProjectController extends BorderPane {

    private static final String FXML_FILE = "ProjectController.fxml";

    @FXML
    public VBox devsVBox;
    @FXML
    public Label projectTitle;
    @FXML
    public HBox columnHBox;
    @FXML
    public ScrollPane columnScrollPane;
    @FXML
    public BorderPane borderPane;

    private final TaskRepository repo;

    private Project project;
    private int loggedDevId;

    public ProjectController(TaskRepository repo, Project project, int loggedDevId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.loggedDevId = loggedDevId;
        this.repo = repo;
        setProject(project);

    }

    private boolean isProjectDevControllerEditable(Dev dev){
        if( dev.getId() == loggedDevId){
            return true;
        }else if( project.getDevStatus(dev) == DevStatus.OWNER){
            return  true;
        }

        return false;
    }

    public void setProject(Project newProject) throws IOException {
        if( newProject == null){
            this.project = new Project(repo);
        }else {
            this.project = newProject;
        }

        updateUI();
    }

    private void updateUI() throws IOException {
        projectTitle.setText(project.getName());

        devsVBox.getChildren().remove(1, devsVBox.getChildren().size());
        columnHBox.getChildren().clear();

        for (Dev dev:project.getDevs()) {
            devsVBox.getChildren().add( new ProjectDevController(this, dev, isProjectDevControllerEditable(dev)) );
        }

        for (Column col:project.getColumns()) {
            columnHBox.getChildren().add( new ProjectColumnController(this, col));
        }
    }

    public Project getProject() {
        return project;
    }

    public void removeColumn(ProjectColumnController projectColumnController) throws ExecutionException, InterruptedException {
        project.removeColumn(projectColumnController.getColumn());
        columnHBox.getChildren().remove(projectColumnController);
    }

    private void addColumn(Column column) throws IOException, ExecutionException, InterruptedException {
        project.addColumn(column);

        //System.out.println("borderPane width" + borderPane.minWidthProperty().get() + " < " + borderPane.getWidth() +" < " +borderPane.getMinWidth());

        columnHBox.getChildren().add( new ProjectColumnController(this, column));
        //this.getTabPane().setPrefWidth( borderPane.getWidth());
    }

    public void removeDev( ProjectDevController dev){
        project.removeDev(dev.getDev());
        devsVBox.getChildren().remove(dev);
    }

    private void addDev(Dev dev) throws IOException {
        project.addDev(dev);
        devsVBox.getChildren().add( new ProjectDevController(this, dev, isProjectDevControllerEditable(dev) ) );
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddColumn(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        ColumnEditorDialog dialog = new ColumnEditorDialog();
        Optional<Column> res = dialog.showAndWait();
        if(res.isPresent()){
            addColumn(res.get());
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddDev(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        ArrayList<Dev> devs = (ArrayList<Dev>) repo.getAllDev();
        System.out.println(devs);
        DevSelectorDialog dialog = new DevSelectorDialog(project, devs);

        Optional<List<Dev>> res = dialog.showAndWait();
        if(res.isPresent()){
            List<Dev> projectDevs = project.getDevs();
            List<Dev> newProjectDevs = res.get();

            for (Dev dev :newProjectDevs) {
                project.addDev(dev);
            }

            for (Dev dev :projectDevs) {
                if( !newProjectDevs.contains(dev)){
                    project.removeDev(dev);
                }
            }
            updateUI();
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void onEditProject(ActionEvent actionEvent) throws IOException{

        ProjectEditorDialog projectEditorDialog = new ProjectEditorDialog(project);
        Optional<Project> res = projectEditorDialog.showAndWait();
        if(res.isPresent()){
            this.projectTitle.setText(project.getName());
        }
    }

}

