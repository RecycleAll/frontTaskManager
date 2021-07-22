package io.taskmanager.ui.graphical;

import io.taskmanager.core.Column;
import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryEditionConflict;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.repository.RepositoryObjectDeleted;
import io.taskmanager.ui.graphical.conflict.ColumnConflictController;
import io.taskmanager.ui.graphical.conflict.IObjectEditor;
import io.taskmanager.ui.graphical.conflict.RepositoryConflictDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ColumnEditor extends DialogPane implements IObjectEditor<Column> {

    private static final String FXML_FILE = "ColumnEditor.fxml";

    private final RepositoryManager repository;

    @FXML
    public TextField nameField;

    private Column column;

    private final SimpleBooleanProperty isNewColumn = new SimpleBooleanProperty(false);

    public ColumnEditor(RepositoryManager repository, Column column, boolean editable) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.repository = repository;
        setColumn(column);
        nameField.setEditable(editable);
    }

    public ColumnEditor(RepositoryManager repository, Column column) throws IOException{
        this(repository, column, true);
    }

    @FXML
    public void initialize() {
        ButtonType removeButtonType = new ButtonType("delete", ButtonBar.ButtonData.OTHER);
        this.getButtonTypes().add(removeButtonType);
        Button removeButton = (Button) this.lookupButton(removeButtonType);
        removeButton.visibleProperty().bind(Bindings.createBooleanBinding(() -> !isNewColumn.get(), isNewColumn));

        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {

            if(applyChange()){

                try {
                    column.updateToRepo();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } catch (RepositoryEditionConflict repositoryEditionConflict) {

                    try {
                        RepositoryConflictDialog<Column> conflictDialog = new RepositoryConflictDialog<Column>( new ColumnConflictController((RepositoryConflictHandler<Column>) repositoryEditionConflict.getConflictHandler()));
                        Optional<Column> res =conflictDialog.showAndWait();
                        if (res.isPresent()) {
                            column.setAll(res.get());
                            System.out.println("///////////////////////////////////////////////");
                            System.out.println("column:name -> "+column.getName());
                            column.updateToRepo(true);
                            System.out.println("///////////////////////////////////////////////");
                        }
                    } catch (IOException | InterruptedException | RepositoryObjectDeleted | ExecutionException | RepositoryEditionConflict e) {
                        e.printStackTrace();
                    }

                } catch (RepositoryObjectDeleted repositoryObjectDeleted) {

                    Column column = (Column) repositoryObjectDeleted.getObjects().get(0);
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The Column "+column.getName()+" has been deleted from the repo", ButtonType.OK);
                    alert.showAndWait();

                    try {
                        repository.removeColumn(this.column);
                        this.column = null;
                    } catch (ExecutionException | InterruptedException objectDeleted) {
                        objectDeleted.printStackTrace();
                    }

                }

            }
        });
    }

    public void setColumn(Column column)  {
        if( column == null){
            this.column = new Column(null);
            isNewColumn.set(true);
        }else{
            this.column = column;
        }
        nameField.setText( this.column.getName());
    }

    public Column getColumn() {
        return column;
    }

    @Override
    public boolean validateChange() {
        if( nameField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "The column name can't be empty", ButtonType.OK);
            alert.showAndWait();
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean applyChange(){
        if(validateChange()) {
            column.setName(nameField.getText());
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Column getEditedObject() {
        applyChange();
        return column;
    }
}
