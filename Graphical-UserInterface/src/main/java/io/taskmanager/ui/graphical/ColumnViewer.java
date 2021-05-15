package io.taskmanager.ui.graphical;
import io.taskmanager.test.Column;
import io.taskmanager.test.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class ColumnViewer {

    private static final String FXML_FILE = "ColumnViewer.fxml";

    public static ColumnViewer loadNew(Column column) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.load();
        ColumnViewer columnViewer = fxmlLoader.getController();
        columnViewer.setColumn(column);
        return columnViewer;
    }
    public static ColumnViewer loadNew() throws IOException {
        return loadNew(null);
    }

    @FXML
    public Label ColumnTitleLabel;
    @FXML
    public VBox TaskVBox;
    @FXML
    public ScrollPane scrollPane;

    private Column column;

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column newColumn) throws IOException {
        if( newColumn == null){
            this.column = new Column();
        }else {
            this.column = newColumn;
        }
        ColumnTitleLabel.setText(column.getName());
        //TODO add task preview to TaskVBox
        for (Task task: column.getTasks()) {
            addTaskToTaskVBox(task);
        }
    }

    private void addTask(Task task) throws IOException {
        column.addTask(task);
        addTaskToTaskVBox(task);
    }

    private void addTaskToTaskVBox(Task task) throws IOException {
        TaskVBox.getChildren().add( ColumnTaskViewer.loadNew(task) );
    }

    public void OnAddTask(ActionEvent actionEvent) throws IOException {
        TaskDialog dialog = new TaskDialog();
        Optional<Task> res = dialog.showAndWait();
        if(res.isPresent()){
            addTask(res.get());
        }
    }


}
