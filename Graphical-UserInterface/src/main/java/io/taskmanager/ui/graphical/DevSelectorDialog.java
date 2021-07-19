package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import io.taskmanager.core.Project;
import io.taskmanager.core.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DevSelectorDialog extends Dialog<List<Dev>> {

    public DevSelectorDialog(@NotNull Project project, List<Dev> devs) throws IOException {
        DevSelectorController controller = new DevSelectorController( project.getDevs(), new ArrayList<>( devs));
        this.setDialogPane( controller);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getDevs();
            }else{
                return null;
            }
        });
    }

}
