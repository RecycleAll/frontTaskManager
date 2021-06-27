package io.taskmanager.ui.graphical;

import io.taskmanager.test.Project;
import io.taskmanager.test.Task;
import io.taskmanager.test.TaskRepository;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class TaskDialog extends Dialog<Task> {

    private boolean shouldBeDelete;

    public TaskDialog(TaskRepository repository, Project project, Task task) throws IOException {
        shouldBeDelete = false;
        TaskController controller = new TaskController(repository, project, task);
        setDialogPane(controller);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getTask();
            }else if( buttonType.getButtonData() == ButtonBar.ButtonData.OTHER){
                shouldBeDelete = true;
                return null;
            }else{
                return null;
            }
        });
    }
    public TaskDialog(TaskRepository repository, Project project) throws IOException {
        this(repository, project,null);
    }

    public boolean isShouldBeDelete() {
        return shouldBeDelete;
    }
}
