package io.taskmanager.ui.graphical;

import io.taskmanager.test.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class TaskController extends DialogPane implements IObjectEditor<Task>{

    private static final String FXML_FILE = "TaskController.fxml";

    private final RepositoryManager repository;

    @FXML
    public TextArea taskDescriptionArea;
    @FXML
    public TextField taskNameField;
    @FXML
    public FlowPane devsFlowPane;
    @FXML
    public DatePicker limitDatePicker;
    @FXML
    public Button selectDevButton;


    private Task task;
    private final Project project;

    private final SimpleBooleanProperty isNewTask;

    private List<Dev> backUpDevList;

    public TaskController(RepositoryManager repository, Project project, Task task, boolean editable) throws IOException {
        this.repository = repository;
        isNewTask = new SimpleBooleanProperty(false);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setTask(task);
        this.project = project;
        setEditable(editable);
    }

    public TaskController(RepositoryManager repository, Project project, Task task) throws IOException {
        this(repository, project, task, true);
    }

    public TaskController(RepositoryManager repository, Project project) throws IOException {
        this(repository, project, null, true);
    }

    private void setEditable(boolean editable){
        taskDescriptionArea.setEditable(editable);
        taskNameField.setEditable(editable);
        limitDatePicker.setEditable(editable);
        selectDevButton.setVisible(editable);
    }

    @FXML
    public void initialize() {
        ButtonType removeButtonType = new ButtonType("delete", ButtonBar.ButtonData.OTHER);
        this.getButtonTypes().add(removeButtonType);
        Button removeButton = (Button) this.lookupButton(removeButtonType);
        removeButton.visibleProperty().bind(Bindings.createBooleanBinding(() -> !isNewTask.get(), isNewTask));

        Button cancelButton = (Button) this.lookupButton(ButtonType.CANCEL);
        cancelButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            task.setDevs(backUpDevList);
        });

        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {

            if( applyChange() ){
                try {
                    task.updateToRepo();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } catch (RepositoryEditionConflict repositoryEditionConflict) {
                    try {
                        TaskConflictDialog dialog = new TaskConflictDialog( (RepositoryConflictHandler<Task>) repositoryEditionConflict.getConflictHandler(), project);
                        Optional<Task> res = dialog.showAndWait();
                        if (res.isPresent()) {
                            task.setAll(res.get());
                            System.out.println("///////////////////////////////////////////////");
                            System.out.println("task:name -> "+task.getName());
                            task.updateToRepo(true);
                            System.out.println("///////////////////////////////////////////////");
                        }
                    } catch (IOException | RepositoryEditionConflict | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    public void setTask(Task newTask) {
        if( newTask == null){
            this.task = new Task(repository);
            isNewTask.set(true);
        }else{
            this.task = newTask;
        }
        backUpDevList = this.task.getDevs();

        taskNameField.setText(this.task.getName());
        taskDescriptionArea.setText(this.task.getDescription());
        limitDatePicker.setValue( this.task.getLimitDate());

        for (Dev dev: this.task.getDevs()) {
            addDevToFlowPane(dev);
        }
    }

    private void removeDev(DevButton devButton){
        devsFlowPane.getChildren().remove(devButton);
    }

    private void addDev(Dev dev){
        addDevToFlowPane(dev);
    }

    private void setDevs(List<Dev> devs) throws ExecutionException, InterruptedException {
        devsFlowPane.getChildren().remove(0, devsFlowPane.getChildren().size() - 1); // the last child id the add button

        for (Dev dev: devs ) {
            addDevToFlowPane(dev);
        }

        task.updateDevs(devs);
    }

    private void addDevToFlowPane(Dev dev){
        devsFlowPane.getChildren().add(devsFlowPane.getChildren().size() - 1, new DevButton(this, dev));
    }

    @FXML
    public void OnAddDev(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        DevSelectorDialog dialog = new DevSelectorDialog( project, task);
        Optional<List<Dev>> res = dialog.showAndWait();

        if( res.isPresent()){
            System.out.println(res.get().size());
            this.setDevs(res.get());
        }
    }

    @Override
    public boolean validateChange() {
        if( task == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Internal error task is null", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        else if( taskNameField.getText().isEmpty() )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "task name can't be empty", ButtonType.OK);
            alert.showAndWait();
            return false;

        }else if(limitDatePicker.getValue() == null  ){
            Alert alert = new Alert(Alert.AlertType.ERROR, "You can't set the limit date to be before the creation date", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @Override
    public boolean applyChange() {
        if(validateChange()) {
            task.setName(taskNameField.getText());
            task.setDescription(taskDescriptionArea.getText());
            task.setLimitDate(limitDatePicker.getValue().atStartOfDay().toLocalDate());
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Task getEditedObject() {
        applyChange();
        return task;
    }

    static class DevButton extends Button{
        public Dev dev;
        public DevButton(TaskController editor, Dev dev) {
            super(dev.getFirstname());
            this.dev = dev;

            this.setOnAction(actionEvent -> {
                //TODO open dev viewer
            });
        }

    }
}


