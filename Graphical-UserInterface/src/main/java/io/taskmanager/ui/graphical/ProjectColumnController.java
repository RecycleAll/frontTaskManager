package io.taskmanager.ui.graphical;
import io.taskmanager.test.Column;
import io.taskmanager.test.Project;
import io.taskmanager.test.Task;
import io.taskmanager.test.TaskRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ProjectColumnController extends ScrollPane{

    private static final String FXML_FILE = "ProjectColumnController.fxml";

    private final TaskRepository repository;

    @FXML
    public Label ColumnTitleLabel;
    @FXML
    public VBox TaskVBox;

    private Column column;
    private ProjectController projectController;

    public ProjectColumnController(TaskRepository repository, ProjectController projectController, Column column) throws IOException, ExecutionException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.repository = repository;
        setColumn(column);
        this.projectController = projectController;
    }
    public ProjectColumnController(TaskRepository repository, ProjectController projectController) throws IOException, ExecutionException, InterruptedException {
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

    public void removeTask( ColumnTaskController taskController){
        column.removeTask(taskController.getTask());
        TaskVBox.getChildren().remove(taskController);
    }

    private void addTask(Task task) throws IOException {
        column.addTask(task);
        addTaskToTaskVBox(task);
        this.getScene().getWindow().sizeToScene();
    }

    private void addTaskToTaskVBox(Task task) throws IOException {
        TaskVBox.getChildren().add( new ColumnTaskController(repository, this, task) );
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnAddTask(ActionEvent actionEvent) throws IOException {
        TaskDialog dialog = new TaskDialog(repository, getProject());
        Optional<Task> res = dialog.showAndWait();
        if(res.isPresent()){
            addTask(res.get());
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
