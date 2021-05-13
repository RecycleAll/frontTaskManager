package io.taskmanager.ui.graphical;

import io.taskmanager.test.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class TaskEditorDialog extends Dialog<Task> {

    public TaskEditorDialog(Task task) throws IOException {
        TaskEditor controller = TaskEditor.loadNew(task);
        setDialogPane(controller.dialogPane);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getTask();
            }else{
                return null;
            }
        });
    }
    public TaskEditorDialog() throws IOException {
        this(null);
    }
}
