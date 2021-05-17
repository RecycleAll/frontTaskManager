package io.taskmanager.ui.graphical;

import io.taskmanager.api.TaskRepositoryApi;
import io.taskmanager.test.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException, ExecutionException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( "ProjectColumnController.fxml"));

        TaskRepositoryApi api = new TaskRepositoryApi("http://localhost:3000");
       // Project p = Project.loadFromApi(api, 1);
      //  System.out.println("project: "+p);


        Project project = new Project(api, 0, "project test", "gitURL");

        Dev dev = new Dev(0, "dev1", "pata", "", "", 0);
        Dev dev2 = new Dev(0, "dev2", "pata", "", "", 0);

        Task task = new Task(0, "task1", "", LocalDate.now());
        task.addDev(dev);
        task.addDev(dev2);

        Column column = new Column(0, "column 1");
        Column column2 = new Column(0, "column 2");
        column.addTask(task);

        project.addColumn(column);
        project.addColumn(column2);
        project.addDev(dev);
        project.addDev(dev2);

        dev.addProject(project);

        scene = new Scene( new DevViewerController(dev) );
        //scene = new Scene( new ProjectController(project) );
        //scene = new Scene( ProjectColumnController.loadNew(column).scrollPane );

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