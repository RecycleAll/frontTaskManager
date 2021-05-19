package io.taskmanager.ui.graphical;

import io.taskmanager.test.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private Stage stage;

    private final DevViewerController devViewerController;
    private final Scene devViewerScene;

    private final LoginController loginController;
    private final Scene loginScene;

    private final TaskRepository repository;

    public static void launchApp(TaskRepository repository) throws Exception {
        Platform.startup(() -> {});
        App app = new App(repository);
        Platform.runLater( () -> {
            Stage stage = new Stage();
            stage.setMaxWidth(999999);
            app.start(stage);
        });
    }

    public App(TaskRepository repository) throws Exception {
        super();
        devViewerController = new DevViewerController();
        devViewerScene = new Scene( devViewerController);
        loginController = new LoginController(repository, this);
        loginScene = new Scene( loginController );

        this.repository = repository;
    }

    public void setDevViewerScene(Dev dev) throws IOException {
        devViewerController.setDev(dev);
        stage.setScene(devViewerScene);
        stage.sizeToScene();
    }
    public void setLoginScene(){
        loginController.reset();
        stage.setScene(loginScene);
        stage.sizeToScene();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Project project = new Project(0, "project test", "gitURL");

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

        //scene = new Scene( new ProjectController(project) );
        //scene = new Scene( ProjectColumnController.loadNew(column).scrollPane );

        stage.setScene(loginScene);
        stage.show();
    }

}