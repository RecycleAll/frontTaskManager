package io.taskmanager.ui.graphical;

import io.taskmanager.test.Column;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

public class ColumnEditor extends DialogPane{

    private static final String FXML_FILE = "ColumnEditor.fxml";

    @FXML
    public TextField nameField;

    private Column column;

    private final SimpleBooleanProperty isNewDev = new SimpleBooleanProperty(false);

    public ColumnEditor(Column column) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        setColumn(column);
    }

    @FXML
    public void initialize() {
        ButtonType removeButtonType = new ButtonType("delete", ButtonBar.ButtonData.OTHER);
        this.getButtonTypes().add(removeButtonType);
        Button removeButton = (Button) this.lookupButton(removeButtonType);
        removeButton.visibleProperty().bind(Bindings.createBooleanBinding(() -> !isNewDev.get(), isNewDev));

        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if( nameField.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "The column name can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }else{
                column.setName( nameField.getText());
            }
        });
    }

    public void setColumn(Column column) {
        if( column == null){
            this.column = new Column();
            isNewDev.set(true);
        }else{
            this.column = column;
        }
        nameField.setText( this.column.getName());
    }

    public Column getColumn() {
        return column;
    }
}
