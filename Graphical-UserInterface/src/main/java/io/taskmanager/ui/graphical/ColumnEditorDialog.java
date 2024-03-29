package io.taskmanager.ui.graphical;

import io.taskmanager.core.Column;
import io.taskmanager.core.repository.RepositoryManager;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ColumnEditorDialog extends Dialog<Column> {

    private boolean shouldBeDelete;

    public ColumnEditorDialog(RepositoryManager repository, Column column) throws IOException {
        shouldBeDelete = false;
        ColumnEditor controller = new ColumnEditor(repository, column);
        this.setDialogPane(controller);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.APPLY) {
                Column res = controller.getColumn();
                if (res == null)
                    shouldBeDelete = true;
                return controller.getColumn();
            } else if (buttonType.getButtonData() == ButtonBar.ButtonData.OTHER) {
                shouldBeDelete = true;
                return null;
            } else {
                return null;
            }
        });
    }

    public ColumnEditorDialog(RepositoryManager repository) throws IOException, ExecutionException, InterruptedException {
        this(repository, null);
    }

    public boolean isShouldBeDelete() {
        return shouldBeDelete;
    }
}
