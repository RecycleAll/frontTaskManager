package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class ProjectEditorDialog extends Dialog<Project> {

    private boolean shouldBeDelete;

    public ProjectEditorDialog(Project project, boolean deletable) throws IOException {
        shouldBeDelete = false;
        ProjectEditorController controller = new ProjectEditorController(project, deletable);
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getProject();
            }else if( buttonType.getButtonData() == ButtonBar.ButtonData.OTHER){
                shouldBeDelete = true;
                return null;
            }else{
                return null;
            }
        });
    }

    public ProjectEditorDialog(Project project) throws IOException {
        this(project, false);
    }
}
