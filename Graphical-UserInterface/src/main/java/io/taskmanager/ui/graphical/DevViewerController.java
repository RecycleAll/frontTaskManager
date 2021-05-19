package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class DevViewerController extends TabPane {

    private static final String FXML_FILE = "DevViewerController.fxml";

    @FXML
    public Label firstNameLabel;
    @FXML
    public Label lastNameLabel;
    @FXML
    public VBox projectVBox;

    private Dev dev;

    public DevViewerController(Dev dev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setDev(dev);
    }

    public DevViewerController() throws IOException {
        this(null);
    }

    public void setDev(Dev dev) throws IOException {
        this.dev = dev;
        updateUI();
    }

    private void addProjectViewer(Project project) throws IOException {
        Optional<Tab> optionalTab = this.getTabs().stream().filter(tab -> {
            if(tab instanceof ProjectController myTab) {
                return project == myTab.getProject();
            }else{
                return false;
            }
        }).findFirst();

        if( optionalTab.isPresent()){
            this.getSelectionModel().select(optionalTab.get());
        }else{
            ProjectController projectControllerTab = new ProjectController(project);
            this.getTabs().add( projectControllerTab);
            this.getSelectionModel().select(projectControllerTab);
            this.getScene().getWindow().sizeToScene();
        }

    }

    private void updateUI() throws IOException {
        if( dev == null){
            firstNameLabel.setText("No Dev");
            lastNameLabel.setText("");

            projectVBox.getChildren().clear();
        }else {
            firstNameLabel.setText(dev.getFirstname());
            lastNameLabel.setText(dev.getLastname().toUpperCase(Locale.ROOT));

            projectVBox.getChildren().clear();
            for (Project project : dev.getProjects()) {
                DevProjectController devProjectController = new DevProjectController(project);
                devProjectController.setOnMouseClicked(mouseEvent -> {
                    try {
                        addProjectViewer(project);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                projectVBox.getChildren().add(devProjectController);
            }
        }
    }

    @FXML
    public void OnEdit(ActionEvent actionEvent){

    }
}

