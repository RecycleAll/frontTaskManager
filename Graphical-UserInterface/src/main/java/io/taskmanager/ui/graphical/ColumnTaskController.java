package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
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

public class ColumnTaskController extends BorderPane {

    private static final String FXML_FILE = "ColumnTaskController.fxml";

    @FXML
    public FlowPane DevsFlowPane;

    @FXML
    public Label taskTitleLabel;

    private Task task;
    private ProjectColumnController projectColumnController;

    public ColumnTaskController(ProjectColumnController projectColumnController, Task task) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setTask(task);
        this.projectColumnController = projectColumnController;
    }

    public ColumnTaskController(ProjectColumnController projectColumnController) throws IOException {
        this(projectColumnController, null);
    }

    public void setTask(Task task) {
        this.task = task;
        this.taskTitleLabel.setText(task.getName());
        DevsFlowPane.getChildren().clear();

        for (Dev dev :task.getDevs() ) {
            DevsFlowPane.getChildren().add( new DevButton(dev));
        }
    }

    public Task getTask() {
        return task;
    }

    public void OnView(ActionEvent actionEvent) throws IOException {
        TaskDialog dialog = new TaskDialog(projectColumnController.getProject(), task);
        Optional<Task> res = dialog.showAndWait();
        if( res.isEmpty() && dialog.isShouldBeDelete()){
            projectColumnController.removeTask(this);
        }
    }
}

class DevButton extends Button implements EventHandler<ActionEvent> {

    private final Dev dev;

    public DevButton(Dev dev) {
        super(dev.getFirstName());
        this.dev = dev;
        setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        System.out.println("clicked on " + dev.getFirstName());
    }
}
