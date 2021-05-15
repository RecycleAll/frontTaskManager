package io.taskmanager.ui.graphical;

import io.taskmanager.test.Column;
import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import io.taskmanager.test.Task;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( "ColumnViewer.fxml"));

        Project project = new Project(0, "project test", "gitURL");

        Dev dev = new Dev(0, "dev1", "pata", "", "", 0);
        Dev dev2 = new Dev(0, "dev2", "pata", "", "", 0);

        Task task = new Task(0, "task1", "", LocalDateTime.now());
        task.addDev(dev);
        task.addDev(dev2);

        Column column = new Column(0, "column 1");
        Column column2 = new Column(0, "column 2");
        column.addTask(task);

        project.addColumn(column);
        project.addColumn(column2);
        project.addDev(dev);
        project.addDev(dev2);

        scene = new Scene( ProjectController.loadNew(project).borderPane );
        //scene = new Scene( ColumnViewer.loadNew(column).scrollPane );

        stage.setScene(scene);
        stage.show();

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}