package io.taskmanager.ui.graphical;
import io.taskmanager.test.Column;
import io.taskmanager.test.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class ColumnViewer {

    @FXML
    public Label ColumnTitleLabel;
    @FXML
    public VBox TaskVBox;

    private Column column;

    public ColumnViewer(){}

    public ColumnViewer(Column column) throws IOException {
        setColumn(column);
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) throws IOException {
        this.column = column;
        ColumnTitleLabel.setText(column.getName());
        //TODO add task preview to TaskVBox
        for (Task task: column.getTasks()) {
            addTaskToTaskVBox(task);
        }
    }

    private void addTask(Task task) throws IOException {
        column.addTask(task);
        addTaskToTaskVBox(task);
    }

    private void addTaskToTaskVBox(Task task) throws IOException {
        TaskVBox.getChildren().add( ColumnTaskViewer.loadNew(task) );
    }

    public void OnAddTask(ActionEvent actionEvent) throws IOException {
        TaskDialog dialog = new TaskDialog();
        Optional<Task> res = dialog.showAndWait();
        if(res.isPresent()){
            addTask(res.get());
        }
    }


}
