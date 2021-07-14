package io.taskmanager.ui.graphical;

import io.taskmanager.test.Column;
import io.taskmanager.test.RepositoryManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ColumnEditor extends DialogPane{

    private static final String FXML_FILE = "ColumnEditor.fxml";

    private final RepositoryManager repository;

    @FXML
    public TextField nameField;

    private Column column;

    private final SimpleBooleanProperty isNewColumn = new SimpleBooleanProperty(false);

    public ColumnEditor(RepositoryManager repository, Column column) throws IOException, ExecutionException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.repository = repository;
        setColumn(column);
    }

    @FXML
    public void initialize() {
        ButtonType removeButtonType = new ButtonType("delete", ButtonBar.ButtonData.OTHER);
        this.getButtonTypes().add(removeButtonType);
        Button removeButton = (Button) this.lookupButton(removeButtonType);
        removeButton.visibleProperty().bind(Bindings.createBooleanBinding(() -> !isNewColumn.get(), isNewColumn));

        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if( nameField.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "The column name can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }else{
                try {
                    column.setName( nameField.getText());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setColumn(Column column) throws ExecutionException, InterruptedException {
        if( column == null){
            this.column = new Column(repository);
            isNewColumn.set(true);
        }else{
            this.column = column;
        }
        nameField.setText( this.column.getName());
    }

    public Column getColumn() {
        return column;
    }
}
