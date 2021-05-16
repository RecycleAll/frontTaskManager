package io.taskmanager.api;

import com.google.gson.*;
import io.taskmanager.test.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TaskRepositoryApi implements TaskRepository {

    private final static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private final HttpClient httpClient;
    private final Gson g;
    private final String apiUrl;

    public TaskRepositoryApi(String apiUrl) {
        httpClient = HttpClient.newHttpClient();
        g = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
            }
        }).registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), format);
            }
        }).create();
        this.apiUrl = apiUrl;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        asynchronousRequest();
    }

    private static void asynchronousRequest() throws ExecutionException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/task/one/1"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        var d = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        System.out.println("test: "+d.get());

        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                System.out.println("LocalDate: "+ json.getAsJsonPrimitive().getAsString() );
                System.out.println("LocalDate: "+ LocalDate.parse(json.getAsJsonPrimitive().getAsString()) );
                return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
            }
        }).registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                //System.out.println("test");
                System.out.println("LocalDateTime: "+ json.getAsJsonPrimitive().getAsString() );
                //System.out.println("LocalDateTime: "+ LocalDateTime.parse(json.getAsJsonPrimitive().getAsString()) );
                return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), format);
                //return null;
            }
        }).create();
      //  Tag s = g.fromJson(d.get(), Tag.class);

       // Project s = g.fromJson(d.get(), Project.class);
      /*  Task[] s = g.fromJson(d.get(), Task[].class);
        System.out.println( s[0].getName());
        System.out.println( s[0].getId() );
        System.out.println( s[1].getName());
        System.out.println( s[1].getId() );*/

        Task s = gson.fromJson(d.get(), Task.class);
        System.out.println( s.getName());
        System.out.println( s.getId() );
        System.out.println( s.getLimitDate() );
        System.out.println( s.getCreatedAt() );
        System.out.println( s.getLastUpdateDate() );
    }

    @Override
    public List<Project> getProjects(Dev dev) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/project/all/" + dev.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Project[] projectArray = g.fromJson(projectsAsJson.get(), Project[].class);
        return Arrays.asList(projectArray);
    }

    @Override
    public Project getProject(Project project) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/project/one/" + project.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        return g.fromJson(projectsAsJson.get(), Project.class);
    }

    @Override
    public Project getProject(int projectID) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/project/one/" + projectID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Project project = g.fromJson(projectsAsJson.get(), Project.class);
        project.setApi(this);

        project.setDevs( getProjectDevs(projectID));
        System.out.println("test: "+project.getDevs().size());

        project.setColumns( getColumns(projectID));

        return project;
    }
    @Override
    public List<Dev> getProjectDevs(int projectID) throws ExecutionException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/participe/" + projectID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> columnsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        System.out.println("getProjectDevs json: "+columnsAsJson.get());

        Participates[] participation= g.fromJson(columnsAsJson.get(), Participates[].class);
        List<Dev> devs = new ArrayList<>();

        for (Participates participates: participation  ) {
            System.out.println("test");
            devs.add( getDev( participates.getDev_id()));
        }
        System.out.println(devs.size());
        return devs;
    }
    @Override
    public Dev getDev( int devID) throws ExecutionException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/auth/"+ devID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> columnsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        System.out.println("getDev json: "+columnsAsJson.get());

        return g.fromJson(columnsAsJson.get(), Dev.class);
    }

    @Override
    public List<Dev> getTaskDevs(int taskID) throws ExecutionException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/devTask/" + taskID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> columnsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        System.out.println(columnsAsJson.get());
        Dev[] devs = g.fromJson(columnsAsJson.get(), Dev[].class);
        return Arrays.asList(devs);
    }

    @Override
    public List<Column> getColumns(Project project) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/column/all/" + project.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> columnsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Column[] columnsArray = g.fromJson(columnsAsJson.get(), Column[].class);

        for (Column col:columnsArray) {
            col.setTasks( getColumnTasks( col.getId()));
        }

        return Arrays.asList(columnsArray);
    }

    @Override
    public List<Column> getColumns(int projectID) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/column/all/" + projectID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> columnsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Column[] columnsArray = g.fromJson(columnsAsJson.get(), Column[].class);

        for (Column col:columnsArray) {
            col.setTasks( getColumnTasks( col.getId()));
        }

        return Arrays.asList(columnsArray);
    }

    @Override
    public Column getColumn(Column column) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/column/one/" + column.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> tasksAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        return g.fromJson(tasksAsJson.get(), Column.class);
    }

    @Override
    public List<Task> getTasks(Project project) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/task/all/" + project.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> tasksAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Task[] tasksArray = g.fromJson(tasksAsJson.get(), Task[].class);
        return Arrays.asList(tasksArray);
    }

    @Override
    public List<Task> getColumnTasks(int columnId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/task/all/" + columnId))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> tasksAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Task[] tasksArray = g.fromJson(tasksAsJson.get(), Task[].class);
        return Arrays.asList(tasksArray);
    }

    @Override
    public Task getTask(Column column) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/task/one/" + column.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> taskAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        return g.fromJson(taskAsJson.get(), Task.class);
    }
}


