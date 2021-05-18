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
import java.util.Optional;
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
        HttpClient httpClient = HttpClient.newHttpClient();

        String name = "name", description = "dex";
        LocalDate limitDate = LocalDate.now();
        int columnId = 1;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create( "http://localhost:3000/task/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"name\":\""+name+"\"," +
                        "\"description\":\""+description+"\"," +
                        "\"limitDate\":\""+limitDate+"\"," +
                        "\"columnId\":\""+columnId+"\"}"))
                .build();

        if(request.bodyPublisher().isPresent() )
            System.out.println("req: "+request.toString());

        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("headers: "+response.get().statusCode());

    }

    @Override
    public Dev loginDev(String login, String password) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/auth/login"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST( HttpRequest.BodyPublishers.ofString("{ " +
                        "\"email\":\""+login+"\"," +
                        "\"password\":\""+password+"\"" +
                        "}"))
                .build();

        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("code: "+projectsAsJson.get().statusCode());
        if( projectsAsJson.get().statusCode() == 201 ) {
            SessionModel session = g.fromJson(projectsAsJson.get().body(), SessionModel.class);


            return getDev(session.getDev_id());
        }else{
            return null;
        }
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
    public Project getProject(int projectID) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/project/one/" + projectID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        ProjectModel projectModel = g.fromJson(projectsAsJson.get(), ProjectModel.class);

        List<Dev> devs = getProjectDevs(projectID);
        List<Column> columns = getColumns(projectID);

        for (Column col: columns) {
            for (Task task: col.getTasks()) {
                int[] assignedDevs = getTaskDevsID(task.getId());

                for (int devID: assignedDevs ) {
                    Dev dev = devs.stream().filter(dev1 -> dev1.getId() == devID).findFirst().orElseThrow( () -> new Exception("Dev ID inside task is not present inside project"));
                    task.addDev(dev);
                }
            }
        }

        return new Project(this,  projectModel.getId(), projectModel.getName(), "", columns, new ArrayList<>(), devs);
    }

    private int[] getTaskDevsID(int taskID) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/devTask/" + taskID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        DevTaskModel[] devs = g.fromJson(projectsAsJson.get(), DevTaskModel[].class);

        int[] devsID = new int[devs.length];
        for (int i = 0; i < devs.length; i++) {
            devsID[i] = devs[i].getDev_id();
        }
        return devsID;
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

    @Override
    public void postProject(Dev dev, String name) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/project/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"name\":\""+name+"\",\"devId\":"+dev.getId() +"}"))
                .build();
        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.print(response.get().body());
    }

    @Override
    public void registerDev(String firstname, String lastname, String email, String password, String githubId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/project/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"firstname\":\""+firstname+"\"," +
                        "\"lastname\":\""+lastname+"\"," +
                        "\"email\":\""+email+"\"," +
                        "\"password\":\""+password+"\"," +
                        "\"githubId\":\""+githubId+"\"}"))
                .build();
        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.print(response.get().body());
    }

    @Override
    public void postTask(String name, String description, LocalDate limitDate, int columnId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/task/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"name\":\""+name+"\"," +
                        "\"description\":\""+description+"\"," +
                        "\"limitDate\":\""+limitDate+"\"," +
                        "\"columnId\":\""+columnId+"\"}"))
                .build();
        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.print(response.get().body());
    }

    @Override
    public void postColumn(String name, int projectId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/column/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"name\":\""+name+"\"," +
                        "\"projectId\":\""+projectId+"\"}"))
                .build();
        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.print(response.get().body());
    }
}


