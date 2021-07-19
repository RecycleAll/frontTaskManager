package io.taskmanager.ui.graphical;

import io.taskmanager.core.Project;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.Task;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class TaskDialog extends Dialog<Task> {

    private boolean shouldBeDelete;

    public TaskDialog(RepositoryManager repository, Project project, Task task) throws IOException {
        shouldBeDelete = false;
        TaskController controller = new TaskController(repository, project, task);
        setDialogPane(controller);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getEditedObject();
            }else if( buttonType.getButtonData() == ButtonBar.ButtonData.OTHER){
                shouldBeDelete = true;
                return null;
            }else{
                return null;
            }
        });
    }
    public TaskDialog(RepositoryManager repository, Project project) throws IOException {
        this(repository, project,null);
    }

    public boolean isShouldBeDelete() {
        return shouldBeDelete;
    }
}
