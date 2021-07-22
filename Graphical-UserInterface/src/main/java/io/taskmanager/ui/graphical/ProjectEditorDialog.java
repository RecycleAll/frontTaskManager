package io.taskmanager.ui.graphical;

import io.taskmanager.core.Project;
import io.taskmanager.core.repository.RepositoryManager;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class ProjectEditorDialog extends Dialog<Project> {

    private boolean shouldBeDelete;

    public ProjectEditorDialog(RepositoryManager repositoryManager, Project project, boolean deletable) throws IOException {
        shouldBeDelete = false;
        ProjectEditorController controller = new ProjectEditorController(repositoryManager, project, deletable);
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                Project res = controller.getProject();
                if( res == null)
                    shouldBeDelete = true;
                return res;
            }else if( buttonType.getButtonData() == ButtonBar.ButtonData.OTHER){
                shouldBeDelete = true;
                return null;
            }else{
                return null;
            }
        });
    }

    public ProjectEditorDialog(RepositoryManager repositoryManager,Project project) throws IOException {
        this(repositoryManager, project, false);
    }

    public ProjectEditorDialog(RepositoryManager repositoryManager) throws IOException {
        this(repositoryManager, null);
    }

    public boolean isShouldBeDelete() {
        return shouldBeDelete;
    }
}
