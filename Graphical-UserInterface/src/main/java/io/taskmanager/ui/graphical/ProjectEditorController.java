package io.taskmanager.ui.graphical;

import io.taskmanager.core.Project;
import io.taskmanager.core.repository.RepositoryEditionConflict;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ProjectEditorController extends DialogPane {

    private static final String FXML_FILE = "ProjectEditor.fxml";
    @FXML
    public TextField nameField;
    @FXML
    public TextField gitHubUrlFiled;

    private Project project;
    private final SimpleBooleanProperty isDeletable = new SimpleBooleanProperty(false);

    public ProjectEditorController(Project project, boolean deletable) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setProject(project);
        isDeletable.set(deletable);
    }

    public ProjectEditorController(Project project) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setProject(project);
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml loader
    public void initialize() {

        ButtonType removeButtonType = new ButtonType("delete", ButtonBar.ButtonData.OTHER);
        this.getButtonTypes().add(removeButtonType);
        Button removeButton = (Button) this.lookupButton(removeButtonType);
        removeButton.visibleProperty().bind(Bindings.createBooleanBinding(isDeletable::get, isDeletable));

        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {

            if( nameField.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Project name can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }else{
                project.setName(nameField.getText());
                project.setGitHubUrl(gitHubUrlFiled.getText());
                try {
                    project.updateToRepo();
                } catch (ExecutionException | InterruptedException | RepositoryEditionConflict e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void setProject(Project project) {
        this.project = project;
        nameField.setText(project.getName());
        gitHubUrlFiled.setText(project.getGitHubUrl());
    }

    public Project getProject() {
        return project;
    }
}
