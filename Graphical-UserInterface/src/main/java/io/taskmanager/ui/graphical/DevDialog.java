package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import java.io.IOException;

public class DevDialog extends Dialog<Dev> {

    public DevDialog(Dev dev) throws IOException {
        DevController controller = DevController.loadNew(dev);
        this.setDialogPane(controller.getDialogPane());

        setResultConverter(buttonType -> {
            System.out.println(buttonType);
            if( buttonType == ButtonType.APPLY){
                return controller.getDev();
            }else if( buttonType.getButtonData() == ButtonBar.ButtonData.OTHER){
                return null;
            }else{
                return null;
            }
        });
    }

    public DevDialog() throws IOException {
        this(null);
    }

}
