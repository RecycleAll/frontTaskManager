package io.taskmanager.ui.graphical;

import io.taskmanager.core.*;
import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryEditionConflict;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.repository.RepositoryObjectDeleted;
import io.taskmanager.ui.graphical.conflict.IObjectEditor;
import io.taskmanager.ui.graphical.conflict.RepositoryConflictDialog;
import io.taskmanager.ui.graphical.conflict.TaskConflictController;
import io.taskmanager.ui.graphical.conflict.TaskConflictDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class TaskController extends DialogPane implements IObjectEditor<Task> {

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

    private List<Dev> editedDevList;

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

        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {

            if( applyChange() ){
                try {
                    task.updateToRepo();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } catch (RepositoryEditionConflict repositoryEditionConflict) {
                    try {
                        //TaskConflictDialog dialog = new TaskConflictDialog( (RepositoryConflictHandler<Task>) repositoryEditionConflict.getConflictHandler(), project);
                        RepositoryConflictDialog<Task> dialog = new RepositoryConflictDialog<Task>( new TaskConflictController((RepositoryConflictHandler<Task>) repositoryEditionConflict.getConflictHandler(), project));
                        Optional<Task> res = dialog.showAndWait();
                        if (res.isPresent()) {
                            task.setAll(res.get());
                            System.out.println("///////////////////////////////////////////////");
                            System.out.println("task:name -> "+task.getName());
                            task.updateToRepo(true);
                            System.out.println("task:name -> "+task.getName());
                            System.out.println("///////////////////////////////////////////////");
                        }
                    } catch (IOException | RepositoryEditionConflict | ExecutionException | InterruptedException | RepositoryObjectDeleted e) {
                        e.printStackTrace();
                    }
                } catch (RepositoryObjectDeleted repositoryObjectDeleted) {
                    Task task = (Task) repositoryObjectDeleted.getObjects().get(0);
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The TAsk "+task.getName()+" has been deleted from the repo", ButtonType.OK);
                    alert.showAndWait();

                    try {
                        repository.removeTask(this.task);
                        this.task = null;
                    } catch (ExecutionException | InterruptedException objectDeleted) {
                        objectDeleted.printStackTrace();
                    }
                }
            }

        });
    }

    public void setTask(Task newTask) {
        if( newTask == null){
            this.task = new Task();
            isNewTask.set(true);
        }else{
            this.task = newTask;
        }

        editedDevList = new ArrayList<>( this.task.getDevs());

        taskNameField.setText(this.task.getName());
        taskDescriptionArea.setText(this.task.getDescription());
        limitDatePicker.setValue( this.task.getLimitDate());

        for (Dev dev: editedDevList) {
            addDevToFlowPane(dev);
        }
    }

    private void removeDev(DevButton devButton){
        devsFlowPane.getChildren().remove(devButton);
    }

    private void setDevs(List<Dev> devs) throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted {
        devsFlowPane.getChildren().remove(0, devsFlowPane.getChildren().size() - 1); // the last child is the add button

        for (Dev dev: devs ) {
            addDevToFlowPane(dev);
        }

        editedDevList = devs;
    }

    private void addDevToFlowPane(Dev dev){
        devsFlowPane.getChildren().add(devsFlowPane.getChildren().size() - 1, new DevButton(this, dev));
    }

    @FXML
    public void OnAddDev(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted {
        DevSelectorDialog dialog = new DevSelectorDialog( project.getDevs(), editedDevList);
        Optional<List<Dev>> res = dialog.showAndWait();

        if( res.isPresent()){
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
            try {
                task.updateDevs(editedDevList);
            } catch (ExecutionException | RepositoryEditionConflict | InterruptedException e) {
                e.printStackTrace();
            } catch ( RepositoryObjectDeleted e){

                Dev devs = (Dev) e.getObjects().get(0);
                Alert alert = new Alert(Alert.AlertType.ERROR, "The dev "+devs.getFirstname()+" you are trying to add has been deleted from the repo and will be removed", ButtonType.OK);
                alert.showAndWait();

                for (RepositoryObject<?> obj:e.getObjects()) {
                    try {
                        repository.removeDev((Dev) obj);
                    } catch (ExecutionException | RepositoryObjectDeleted | InterruptedException executionException) {
                        executionException.printStackTrace();
                    }
                }
            }
            return true;
        }else{
            return false;
        }
    }

    public Task getTask() {
        return task;
    }

    @Override
    public Task getEditedObject() {
        System.out.println("taskController:getEditedObject -> "+task.getName());
        applyChange();
        System.out.println("taskController:getEditedObject -> "+task.getName());
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


