module TaskRepository.Api {
    requires Core;
    requires com.google.gson;
    requires java.net.http;

    opens io.taskmanager.api to com.google.gson;

    exports io.taskmanager.api;
}