package io.taskmanager.ui.graphical;
import io.taskmanager.test.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class TaskController {

    private static final String FXML_FILE = "task.fxml";

    public static TitledPane loadNew(Task task) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        TitledPane pane = (TitledPane) fxmlLoader.load();
        ((TaskController)fxmlLoader.getController()).setTask(task);
        return pane;
    }

    @FXML
    public TitledPane titledPane;
    @FXML
    public TextField taskNameField;
    @FXML
    public TextArea contentTextArea;

    private Task task;
    private boolean validatedChange = false;

    public void setTask(Task task){
        this.task = task;
        taskNameField.setText(task.getName());
        contentTextArea.setText(task.getDescription());
    }

    public boolean isChangeValidated() {
        return validatedChange;
    }

    public void OnApply(ActionEvent actionEvent) {
        task.setName(taskNameField.getText());
        task.setDescription(contentTextArea.getText());
        validatedChange = true;
    }

    public void OnCancel(ActionEvent actionEvent) {
    }
}
