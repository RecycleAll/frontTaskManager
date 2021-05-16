package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class PasswordChangeDialog extends Dialog<Void> {

    public PasswordChangeDialog(Dev dev) throws IOException {
        PasswordController controller = new PasswordController(dev);
        this.setDialogPane(controller);
    }

}
