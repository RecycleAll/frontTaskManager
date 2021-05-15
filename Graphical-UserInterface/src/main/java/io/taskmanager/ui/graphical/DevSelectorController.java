package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import io.taskmanager.test.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class DevSelectorController extends DialogPane {

    private static final String FXML_FILE = "DevSelectorController.fxml";

    @FXML
    public TableView<Dev> table;
    @FXML
    public TableColumn<Dev, String> firstNameColumn;
    @FXML
    public TableColumn<Dev, String> lastNameColumn;
    @FXML
    public TableColumn<Dev, Boolean> presentColumn;

    private Task task;
    private Project project;

    public DevSelectorController(Project project, Task task) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.task = task;
        this.project = project;

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        for (Dev dev:project.getDevs() ) {
            table.getItems().add(dev);
        }
    }

}
