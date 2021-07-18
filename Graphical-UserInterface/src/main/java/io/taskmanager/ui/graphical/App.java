package io.taskmanager.ui.graphical;

import io.taskmanager.test.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static App AppInstance;

    private Stage stage;

    private final DevViewerController devViewerController;
    private final Scene devViewerScene;

    private final LoginController loginController;
    private final Scene loginScene;

    private final RepositoryManager repository;

    public static void launchApp(RepositoryManager repository) throws Exception {
        Platform.startup(() -> {});

        if( AppInstance == null) {
            AppInstance = new App(repository);
            Platform.runLater(() -> {
                Stage stage = new Stage();

                stage.sceneProperty().addListener((observableValue, scene, newScene) -> {
                    stage.setMinWidth(newScene.getWidth());
                    stage.setMinHeight(newScene.getHeight());
                });
                try {
                    AppInstance.start(stage);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public App(RepositoryManager repository) throws Exception {
        super();
        devViewerController = new DevViewerController(repository);
        devViewerScene = new Scene( devViewerController);

        loginController = new LoginController(repository, this);
        loginScene = new Scene( loginController );


        this.repository = repository;
    }

    public void setDevViewerScene(Dev dev) throws IOException {
        devViewerController.setDev(dev);
        stage.setScene(devViewerScene);
        pack();
    }

    public void setLoginScene(){
        loginController.reset();
        stage.setScene(loginScene);
        pack();
    }

    private void pack(){
        Scene scene = stage.getScene();
       // stage.sizeToScene();
       // stage.setMinWidth( stage.getWidth());
       // stage.setMinHeight( stage.getHeight());
    }

    @Override
    public void start(Stage stage) throws ExecutionException, InterruptedException {
        this.stage = stage;

        setLoginScene();
        stage.show();
        pack();
    }

}