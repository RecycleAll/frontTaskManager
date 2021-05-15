package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import io.taskmanager.test.Task;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
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

public class TaskController extends DialogPane {

    private static final String FXML_FILE = "TaskController.fxml";

    @FXML
    public TextArea taskDescriptionArea;
    @FXML
    public TextField taskNameField;
    @FXML
    public FlowPane devsFlowPane;

    private Task task;
    private Project project;

    private final SimpleBooleanProperty isNewTask;

    private ArrayList<Dev>  newDevList;

    public TaskController(Project project) throws IOException {
        this(project, null);
    }

    public TaskController(Project project, Task task) throws IOException {
        isNewTask = new SimpleBooleanProperty(false);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setTask(task);
        this.project = project;
    }

    @FXML
    public void initialize() {
        ButtonType removeButtonType = new ButtonType("delete", ButtonBar.ButtonData.OTHER);
        this.getButtonTypes().add(removeButtonType);
        Button removeButton = (Button) this.lookupButton(removeButtonType);
        removeButton.visibleProperty().bind(Bindings.createBooleanBinding(() -> !isNewTask.get(), isNewTask));

        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
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
            else if( newDevList.size() <= 0)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You need to have at least 1 dev assigned too a task.", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }else{
                task.setName( taskNameField.getText());
                task.setDescription( taskDescriptionArea.getText());
                task.setDevs(newDevList);
            }

        });
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task newTask) {
        if( newTask == null){
            this.task = new Task();
            isNewTask.set(true);
        }else{
            this.task = newTask;
        }
        newDevList = this.task.getDevs();

        taskNameField.setText(this.task.getName());
        taskDescriptionArea.setText(this.task.getDescription());

        for (Dev dev: task.getDevs()) {
            addDevToFlowPane(dev);
        }
    }

    private void removeDev(DevButton devButton){
        devsFlowPane.getChildren().remove(devButton);
    }

    private void addDev(Dev dev){
        addDevToFlowPane(dev);
    }

    private void setDevs(ArrayList<Dev> devs){
        devsFlowPane.getChildren().remove(0, devsFlowPane.getChildren().size() - 1);

        for (Dev dev: devs ) {
            addDevToFlowPane(dev);
        }
        newDevList = devs;
    }

    private void addDevToFlowPane(Dev dev){
        devsFlowPane.getChildren().add(devsFlowPane.getChildren().size() - 1, new DevButton(this, dev));
    }

    @FXML
    public void OnAddDev(ActionEvent actionEvent) throws IOException {
        DevSelectorDialog dialog = new DevSelectorDialog( project, newDevList);
        Optional<ArrayList<Dev>> res = dialog.showAndWait();

        if( res.isPresent()){
            System.out.println(res.get().size());
            this.setDevs(res.get());
        }

       /* DevDialog devDialog = new DevDialog();
        Optional<Dev> res = devDialog.showAndWait();
        res.ifPresent(this::addDev);*/
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


