package io.taskmanager.ui.graphical;

import io.taskmanager.test.Task;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class TaskDialog extends Dialog<Task> {

    private boolean shouldBeDelete;

    public TaskDialog(Task task) throws IOException {
        shouldBeDelete = false;
        TaskController controller = new TaskController(task);
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
    public TaskDialog() throws IOException {
        this(null);
    }

    public boolean isShouldBeDelete() {
        return shouldBeDelete;
    }
}
