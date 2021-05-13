package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Optional;

public class ColumnTaskViewer {

    private static final String FXML_FILE = "ColumnTaskViewer.fxml";

    public static Pane loadNew(Task task) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        Pane pane = fxmlLoader.load();
        ((ColumnTaskViewer)fxmlLoader.getController()).setTask(task);
        return pane;
    }

    @FXML
    public FlowPane DevsFlowPane;

    @FXML
    public Label taskTitleLabel;

    private Task task;

    public void setTask(Task task) {
        this.task = task;
        this.taskTitleLabel.setText(task.getName());
        DevsFlowPane.getChildren().clear();

        for (Dev dev :task.getDevs() ) {
            DevsFlowPane.getChildren().add( new DevButton(dev));
        }
    }

    public void OnView(ActionEvent actionEvent) throws IOException {
        TaskEditorDialog dialog = new TaskEditorDialog(task);
        Optional<Task> res = dialog.showAndWait();
        res.ifPresent(this::setTask);
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
