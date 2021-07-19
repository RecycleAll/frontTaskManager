package io.taskmanager.ui.graphical;

import io.taskmanager.core.*;
import io.taskmanager.core.repository.RepositoryManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static App AppInstance;

    private Stage stage;

    private final DevViewerController devViewerController;

    private final LoginController loginController;

    private final MainController mainController;

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


        Menu menu = new Menu("Plugins");
        MenuItem item = new Menu("add plugin");

        item.setOnAction(actionEvent -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("select a jar file");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR files (*.jar)", "*.jar"));

            File file = chooser.showOpenDialog(stage);

        });
        mainController = new MainController();

        menu.getItems().add(item);
        mainController.menuBar.getMenus().add( menu);

        loginController = new LoginController(repository, this);

        this.repository = repository;
    }

    public MenuBar getMenuBar() {
        return mainController.menuBar;
    }

    public void setDevViewerScene(Dev dev) throws IOException {
        devViewerController.setDev(dev);
        mainController.contentPane.getChildren().clear();
        mainController.contentPane.setCenter( devViewerController);
        pack();
    }

    public void setLoginScene(){
        loginController.reset();
        mainController.contentPane.getChildren().clear();
        mainController.contentPane.setCenter(loginController);
        pack();
    }

    private void pack(){
        Scene scene = stage.getScene();
        stage.sizeToScene();
       // stage.setMinWidth( stage.getWidth());
       // stage.setMinHeight( stage.getHeight());
    }

    @Override
    public void start(Stage stage) throws ExecutionException, InterruptedException {
        this.stage = stage;
        this.stage.setScene( new Scene(mainController));
        setLoginScene();

        stage.show();
        pack();
    }

}