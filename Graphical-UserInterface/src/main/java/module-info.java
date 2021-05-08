module io.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;

    opens io.taskmanager to javafx.fxml;
    exports io.taskmanager;
}
