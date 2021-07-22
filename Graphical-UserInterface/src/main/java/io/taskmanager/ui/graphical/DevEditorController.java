package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryEditionConflict;
import io.taskmanager.core.repository.RepositoryObjectDeleted;
import io.taskmanager.ui.graphical.conflict.DevConflictController;
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

public class DevEditorController extends DialogPane{

    private static final String FXML_FILE = "DevEditorController.fxml";

    private final DevEditorGrid grid;

    private final SimpleBooleanProperty isDeletable = new SimpleBooleanProperty(false);

    public DevEditorController(Dev dev, boolean deletable, boolean register) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        grid = new DevEditorGrid(dev, true, register);
        isDeletable.set(deletable);
        this.setContent(grid);
    }
    public DevEditorController(Dev dev, boolean deletable) throws IOException {
        this(dev, false, false);
    }
    public DevEditorController(Dev dev) throws IOException {
        this(dev, false);
    }

    @FXML
    @SuppressWarnings("unused") //used by fxml loader
    public void initialize() {
        ButtonType removeButtonType = new ButtonType("delete", ButtonBar.ButtonData.OTHER);
        this.getButtonTypes().add(removeButtonType);
        Button removeButton = (Button) this.lookupButton(removeButtonType);
        removeButton.visibleProperty().bind(Bindings.createBooleanBinding(isDeletable::get, isDeletable));

        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {

           if( !grid.validateChange()){
               actionEvent.consume();
           }else{
               Dev dev = grid.getEditedObject();

               try {
                    dev.updateToRepo();
               } catch (ExecutionException | InterruptedException e) {
                   e.printStackTrace();
               } catch (RepositoryEditionConflict repositoryEditionConflict) {
                   System.out.println("catch");
                   try {
                       RepositoryConflictDialog<Dev> dialog = new RepositoryConflictDialog<Dev>( new DevConflictController( (RepositoryConflictHandler<Dev>) repositoryEditionConflict.getConflictHandler()) );

                       Optional<Dev> res = dialog.showAndWait();
                       if (res.isPresent()) {
                           dev.setAll(res.get());
                           dev.updateToRepo(true);
                       }
                   } catch (IOException | RepositoryEditionConflict | ExecutionException | InterruptedException | RepositoryObjectDeleted e) {
                       e.printStackTrace();
                   }
               } catch (RepositoryObjectDeleted repositoryObjectDeleted) {
                   repositoryObjectDeleted.printStackTrace();
               }


           }
        });
    }

    public void setDev(Dev dev){
        grid.setDev(dev);
    }

    public Dev getDev() {
        return grid.getEditedObject();
    }


}
