module TaskRepository.Api {
    requires Core;
    requires com.google.gson;
    requires java.net.http;

    opens io.taskmanager.api to com.google.gson;

    exports io.taskmanager.api;
    exports io.taskmanager.api.model;
    opens io.taskmanager.api.model to com.google.gson;
}