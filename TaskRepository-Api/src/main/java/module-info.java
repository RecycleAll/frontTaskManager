module TaskRepository.Api {
    requires Core;
    requires com.google.gson;
    requires java.net.http;
    exports io.taskmanager.api;
}