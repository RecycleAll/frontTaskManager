import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TaskRepositoryApi implements TaskRepository {

    private final HttpClient httpClient;
    private final Gson g ;
    private final String apiUrl;

    public TaskRepositoryApi(String apiUrl){
        httpClient = HttpClient.newHttpClient();
        g = new Gson();
        this.apiUrl = apiUrl;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        asynchronousRequest();
    }

    private static void asynchronousRequest() throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        var d = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        System.out.print(d.get());
        Gson g = new Gson();
        testJsonObject[] s = g.fromJson(d.get(),testJsonObject[].class);
        System.out.print(s[0].toString());
    }

    @Override
    public List<Project> getProjects(Dev dev) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl+"/project/all/"+dev.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Project[] projectArray = g.fromJson(projectsAsJson.get(),Project[].class);
        return Arrays.asList(projectArray);
    }

    @Override
    public Project getProject(Project project) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl+"/project/one/"+project.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        return g.fromJson(projectsAsJson.get(),Project.class);
    }

    @Override
    public List<Column> getColumns(Project project) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl+"/column/all/"+project.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> columnsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Column[] columnsArray = g.fromJson(columnsAsJson.get(),Column[].class);
        return Arrays.asList(columnsArray);
    }

    @Override
    public Column getColumn(Column column) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl+"/column/one/"+ column.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> tasksAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        return g.fromJson(tasksAsJson.get(),Column.class);
    }

    @Override
    public List<Task> getTasks(Project project) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl+"/task/all/"+project.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> tasksAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Task[] tasksArray = g.fromJson(tasksAsJson.get(),Task[].class);
        return Arrays.asList(tasksArray);
    }

    @Override
    public Task getTask(Column column) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl+"/task/one/"+ column.getId()))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> taskAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        return g.fromJson(taskAsJson.get(),Task.class);
    }
}
