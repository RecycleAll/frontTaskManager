module Graphical.UserInterface {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires Core;

    opens io.taskmanager.ui.graphical to javafx.fxml;
    exports io.taskmanager.ui.graphical;
}
