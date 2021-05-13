package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class TaskController {

    @FXML
    public TitledPane titledPane;
    @FXML
    public TextField taskNameField;
    @FXML
    public TextArea contentTextArea;

    private Task task;

    public void setTask(Task task){
        this.task = task;
        taskNameField.setText(task.getName());
        contentTextArea.setText(task.getDescription());
    }

    public void OnApply(ActionEvent actionEvent) {
        task.setName(taskNameField.getText());
        task.setDescription(contentTextArea.getText());

    }

    public void OnCancel(ActionEvent actionEvent) {
    }
}
