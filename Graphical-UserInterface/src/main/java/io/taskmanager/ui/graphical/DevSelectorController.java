package io.taskmanager.ui.graphical;

import io.taskmanager.core.Dev;
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
import java.util.List;

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

    private final List<Dev> devs;

    public DevSelectorController(List<Dev> selectableDev, List<Dev> alreadySelectedDev) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_FILE));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        this.devs = alreadySelectedDev;

        firstNameColumn.setCellValueFactory(devTableItemStringCellDataFeatures -> new SimpleStringProperty(devTableItemStringCellDataFeatures.getValue().dev.getFirstname()));
        lastNameColumn.setCellValueFactory(devTableItemStringCellDataFeatures -> new SimpleStringProperty(devTableItemStringCellDataFeatures.getValue().dev.getLastname()));
        presentColumn.setCellValueFactory(devTableItemBooleanCellDataFeatures -> devTableItemBooleanCellDataFeatures.getValue().isSelected);
        presentColumn.setCellFactory(CheckBoxTableCell.forTableColumn(presentColumn));

        for (Dev dev : selectableDev) {
            table.getItems().add(new DevTableItem(dev, alreadySelectedDev.contains(dev)));
        }
    }

    @FXML
    @SuppressWarnings("unused") // used by FXML
    public void initialize() {

        Button applyButton = (Button) this.lookupButton(ButtonType.APPLY);
        applyButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {

            for (DevTableItem devTableItem : table.getItems()) {
                if (devTableItem.isSelected.get() && !devs.contains(devTableItem.dev)) {
                    devs.add(devTableItem.dev);
                } else if (!devTableItem.isSelected.get()) {
                    devs.remove(devTableItem.dev);
                }
            }
        });

        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.01)));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
    }

    public List<Dev> getDevs() {
        return devs;
    }

    public static class DevTableItem {
        public Dev dev;
        public BooleanProperty isSelected;

        public DevTableItem(Dev dev, boolean isSelected) {
            this.dev = dev;
            this.isSelected = new SimpleBooleanProperty(isSelected);
        }
    }

}
