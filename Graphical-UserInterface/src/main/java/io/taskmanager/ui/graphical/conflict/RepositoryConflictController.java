package io.taskmanager.ui.graphical.conflict;

import io.taskmanager.core.RepositoryObject;
import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.ui.graphical.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class RepositoryConflictController<T extends RepositoryObject<?>> extends DialogPane {

    private static final String FXML_FILE = "RepoConflictController.fxml";

    @FXML
    public Pane localPane, mergedPane, repoPane;
    RepositoryConflictHandler<T> conflictHandler;
    T mergedObject;

    IObjectEditor<T> objectEditor;

    public RepositoryConflictController(RepositoryConflictHandler<T> conflictHandler) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.conflictHandler = conflictHandler;
    }

    @FXML
    @SuppressWarnings("unused") // used by FXML
    public void initialize() {
        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {

            if (!objectEditor.validateChange()) {
                actionEvent.consume();
            }

        });
    }

    public T getMerged() {
        return objectEditor.getEditedObject();
    }

}
