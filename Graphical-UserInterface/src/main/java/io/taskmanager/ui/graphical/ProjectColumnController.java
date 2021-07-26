package io.taskmanager.ui.graphical;

import io.taskmanager.core.Column;
import io.taskmanager.core.Project;
import io.taskmanager.core.Task;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.repository.RepositoryObjectDeleted;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ProjectColumnController extends ScrollPane {

    private static final String FXML_FILE = "ProjectColumnController.fxml";

    private final RepositoryManager repository;

    @FXML
    public Label ColumnTitleLabel;
    @FXML
    public VBox TaskVBox;

    private Column column;
    private final ProjectController projectController;

    public ProjectColumnController(RepositoryManager repository, ProjectController projectController, Column column) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.repository = repository;
        setColumn(column);
        this.projectController = projectController;
    }


    public Column getColumn() {
        return column;
    }

    public void setColumn(Column newColumn) throws IOException {
        this.column = Objects.requireNonNullElseGet(newColumn, () -> new Column(repository));

        ColumnTitleLabel.setText(column.getName());
        for (Task task : column.getTasks()) {
            addTaskToTaskVBox(task);
        }
    }


    public void removeTask(ColumnTaskController taskController) {
        TaskVBox.getChildren().remove(taskController);
    }

    private void addTask(Task task) throws IOException {
        addTaskToTaskVBox(task);
        this.getScene().getWindow().sizeToScene();
    }

    private void addTaskToTaskVBox(Task task) throws IOException {
        TaskVBox.getChildren().add(new ColumnTaskController(repository, this, task));
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddTask(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        TaskDialog dialog = new TaskDialog(repository, getProject());
        Optional<Task> res = dialog.showAndWait();

        if (res.isPresent()) {
            Task task = res.get();
            try {
                column.addTask(task);
                addTask(task);
            } catch (RepositoryObjectDeleted repositoryObjectDeleted) {
                Column column = (Column) repositoryObjectDeleted.getObjects().get(0);
                Alert alert = new Alert(Alert.AlertType.ERROR, "The Column " + column.getName() + " has been deleted from the repo", ButtonType.OK);
                alert.showAndWait();

                repository.removeColumn(column);
                projectController.removeColumn(this);
            }
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnEdit(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        ColumnEditorDialog dialog = new ColumnEditorDialog(repository, column);
        Optional<Column> res = dialog.showAndWait();
        //System.err.println(" ProjectColumnController:OnEdit -> res:" + res.isPresent() + "  d: " + dialog.isShouldBeDelete());
        if (res.isPresent()) {
            ColumnTitleLabel.setText(res.get().getName());
        } else if (dialog.isShouldBeDelete()) {
            projectController.removeColumn(this);
        }
    }

    public Project getProject() {
        return projectController.getProject();
    }

}
