package io.taskmanager.ui.graphical;

import io.taskmanager.test.Column;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ColumnEditorDialog extends Dialog<Column> {

    private boolean shouldBeDelete;

    public ColumnEditorDialog(Column column) throws IOException, ExecutionException, InterruptedException {
        shouldBeDelete = false;
        ColumnEditor controller = new ColumnEditor(column.getRepository(), column);
        this.setDialogPane( controller);

        setResultConverter(buttonType -> {
            if( buttonType == ButtonType.APPLY){
                return controller.getColumn();
            }else if( buttonType.getButtonData() == ButtonBar.ButtonData.OTHER){
                shouldBeDelete = true;
                return null;
            }else{
                return null;
            }
        });
    }
    public ColumnEditorDialog() throws IOException, ExecutionException, InterruptedException {
        this(null);
    }

    public boolean isShouldBeDelete() {
        return shouldBeDelete;
    }
}
