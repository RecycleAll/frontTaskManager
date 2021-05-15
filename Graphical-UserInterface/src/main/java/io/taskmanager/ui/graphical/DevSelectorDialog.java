package io.taskmanager.ui.graphical;

import io.taskmanager.test.Project;
import io.taskmanager.test.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DevSelectorDialog extends Dialog<ButtonType> {

    public DevSelectorDialog(@NotNull Project project, Task task) throws IOException {
        DevSelectorController controller = new DevSelectorController(project, task);
        this.setDialogPane( controller);
    }

}
