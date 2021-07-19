module Core {

    opens io.taskmanager.core to com.google.gson;

    exports io.taskmanager.core;
    exports io.taskmanager.core.repository;
    opens io.taskmanager.core.repository to com.google.gson;
}