package io.taskmanager.ui.graphical;

import io.taskmanager.test.ApiRequest;
import io.taskmanager.test.RepositoryConflictHandler;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class RepositoryConflictDialog <T extends ApiRequest<?>> extends Dialog<T> {

    public RepositoryConflictDialog(RepositoryConflictHandler<T> conflictHandler) throws IOException {

        RepositoryConflictController<T> controller = new RepositoryConflictController<T>(conflictHandler);
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            System.out.println("RepositoryConflictDialog:Converter -> "+ controller.getMerged().toString());
            return controller.getMerged();
        });
    }

}
