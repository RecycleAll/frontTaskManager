package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class DevEditorController extends DialogPane{

    private static final String FXML_FILE = "DevEditorController.fxml";


    private DevEditorGrid grid;

    private final SimpleBooleanProperty isDeletable = new SimpleBooleanProperty(false);
    private Dev dev;

    public DevEditorController(Dev dev, boolean deletable) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        grid = new DevEditorGrid(dev);
        this.dev = dev;
        isDeletable.set(deletable);
        this.setContent(grid);
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
            if( dev == null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Internal error dev is null", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( grid.getFirstNameString().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your first name can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( grid.getLastNameString().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your last name can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( grid.getEmailString().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your email can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else if( grid.getGithubString().isEmpty() )
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Your github id can't be empty", ButtonType.OK);
                alert.showAndWait();
                actionEvent.consume();
            }
            else
            {
                try {
                    dev.setGithub_id( Integer.parseInt(grid.getGithubString()));
                    dev.setFirstname( grid.getFirstNameString());
                    dev.setLastname( grid.getLastNameString());
                    dev.setEmail( grid.getEmailString());
                    dev.updateToRepo();

                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "\""+grid.getGithubString()+"\" is not a number", ButtonType.OK);
                    alert.showAndWait();
                    actionEvent.consume();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void setDev(Dev dev){
        this.dev = dev;
        grid.setDev(dev);
    }

    public Dev getDev() {
        return dev;
    }


}
