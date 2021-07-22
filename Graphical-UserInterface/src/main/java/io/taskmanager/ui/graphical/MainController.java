package io.taskmanager.ui.graphical;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController extends VBox {

    private static final String FXML_FILE = "mainController.fxml";

    @FXML
    public BorderPane contentPane;
    @FXML
    public MenuBar menuBar;

    public MainController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
    }


}
