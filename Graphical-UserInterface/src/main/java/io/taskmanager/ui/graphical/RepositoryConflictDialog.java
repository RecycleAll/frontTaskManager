package io.taskmanager.ui.graphical;

import io.taskmanager.test.ApiRequest;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class RepositoryConflictDialog <T extends ApiRequest<?>> extends Dialog<T> {

    public RepositoryConflictDialog(RepositoryConflictController<T> controller) throws IOException {
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            //System.out.println("RepositoryConflictDialog:Converter -> "+ controller.getMerged().toString());
            return controller.getMerged();
        });
    }



}
