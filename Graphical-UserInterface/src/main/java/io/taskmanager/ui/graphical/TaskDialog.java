package io.taskmanager.ui.graphical;

import io.taskmanager.test.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class TaskDialog extends Dialog<Task> {

    public TaskDialog(Task task) throws IOException {
        TaskController controller = new TaskController(task);
        setDialogPane(controller);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getTask();
            }else{
                return null;
            }
        });
    }
    public TaskDialog() throws IOException {
        this(null);
    }
}
