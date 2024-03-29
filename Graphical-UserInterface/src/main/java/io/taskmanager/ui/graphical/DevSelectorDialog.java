package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DevSelectorDialog extends Dialog<List<Dev>> {

    public DevSelectorDialog(List<Dev> selectableDevs, List<Dev> selectedDevs) throws IOException {
        DevSelectorController controller = new DevSelectorController(selectableDevs, new ArrayList<>(selectedDevs));
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.APPLY) {
                return controller.getDevs();
            } else {
                return null;
            }
        });
    }

}
