package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class TaskController {

    private static final String FXML_FILE = "TaskController.fxml";

    public static TaskController loadNew(Task task) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.load();
        TaskController taskController = fxmlLoader.getController();
        taskController.setTask(task);
        return taskController;
    }
    public static TaskController loadNew() throws IOException {
        return loadNew(null);
    }

    @FXML
    public TextArea taskDescriptionArea;
    @FXML
    public TextField taskNameField;
    @FXML
    public FlowPane devsFlowPane;
    @FXML
    public DialogPane dialogPane;

    private Task task;

    private final ArrayList<Dev> devToAdd, devToRemove;

    public TaskController() {
        devToAdd = new ArrayList<>();
        devToRemove = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        Button applyButton = (Button) dialogPane.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if( task == null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Internal error task is null", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( taskNameField.getText().isEmpty() )
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "task name can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if(task.getDevs().size() - devToRemove.size() + devToAdd.size() <= 0)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You need to have at least 1 dev assigned too a task.\nyou have currently: "+ (task.getDevs().size() - devToRemove.size() + devToAdd.size())+ "dev assigned!", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }else{
                task.setName( taskNameField.getText());
                task.setDescription( taskDescriptionArea.getText());
                task.getDevs().removeAll( devToRemove);
                task.getDevs().addAll( devToAdd);
            }

        });
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

        devToAdd.clear();
        devToRemove.clear();

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
        devToRemove.add(devButton.dev);
    }

    private void addDev(Dev dev){
        addDevToFlowPane(dev);
        devToAdd.add(dev);
    }

    private void addDevToFlowPane(Dev dev){
        devsFlowPane.getChildren().add(devsFlowPane.getChildren().size() - 1, new DevButton(this, dev));
    }

    public void OnAddDev(ActionEvent actionEvent) throws IOException {
        DevDialog devDialog = new DevDialog();
        Optional<Dev> res = devDialog.showAndWait();
        res.ifPresent(this::addDev);
    }

    static class DevButton extends Button{
        public Dev dev;
        public DevButton(TaskController editor, Dev dev) {
            super();
            this.dev = dev;
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            HBox hBox = new HBox(10); // 10 spacing
            hBox.setAlignment(Pos.CENTER_LEFT);
            Label label = new Label(dev.getFirstName());
            Button button = new Button("remove");
            button.setOnAction(actionEvent -> {
                editor.removeDev(this);
            });
            hBox.getChildren().addAll(label, button);
            this.setGraphic(hBox);

            this.setOnAction(actionEvent -> {
                //TODO open dev viewer
            });
        }

    }
}


