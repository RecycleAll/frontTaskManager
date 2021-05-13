package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Task;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Optional;

public class TaskEditor{

    private static final String FXML_FILE = "TaskEditor.fxml";
    public FlowPane devsFlowPane;

    public static TaskEditor loadNew(Task task) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        DialogPane pane = fxmlLoader.load();
        TaskEditor taskEditor = fxmlLoader.getController();
        taskEditor.setTask(task);
        taskEditor.setPane(pane);
        return taskEditor;
    }
    public static TaskEditor loadNew() throws IOException {
        return loadNew(null);
    }

    public static DialogPane loadNewPane(Task task) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        DialogPane pane = fxmlLoader.load();
        TaskEditor taskEditor = fxmlLoader.getController();
        taskEditor.setTask(task);
        return pane;
    }

    public static Dialog<ButtonType> loadNewDialog(Task task) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        DialogPane pane = fxmlLoader.load();
        TaskEditor taskEditor = fxmlLoader.getController();
        taskEditor.setTask(task);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(pane);
        return dialog;
    }

    @FXML
    public TextArea taskDescriptionArea;
    @FXML
    public TextField taskNameField;

    private DialogPane pane;

    private Task task;

    public DialogPane getPane() {
        return pane;
    }

    public void setPane(DialogPane pane) {
        this.pane = pane;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task newTask) {
        if( newTask == null){
            this.task = new Task();
        }else{
            this.task = new Task(newTask);
        }
        taskNameField.setText(this.task.getName());
        taskDescriptionArea.setText(this.task.getDescription());
        taskNameField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            task.setName(newValue);
        });
        taskDescriptionArea.textProperty().addListener((observableValue, oldValue, newValue) -> {
            task.setName(newValue);
        });

        for (Dev dev: task.getDevs()) {
            addDevToFlowPane(dev);
        }
    }

    private void removeDev(DevButton devButton){
        devsFlowPane.getChildren().remove(devButton);
        task.removeDev(devButton.dev);
    }

    private void addDev(Dev dev){
        addDevToFlowPane(dev);
        task.addDev(dev);
    }

    private void addDevToFlowPane(Dev dev){
        devsFlowPane.getChildren().add(devsFlowPane.getChildren().size() - 1, new DevButton(this, dev));
    }

    public void OnAddDev(ActionEvent actionEvent) throws IOException {
        DevDialog devDialog = new DevDialog();
        Optional<Dev> res =devDialog.showAndWait();
        res.ifPresent(this::addDev);
    }

    static class DevButton extends Button{
        public Dev dev;
        public DevButton(TaskEditor editor, Dev dev) {
            this.dev = dev;
            HBox hBox = new HBox();
            Label label = new Label(dev.getFirstName());
            Button button = new Button("remove");
            button.setOnAction(actionEvent -> {
                editor.removeDev(this);
            });
            hBox.getChildren().addAll(label, button);

            this.setOnAction(actionEvent -> {
                //TODO open dev viewer
            });
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.setGraphic(hBox);
            this.setAlignment(Pos.CENTER_LEFT);
        }

    }
}


