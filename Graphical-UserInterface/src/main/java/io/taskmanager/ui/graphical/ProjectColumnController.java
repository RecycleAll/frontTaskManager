package io.taskmanager.ui.graphical;
import io.taskmanager.test.Column;
import io.taskmanager.test.Project;
import io.taskmanager.test.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class ProjectColumnController extends ScrollPane{

    private static final String FXML_FILE = "ProjectColumnController.fxml";

    public ProjectColumnController( ProjectController projectController, Column column) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setColumn(column);
        this.projectController = projectController;
    }
    public ProjectColumnController( ProjectController projectController) throws IOException {
        this(projectController, null);
    }

    @FXML
    public Label ColumnTitleLabel;
    @FXML
    public VBox TaskVBox;

    private Column column;
    private ProjectController projectController;

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column newColumn) throws IOException {
        if( newColumn == null){
            this.column = new Column();
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
    }

    private void addTaskToTaskVBox(Task task) throws IOException {
        TaskVBox.getChildren().add( new ColumnTaskController(this, task) );
    }

    public void OnAddTask(ActionEvent actionEvent) throws IOException {
        TaskDialog dialog = new TaskDialog(getProject());
        Optional<Task> res = dialog.showAndWait();
        if(res.isPresent()){
            addTask(res.get());
        }
    }

    public Project getProject(){
        return projectController.getProject();
    }

    public void OnEdit(ActionEvent actionEvent) throws IOException {
        ColumnEditorDialog dialog = new ColumnEditorDialog(column);
        Optional<Column> res = dialog.showAndWait();
        System.out.println(res);
        if( res.isPresent()){
            ColumnTitleLabel.setText( res.get().getName());
        }
        else if( dialog.isShouldBeDelete() ){
            System.out.println("test");
            projectController.removeColumn(this);
        }

    }
}
