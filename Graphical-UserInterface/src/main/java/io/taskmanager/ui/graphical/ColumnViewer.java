package io.taskmanager.ui.graphical;

import io.taskmanager.test.Column;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ColumnViewer {

    @FXML
    public Label ColumnTitleLabel;
    @FXML
    public VBox TaskVBox;

    private Column column;

    public ColumnViewer(){}

    public ColumnViewer(Column column) {
        setColumn(column);
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
        ColumnTitleLabel.setText(column.getName());
        //TODO add task preview to TaskVBox
    }

    public void OnAddTask(ActionEvent actionEvent) {
    }


}
