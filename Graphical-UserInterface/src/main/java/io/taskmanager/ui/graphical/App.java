package io.taskmanager.ui.graphical;

import io.taskmanager.core.*;
import io.taskmanager.core.repository.RepositoryManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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

    private final Pane contentPane;
    private final VBox mainPane;
    private final MenuBar menuBar;

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

        menuBar = new MenuBar();
        Menu menu = new Menu("Plugins");
        MenuItem item = new Menu("add plugin");

        item.setOnAction(actionEvent -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("select a jar file");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR files (*.jar)", "*.jar"));

            File file = chooser.showOpenDialog(stage);


        });

        menu.getItems().add(item);
        menuBar.getMenus().add( menu);

        mainPane = new VBox();
        mainPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        mainPane.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        mainPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        contentPane = new Pane();
        contentPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        contentPane.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        contentPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);


        mainPane.getChildren().add(menuBar);
        mainPane.getChildren().add(contentPane);

        loginController = new LoginController(repository, this);

        this.repository = repository;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void setDevViewerScene(Dev dev) throws IOException {
        devViewerController.setDev(dev);
        contentPane.getChildren().clear();
        contentPane.getChildren().add(0, devViewerController);
        pack();
    }

    public void setLoginScene(){
        loginController.reset();
        contentPane.getChildren().clear();
        contentPane.getChildren().add(0, loginController);
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
        this.stage.setScene( new Scene(mainPane));
        setLoginScene();
        stage.show();
        pack();
    }

}