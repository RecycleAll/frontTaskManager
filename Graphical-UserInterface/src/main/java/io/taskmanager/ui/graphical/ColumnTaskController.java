package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.RepositoryManager;
import io.taskmanager.test.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ColumnTaskController extends BorderPane {

    private static final String FXML_FILE = "ColumnTaskController.fxml";

    private final RepositoryManager repository;

    @FXML
    public FlowPane DevsFlowPane;
    @FXML
    public Label taskTitleLabel;

    private Task task;
    private final ProjectColumnController projectColumnController;

    public ColumnTaskController(RepositoryManager repository, ProjectColumnController projectColumnController, Task task) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.repository = repository;
        setTask(task);
        this.projectColumnController = projectColumnController;
    }

    public ColumnTaskController(RepositoryManager repository, ProjectColumnController projectColumnController) throws IOException {
        this(repository, projectColumnController, null);
    }

    public void setTask(Task task) {
        this.task = task;
        updateUI();
    }

    private void updateUI(){
        taskTitleLabel.setText(task.getName());
        DevsFlowPane.getChildren().clear();

        for (Dev dev :task.getDevs() ) {
            DevsFlowPane.getChildren().add(new DevButton(dev));
        }
    }



    public Task getTask() {
        return task;
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml
    public void OnView(ActionEvent actionEvent) throws IOException, ExecutionException, InterruptedException {
        TaskDialog dialog = new TaskDialog(repository, projectColumnController.getProject(), task);
        Optional<Task> res = dialog.showAndWait();

        if( res.isPresent()){
            res.get().updateToRepo();
            updateUI();
        }
        else if(dialog.isShouldBeDelete()){
            projectColumnController.removeTask(this);
        }
    }

    private static class DevButton extends Button implements EventHandler<ActionEvent> {

        private final Dev dev;

        public DevButton(Dev dev) {
            super(dev.getFirstname());
            this.dev = dev;
            setOnAction(this);
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("clicked on " + dev.getFirstname());
        }
    }
}


