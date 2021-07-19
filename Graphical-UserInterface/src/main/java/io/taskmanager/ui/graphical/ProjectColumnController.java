package io.taskmanager.ui.graphical;
import io.taskmanager.core.*;
import io.taskmanager.core.repository.RepositoryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ProjectColumnController extends ScrollPane{

    private static final String FXML_FILE = "ProjectColumnController.fxml";

    private final RepositoryManager repository;

    @FXML
    public Label ColumnTitleLabel;
    @FXML
    public VBox TaskVBox;

    private Column column;
    private ProjectController projectController;

    public ProjectColumnController(RepositoryManager repository, ProjectController projectController, Column column) throws IOException, ExecutionException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.repository = repository;
        setColumn(column);
        this.projectController = projectController;
    }
    public ProjectColumnController(RepositoryManager repository, ProjectController projectController) throws IOException, ExecutionException, InterruptedException {
        this(repository, projectController, null);
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column newColumn) throws IOException, ExecutionException, InterruptedException {
        if( newColumn == null){
            this.column = new Column(repository);
        }else {
            this.column = newColumn;
        }
        ColumnTitleLabel.setText(column.getName());
        //TODO add task preview to TaskVBox
        for (Task task: column.getTasks()) {
            addTaskToTaskVBox(task);
        }
    }

    public void setProjectController(ProjectController projectController) {
        this.projectController = projectController;
    }

    public void removeTask( ColumnTaskController taskController) throws ExecutionException, InterruptedException {
        column.removeTask(taskController.getTask());
        TaskVBox.getChildren().remove(taskController);
    }

    private void addTask(Task task) throws IOException {
        addTaskToTaskVBox(task);
        this.getScene().getWindow().sizeToScene();
    }

    private void addTaskToTaskVBox(Task task) throws IOException {
        TaskVBox.getChildren().add( new ColumnTaskController(repository, this, task) );
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddTask(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        TaskDialog dialog = new TaskDialog(repository, getProject());
        Optional<Task> res = dialog.showAndWait();
        if(res.isPresent()){
            Task task = res.get();
            task = column.addNewTask(task.getName(), task.getDescription(), task.getLimitDate());
            addTask(task);
        }
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnEdit(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        ColumnEditorDialog dialog = new ColumnEditorDialog(repository, column);
        Optional<Column> res = dialog.showAndWait();
        if( res.isPresent()){
            ColumnTitleLabel.setText( res.get().getName());
        }
        else if( dialog.isShouldBeDelete() ){
            projectController.removeColumn(this);
        }
    }

    public Project getProject(){
        return projectController.getProject();
    }

}
