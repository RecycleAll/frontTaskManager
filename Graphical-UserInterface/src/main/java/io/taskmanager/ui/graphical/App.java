package io.taskmanager.ui.graphical;

import io.taskmanager.test.Column;
import io.taskmanager.test.Dev;
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

        Dev dev = new Dev(0, "dev1", "", "", "", 0);
        Dev dev2 = new Dev(0, "dev2", "", "", "", 0);
        Task task = new Task(0, "task1", "", LocalDateTime.now());
        task.addDev(dev);
        task.addDev(dev2);
        Column column = new Column(0, "column 1");
        column.addTask(task);

        scene = new Scene( fxmlLoader.load() );
        ColumnViewer controller = fxmlLoader.getController();
        controller.setColumn(column);
        stage.setScene(scene);
        stage.show();

        System.out.println("test: "+ task.getDescription());
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