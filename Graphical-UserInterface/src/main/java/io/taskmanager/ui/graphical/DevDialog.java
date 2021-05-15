package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import java.io.IOException;

public class DevDialog extends Dialog<Dev> {

    private boolean shouldBeDelete;

    public DevDialog(Dev dev) throws IOException {
        shouldBeDelete = false;
        DevController controller = DevController.loadNew(dev);
        this.setDialogPane(controller.getDialogPane());

        setResultConverter(buttonType -> {
            System.out.println(buttonType);
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

    public DevDialog() throws IOException {
        this(null);
    }

}
