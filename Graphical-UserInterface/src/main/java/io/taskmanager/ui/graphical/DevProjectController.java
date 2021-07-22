package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import io.taskmanager.core.DevStatus;
import io.taskmanager.core.Project;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.repository.RepositoryObjectDeleted;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class DevProjectController extends AnchorPane {

    private static final String FXML_FILE = "DevProjectController.fxml";

    @FXML
    public Label projectNameLabel;

    private Project project;

    private final Dev loggedDev;

    private final RepositoryManager repositoryManager;
    private final DevViewerController viewerController;

    public DevProjectController(DevViewerController viewerController, @NotNull Project project, Dev loggedDev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setProject(project);
        this.viewerController = viewerController;
        this.repositoryManager = viewerController.getRepositoryManager();
        this.loggedDev = loggedDev;
    }

    public void setProject(Project project) {
        this.project = project;
        updateUI();
    }

    private void updateUI() {
        projectNameLabel.setText(project.getName());
    }

    public Project getProject() {
        return project;
    }

    @FXML
    @SuppressWarnings("unused") // used by FXML
    public void OnMouseClicked(MouseEvent mouseEvent) {
        System.out.println("test");
    }

    @FXML
    @SuppressWarnings("unused")
    public void OnEdit(ActionEvent actionEvent) throws IOException, ExecutionException, RepositoryObjectDeleted, InterruptedException {
        ProjectEditorDialog dialog = new ProjectEditorDialog(repositoryManager, project, project.getDevStatus(loggedDev) == DevStatus.OWNER);
        Optional<Project> res = dialog.showAndWait();
        if (res.isPresent()) {
            if (dialog.isShouldBeDelete()) {
                repositoryManager.removeProject(project);
                viewerController.removeProject(this);
            } else {
                updateUI();
            }
        } else if (dialog.isShouldBeDelete()) {
            repositoryManager.removeProject(project);
            viewerController.removeProject(this);
        }
    }

}
