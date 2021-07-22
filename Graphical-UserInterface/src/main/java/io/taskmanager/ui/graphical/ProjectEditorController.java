package io.taskmanager.ui.graphical;

import io.taskmanager.core.Project;
import io.taskmanager.core.Task;
import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryEditionConflict;
import io.taskmanager.core.repository.RepositoryObjectDeleted;
import io.taskmanager.ui.graphical.conflict.IObjectEditor;
import io.taskmanager.ui.graphical.conflict.ProjectConflictController;
import io.taskmanager.ui.graphical.conflict.RepositoryConflictDialog;
import io.taskmanager.ui.graphical.conflict.TaskConflictController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ProjectEditorController extends DialogPane implements IObjectEditor<Project> {

    private static final String FXML_FILE = "ProjectEditor.fxml";
    @FXML
    public TextField nameField;
    @FXML
    public TextField gitHubUrlFiled;

    private Project project;
    private final SimpleBooleanProperty isDeletable = new SimpleBooleanProperty(false);

    public ProjectEditorController(Project project, boolean deletable, boolean editable) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setProject(project);
        isDeletable.set(deletable);
        nameField.setEditable(editable);
        gitHubUrlFiled.setEditable(editable);
    }

    public ProjectEditorController(Project project, boolean deletable) throws IOException {
        this(project, deletable, true);
    }

    public ProjectEditorController(Project project) throws IOException {
        this(project, false, true);
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

            if( applyChange() ){
                try {
                    project.updateToRepo();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } catch( RepositoryEditionConflict repositoryEditionConflict) {
                    try {
                        RepositoryConflictDialog<Project> dialog = new RepositoryConflictDialog<Project>(new ProjectConflictController((RepositoryConflictHandler<Project>) repositoryEditionConflict.getConflictHandler()));
                        Optional<Project> res = dialog.showAndWait();
                        if (res.isPresent()) {
                            project.setAll(res.get());
                            System.out.println("///////////////////////////////////////////////");
                            System.out.println("project:name -> " + project.getName());
                            project.updateToRepo(true);
                            System.out.println("project:name -> " + project.getName());
                            System.out.println("///////////////////////////////////////////////");
                        }
                    } catch (ExecutionException | InterruptedException | RepositoryEditionConflict | RepositoryObjectDeleted | IOException e) {
                        e.printStackTrace();
                    }
                }catch(RepositoryObjectDeleted repositoryObjectDeleted){
                    repositoryObjectDeleted.printStackTrace();
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

    @Override
    public boolean validateChange() {
        if( nameField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Project name can't be empty", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @Override
    public boolean applyChange() {
        if(!validateChange()){
            return false;
        }

        project.setName(nameField.getText());
        project.setGitHubUrl(gitHubUrlFiled.getText());

        return true;
    }

    @Override
    public Project getEditedObject() {
        applyChange();
        return project;
    }
}
