module Graphical.UserInterface {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires Core;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires TaskRepository.Api;

    opens io.taskmanager.ui.graphical to javafx.fxml;
    exports io.taskmanager.ui.graphical;
}
