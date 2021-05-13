package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import java.io.IOException;

public class DevDialog extends Dialog<Dev> {

    public DevDialog(Dev dev) throws IOException {
        DevController controller = DevController.loadNew(dev);
        this.setDialogPane(controller.getDialogPane());

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getDev();
            }else{
                return null;
            }
        });
    }

    public DevDialog() throws IOException {
        this(null);
    }

}
