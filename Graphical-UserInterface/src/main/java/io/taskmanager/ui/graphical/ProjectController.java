package io.taskmanager.ui.graphical;

import io.taskmanager.core.Column;
import io.taskmanager.core.Dev;
import io.taskmanager.core.DevStatus;
import io.taskmanager.core.Project;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.repository.RepositoryObjectDeleted;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    @SuppressWarnings("unused") // used by FXML
    public ScrollPane columnScrollPane;
    @FXML
    @SuppressWarnings("unused") // used by FXML
    public BorderPane borderPane;

    private RepositoryManager repositoryManager;

    private Project project;
    private final int loggedDevId;

    public ProjectController(RepositoryManager repository, Project project, int loggedDevId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.loggedDevId = loggedDevId;
        this.repositoryManager = repository;
        setProject(project);

    }

    private boolean isProjectDevControllerEditable(Dev dev) {
        if (dev.getId() == loggedDevId) {
            return true;
        } else return project.getDevStatus(dev) == DevStatus.OWNER;
    }

    public void setProject(Project newProject) throws IOException {
        if (newProject == null) {
            this.project = new Project(repositoryManager);
        } else {
            this.project = newProject;
            this.repositoryManager = newProject.getRepository();
        }

        updateUI();
    }

    private void updateUI() throws IOException {
        projectTitle.setText(project.getName());

        devsVBox.getChildren().remove(1, devsVBox.getChildren().size());
        columnHBox.getChildren().clear();

        for (Dev dev : project.getDevs()) {
            devsVBox.getChildren().add(new ProjectDevController(this, dev, isProjectDevControllerEditable(dev)));
        }
        //System.err.println("UI col: " + project.getColumns());
        for (Column col : project.getColumns()) {
            columnHBox.getChildren().add(new ProjectColumnController(col.getRepository(), this, col));
        }
    }

    public Project getProject() {
        return project;
    }

    public void removeColumn(ProjectColumnController projectColumnController) throws ExecutionException, InterruptedException {
        project.removeColumn(projectColumnController.getColumn());
        columnHBox.getChildren().remove(projectColumnController);
    }

    private void addColumn(Column column) throws IOException {
        columnHBox.getChildren().add(new ProjectColumnController(column.getRepository(), this, column));
    }

    public void removeDev(ProjectDevController dev) throws ExecutionException, InterruptedException, RepositoryObjectDeleted {
        project.removeDev(dev.getDev());
        devsVBox.getChildren().remove(dev);
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddColumn(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        ColumnEditorDialog dialog = new ColumnEditorDialog(repositoryManager);
        Optional<Column> res = dialog.showAndWait();
        if (res.isPresent()) {
            Column col = res.get();
            col.setRepositoryManager(repositoryManager);
            project.addColumn(col);
            addColumn(col);
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddDev(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException, RepositoryObjectDeleted {
        ArrayList<Dev> devs = (ArrayList<Dev>) repositoryManager.getRepository().getAllDev();
        //System.err.println(devs);
        DevSelectorDialog dialog = new DevSelectorDialog(repositoryManager.getAllDev(), project.getDevs());

        Optional<List<Dev>> res = dialog.showAndWait();
        if (res.isPresent()) {
            project.updateDevs(res.get());
            updateUI();
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void onEditProject(ActionEvent actionEvent) throws IOException {

        ProjectEditorDialog projectEditorDialog = new ProjectEditorDialog(repositoryManager, project);
        Optional<Project> res = projectEditorDialog.showAndWait();
        if (res.isPresent()) {
            this.projectTitle.setText(project.getName());
        }
    }

}

