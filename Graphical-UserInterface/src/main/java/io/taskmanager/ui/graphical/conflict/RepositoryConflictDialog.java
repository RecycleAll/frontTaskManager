package io.taskmanager.ui.graphical.conflict;

import io.taskmanager.core.RepositoryObject;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class RepositoryConflictDialog <T extends RepositoryObject<?>> extends Dialog<T> {

    public RepositoryConflictDialog(RepositoryConflictController<T> controller) throws IOException {
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            //System.out.println("RepositoryConflictDialog:Converter -> "+ controller.getMerged().toString());
            return controller.getMerged();
        });
    }



}
