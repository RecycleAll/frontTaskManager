package io.taskmanager.ui.graphical;

import io.taskmanager.test.ApiRequest;
import io.taskmanager.test.Dev;
import io.taskmanager.test.RepositoryConflictHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class RepositoryConflictController <T extends ApiRequest<?>> extends DialogPane{

    private static final String FXML_FILE = "RepoConflictController.fxml";

    @FXML
    public Pane localPane, mergedPane, repoPane;

    T mergedObject;
    IObjectEditor<T> objectEditor;

    public RepositoryConflictController(RepositoryConflictHandler<T> conflictHandler) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();

        if(conflictHandler.getRepo() instanceof Dev repoDev){
            //localPane.add( new DevEditorGrid((Dev) conflictHandler.getRepo()), 0, 1);
            Dev localDev = (Dev) conflictHandler.getLocal();
            mergedObject = (T) localDev.merge(repoDev);

            localPane.getChildren().add( new DevEditorGrid( localDev , false));
            repoPane.getChildren().add( new DevEditorGrid( repoDev, false));
            DevEditorGrid mergedEditor = new DevEditorGrid( (Dev)mergedObject);
            objectEditor = (IObjectEditor<T>) mergedEditor;
            mergedPane.getChildren().add( mergedEditor );

        }
    }

    public T getMerged(){
        return objectEditor.getEditedObject();
    }

}
