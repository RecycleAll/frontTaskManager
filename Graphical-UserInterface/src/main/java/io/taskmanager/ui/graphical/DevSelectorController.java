package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.Project;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.io.IOException;
import java.util.ArrayList;

public class DevSelectorController extends DialogPane {

    private static final String FXML_FILE = "DevSelectorController.fxml";

    @FXML
    public TableView<DevTableItem> table;
    @FXML
    public TableColumn<DevTableItem, String> firstNameColumn;
    @FXML
    public TableColumn<DevTableItem, String> lastNameColumn;
    @FXML
    public TableColumn<DevTableItem, Boolean> presentColumn;

    private final ArrayList<Dev> devs;
    private Project project;

    public DevSelectorController(Project project, ArrayList<Dev> devs) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.devs = devs;
        this.project = project;

        firstNameColumn.setCellValueFactory(devTableItemStringCellDataFeatures -> new SimpleStringProperty(devTableItemStringCellDataFeatures.getValue().dev.getFirstName()) );
        lastNameColumn.setCellValueFactory( devTableItemStringCellDataFeatures -> new SimpleStringProperty(devTableItemStringCellDataFeatures.getValue().dev.getLastName()) );
        presentColumn.setCellValueFactory(devTableItemBooleanCellDataFeatures -> devTableItemBooleanCellDataFeatures.getValue().isSelected );
        presentColumn.setCellFactory( CheckBoxTableCell.forTableColumn(presentColumn));

        for (Dev dev :project.getDevs() ) {
            table.getItems().add( new DevTableItem( dev, devs.contains(dev)));
        }
    }

    @FXML
    public void initialize() {
        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {

            for (DevTableItem devTableItem: table.getItems() ) {
                if( devTableItem.isSelected.get() && !devs.contains( devTableItem.dev)){
                    devs.add(devTableItem.dev);
                }else if(!devTableItem.isSelected.get()){
                    devs.remove(devTableItem.dev);
                }
            }
        });

        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01)));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
    }

    public ArrayList<Dev> getDevs(){
        return devs;
    }

    public static class DevTableItem{
        public Dev dev;
        public BooleanProperty isSelected;

        public DevTableItem(Dev dev, boolean isSelected) {
            this.dev = dev;
            this.isSelected = new SimpleBooleanProperty(isSelected);
        }
    }

}
