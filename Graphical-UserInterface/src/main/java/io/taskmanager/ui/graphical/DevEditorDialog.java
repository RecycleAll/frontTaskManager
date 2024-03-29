package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class DevEditorDialog extends Dialog<Dev> {

    private boolean shouldBeDelete;

    public DevEditorDialog(Dev dev, boolean deletable, boolean register) throws IOException {
        shouldBeDelete = false;
        DevEditorController controller = new DevEditorController(dev, deletable, register);
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.APPLY) {
                return controller.getDev();
            } else if (buttonType.getButtonData() == ButtonBar.ButtonData.OTHER) {
                shouldBeDelete = true;
                return null;
            } else {
                return null;
            }
        });
    }

    public DevEditorDialog(Dev dev, boolean deletable) throws IOException {
        this(dev, deletable, false);
    }

    public DevEditorDialog(Dev dev) throws IOException {
        this(dev, false);
    }

    public boolean isShouldBeDelete() {
        return shouldBeDelete;
    }

    public DevEditorDialog() throws IOException {
        this(null);
    }

}
