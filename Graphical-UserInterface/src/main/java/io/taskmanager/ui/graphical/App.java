package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.ui.graphical.plugin.PluginInterface;
import io.taskmanager.ui.graphical.plugin.PluginLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private final List<PluginInterface> plugins;

    public static void launchApp(RepositoryManager repository) throws Exception {
        Platform.startup(() -> {});

        if (AppInstance == null) {
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

    public Dev getConnectedDev(){
        return devViewerController.getDev();
    }

    public App(RepositoryManager repository) throws Exception {
        super();
        plugins = new ArrayList<>();
        devViewerController = new DevViewerController(repository);


        Menu pluginMenu = new Menu("Plugins");
        MenuItem addPluginMenu = new Menu("add plugin");
        Menu usePlugin = new Menu("use plugin");

        addPluginMenu.setOnAction(actionEvent -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("select a jar file");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR files (*.jar)", "*.jar"));

            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                PluginLoader pluginLoader = new PluginLoader();
                Optional<PluginInterface> pluginInterface = pluginLoader.loadPlugin(file);
                if (pluginInterface.isPresent()) {
                    //plugins.add(pluginInterface.get());
                    MenuItem newPlugin = new Menu(file.getName());
                    usePlugin.getItems().add(newPlugin);
                    newPlugin.setOnAction(actionEvent1 -> {
                        pluginInterface.get().startPlugin(this);
                    });
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "plugin isn't valid", ButtonType.OK);
                    alert.showAndWait();
                }
            }

        });
        mainController = new MainController();

        pluginMenu.getItems().add(addPluginMenu);
        pluginMenu.getItems().add(usePlugin);
        mainController.menuBar.getMenus().add( pluginMenu);

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

    public void setLoginScene() {
        loginController.reset();
        mainController.contentPane.getChildren().clear();
        mainController.contentPane.setCenter(loginController);
        pack();
    }

    private void pack() {
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