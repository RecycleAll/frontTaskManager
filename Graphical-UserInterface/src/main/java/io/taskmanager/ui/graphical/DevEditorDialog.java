package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import java.io.IOException;

public class DevEditorDialog extends Dialog<Dev> {

    private boolean shouldBeDelete;

    public DevEditorDialog(Dev dev, boolean deletable) throws IOException {
        shouldBeDelete = false;
        DevEditorController controller = new DevEditorController(dev, deletable);
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getDev();
            }else if( buttonType.getButtonData() == ButtonBar.ButtonData.OTHER){
                shouldBeDelete = true;
                return null;
            }else{
                return null;
            }
        });
    }

    public DevEditorDialog(Dev dev) throws IOException {
        shouldBeDelete = false;
        DevEditorController controller = new DevEditorController(dev);
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getDev();
            }else if( buttonType.getButtonData() == ButtonBar.ButtonData.OTHER){
                shouldBeDelete = true;
                return null;
            }else{
                return null;
            }
        });
    }

    public boolean isShouldBeDelete() {
        return shouldBeDelete;
    }

    public DevEditorDialog() throws IOException {
        this(null);
    }

}
