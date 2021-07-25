package io.taskmanager.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import io.taskmanager.api.model.*;
import io.taskmanager.core.*;
import io.taskmanager.core.repository.RepositoryObjectDeleted;
import io.taskmanager.core.repository.TaskRepository;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TaskRepositoryApi implements TaskRepository {

    public final static DateTimeFormatter dataBaseDateFormatOut = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public final static DateTimeFormatter dataBaseDate2FormatOut = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter dataBaseDateFormatIn = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final HttpClient httpClient;
    private final Gson g;
    private final String apiUrl;

    public TaskRepositoryApi(String apiUrl) {
        httpClient = HttpClient.newHttpClient();

        g = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
            try {
                return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDateFormatOut);
            } catch (DateTimeParseException e) {
                return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDate2FormatOut);
            }

        }).registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> {
            try {
                return LocalDate.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDateFormatOut);
            } catch (DateTimeParseException e) {
                return LocalDate.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDate2FormatOut);
            }
        }).create();
        this.apiUrl = apiUrl;
    }

    private String getJson(String url) throws ExecutionException, InterruptedException {
        String requestStr = apiUrl + url;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestStr))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> responseCompletableFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET JSON: req: " + requestStr);
        HttpResponse<String> response = responseCompletableFuture.get();
        System.out.println("res: " + response.statusCode());

        if (response.statusCode() == 200) {
            return response.body();
        }
        return null;
    }

    private String postJson(String url, String jsonStr) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonStr))
                .build();

        CompletableFuture<HttpResponse<String>> responseCompletableFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST JSON: url: " + url + " \n req: " + jsonStr);
        HttpResponse<String> response = responseCompletableFuture.get();
        System.out.println("res: " + response.statusCode());
        if (response.statusCode() == 201) {
            return response.body();
        }
        return null;
    }

    private LocalDateTime putObject(String url, String jsonStr, Class<? extends BaseModel> clazz) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonStr))
                .build();

        CompletableFuture<HttpResponse<String>> responseCompletableFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("PUT JSON: url: " + url + " \n req: " + jsonStr);
        HttpResponse<String> response = responseCompletableFuture.get();
        System.out.println("res: " + response.statusCode());

        if (response.statusCode() == 200) {
            BaseModel model = g.fromJson(response.body(), (Type) clazz);
            return model.getUpdatedAt();
        } else {
            return null;
        }

    }

    private boolean deleteObject(String url) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + url))
                .timeout(Duration.ofSeconds(10))
                .DELETE()
                .build();
        CompletableFuture<HttpResponse<String>> responseCompletableFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("DELETE JSON: url: " + url);
        HttpResponse<String> response = responseCompletableFuture.get();
        System.out.println("res: " + response.statusCode());

        return response.statusCode() == 200;
    }

    private <T> T postObject(String url, String jsonStr, Class<?> objectType) throws ExecutionException, InterruptedException {
        String responseJson = postJson(url, jsonStr);
        if (responseJson != null) {
            return g.fromJson(responseJson, (Type) objectType);
        } else {
            return null;
        }
    }

    private <T> T getObject2(String url, Class<?> c) throws ExecutionException, InterruptedException {
        String json = getJson(url);
        return g.fromJson(json, (Type) c);
    }

    @Override
    public int loginDev(String email, String password) throws ExecutionException, InterruptedException {
        String requestJson = "{ " +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"" +
                "}";

        SessionModel model = postObject("/auth/login", requestJson, SessionModel.class);

        if (model != null) {
            return model.getDev_id();
        } else {
            return -1;
        }
    }

    @Override
    public Map<Integer, DevStatus> getAllDevProject(int devID) throws ExecutionException, InterruptedException {

        Map<Integer, DevStatus> res = new HashMap<>();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/participe/getAll/dev/" + devID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> tmp = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = tmp.get();

        if (response.statusCode() == 200) {
            Participates[] participates = g.fromJson(response.body(), Participates[].class);

            for (Participates participate : participates) {
                res.put(participate.getProject_id(), participate.isOwner() ? DevStatus.OWNER : DevStatus.DEV);
            }
            return res;
        }
        return null;
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
        ProjectModel projectModel = getObject2("/project/one/" + projectID, ProjectModel.class);
        if (projectModel != null)
            return projectModel.convert();
        return null;
    }

    @Override
    public Map<Integer, DevStatus> getProjectDevs(int projectID) throws ExecutionException, InterruptedException {
        Participates[] participation = getObject2("/participe/" + projectID, Participates[].class);
        Map<Integer, DevStatus> devs = new HashMap<>();

        for (Participates participates : participation) {
            devs.put(participates.getDev_id(), participates.isOwner() ? DevStatus.OWNER : DevStatus.DEV);
        }
        return devs;
    }

    @Override
    public Dev getDev(int id) throws ExecutionException, InterruptedException {
        DevModel devModel = getObject2("/auth/" + id, DevModel.class);
        if (devModel != null) {
            return devModel.convert();
        } else {
            return null;
        }
    }

    @Override
    public List<Dev> getAllDev() throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/auth/"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> columnsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        Dev[] devs = g.fromJson(columnsAsJson.get(), Dev[].class);
        return new ArrayList<>(Arrays.asList(devs));
    }

    @Override
    public boolean postDevTask(Task task, Dev dev) throws ExecutionException, InterruptedException {
        return postDevTask(task.getId(), dev.getId());
    }

    @Override
    public boolean postDevTask(int taskId, int devId) throws ExecutionException, InterruptedException {
        String json = "{\"devId\":\"" + devId + "\"," +
                "\"taskId\":\"" + taskId + "\"" +
                "}";
        return postObject("/devTask", json, DevTaskModel.class) != null;
    }


    @Override
    public List<Integer> getTaskDevsID(int taskID) throws ExecutionException, InterruptedException {
        DevTaskModel[] devTaskModels = getObject2("/devTask/" + taskID, DevTaskModel[].class);
        return Arrays.stream(devTaskModels).map(DevTaskModel::getDev_id).collect(Collectors.toList());
    }

    private DevTaskModel getDevTask(int taskId, int devId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/devTask/" + taskId + "/" + devId))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> taskAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        return g.fromJson(taskAsJson.get(), DevTaskModel.class);
    }

    @Override
    public boolean deleteDevTAsk(Task task, Dev dev) throws ExecutionException, InterruptedException {
        return deleteDevTAsk(task.getId(), dev.getId());
    }

    @Override
    public boolean deleteDevTAsk(int taskId, int devId) throws ExecutionException, InterruptedException {
        DevTaskModel devTaskModel = getDevTask(taskId, devId);
        return deleteObject("/devTask/" + devTaskModel.getId());
    }

    @Override
    public List<Column> getColumns(int projectID) throws ExecutionException, InterruptedException {
        ColumnModel[] cols = getObject2("/column/all/" + projectID, ColumnModel[].class);
        System.out.println("getColumns: " + cols.length);
        return Arrays.stream(cols).map(ColumnModel::convert).collect(Collectors.toList());
    }

    @Override
    public boolean updateProject(Project project) throws ExecutionException, InterruptedException {
        String json = "{ \"id\":\"" + project.getId() + "\"," +
                "\"name\":\"" + project.getName() + "\"}";

        LocalDateTime time = putObject("/project/", json, ProjectModel.class);
        if (time != null) {
            project.setUpdatedAt(time);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteTask(Task task) throws ExecutionException, InterruptedException {
        return deleteTask(task.getId());
    }

    @Override
    public boolean deleteTask(int taskId) throws ExecutionException, InterruptedException {
        return deleteObject("/task/" + taskId);
    }

    @Override
    public boolean updateTask(Task task) throws ExecutionException, InterruptedException {
        String json = "{ \"id\":\"" + task.getId() + "\"," +
                "\"name\":\"" + task.getName() + "\"," +
                "\"description\":\"" + task.getDescription() + "\"," +
                "\"limitDate\":\"" + task.getLimitDate().format(dataBaseDateFormatIn) + "\"}";

        LocalDateTime time = putObject("/task/", json, TaskModel.class);
        if (time != null) {
            task.setUpdatedAt(time);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Column getColumn(int id) throws ExecutionException, InterruptedException {
        ColumnModel model = getObject2("/column/one/" + id, ColumnModel.class);
        if (model != null) {
            return model.convert();
        } else {
            return null;
        }
    }

    @Override
    public boolean deleteColumn(Column column) throws ExecutionException, InterruptedException {
        return deleteObject("/column/" + column.getId());
    }

    @Override
    public boolean putColumn(Column column) throws ExecutionException, InterruptedException {


        LocalDateTime time = putObject("/column/", "{ " +
                        "\"id\":\"" + column.getId() + "\"," +
                        "\"name\":\"" + column.getName() + "\"" +
                        "}",
                ColumnModel.class);
        if (time != null) {
            column.setUpdatedAt(time);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Task> getColumnTasks(int columnId) throws ExecutionException, InterruptedException {
        TaskModel[] tasksArray = getObject2("/task/all/" + columnId, TaskModel[].class);
        return Arrays.stream(tasksArray).map(TaskModel::convert).collect(Collectors.toList());
    }

    @Override
    public Task getTask(int id) throws ExecutionException, InterruptedException, RepositoryObjectDeleted {
        TaskModel model = getObject2("/task/one/" + id, TaskModel.class);
        if (model != null) {
            return model.convert();
        } else {
            throw new RepositoryObjectDeleted();
        }
    }

    @Override
    public boolean postParticipate(Project project, Dev dev) throws ExecutionException, InterruptedException {
        return postParticipate(project.getId(), dev.getId());
    }

    @Override
    public boolean postParticipate(int projectID, int devId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/participe/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ " +
                        "\"projectId\":\"" + projectID + "\"," +
                        "\"devId\":\"" + devId + "\"}"))
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return projectsAsJson.get().statusCode() == 201;
    }

    private Participates getParticipate(int projectID, int devId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/participe/" + projectID + "/" + devId))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> taskAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        return g.fromJson(taskAsJson.get(), Participates.class);
    }

    @Override
    public boolean deleteParticipate(Project project, Dev dev) throws ExecutionException, InterruptedException {
        return deleteParticipate(project.getId(), dev.getId());
    }

    @Override
    public boolean deleteParticipate(int projectID, int devId) throws ExecutionException, InterruptedException {

        Participates participates = getParticipate(projectID, devId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/participe/" + participates.getId()))
                .timeout(Duration.ofSeconds(10))
                .DELETE()
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return projectsAsJson.get().statusCode() == 200;
    }

    @Override
    public boolean postProject(Dev dev, Project project) throws ExecutionException, InterruptedException {
        String json = "{ \"name\":\"" + project.getName() +
                "\",\"devId\":" + dev.getId() + "}";

        ProjectModel model = postObject("/project/", json, ProjectModel.class);
        if (model != null) {
            project.setId(model.getId());
            project.setUpdatedAt(model.getUpdatedAt());
            return true;
        }
        return false;
    }

    @Override
    public boolean registerDev(Dev dev) throws ExecutionException, InterruptedException {
        String json = "{ \"firstname\":\"" + dev.getFirstname() + "\"," +
                "\"lastname\":\"" + dev.getLastname() + "\"," +
                "\"email\":\"" + dev.getEmail() + "\"," +
                "\"password\":\"" + dev.getPassword() + "\"," +
                "\"githubId\":\"" + dev.getGithub_id() + "\"}";

        DevModel model = postObject("/auth/register", json, DevModel.class);
        if (model != null) {
            dev.setId(model.getId());
            dev.setUpdatedAt(model.getUpdatedAt());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateDev(Dev dev) throws ExecutionException, InterruptedException {
        String json = "{ \"id\":\"" + dev.getId() + "\"," +
                "\"firstname\":\"" + dev.getFirstname() + "\"," +
                "\"lastname\":\"" + dev.getLastname() + "\"," +
                "\"email\":\"" + dev.getEmail() + "\"," +
                //TODO: "\"password\":\""+dev.getPassword()+"\"," +
                "\"githubId\":\"" + dev.getGithub_id() + "\"}";

        LocalDateTime time = putObject("/auth/", json,
                ColumnModel.class);
        if (time != null) {
            dev.setUpdatedAt(time);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int postColumn(Column column) throws ExecutionException, InterruptedException {
        String jsonRequest = "{ " +
                "\"name\":\"" + column.getName() + "\"," +
                "\"projectId\":\"" + column.getProjectId() + "\"}";

        ColumnModel model = postObject("/column/", jsonRequest, ColumnModel.class);

        if (model != null) {
            column.setId(model.getId());
            return model.getId();
        } else {
            return -1;
        }
    }

    @Override
    public Task postTask(Task task, int columnId) throws ExecutionException, InterruptedException {
        String json = "{ \"name\":\"" + task.getName() + "\"," +
                "\"description\":\"" + task.getDescription() + "\"," +
                "\"limitDate\":\"" + task.getLimitDate().format(dataBaseDateFormatIn) + "\"," +
                "\"columnId\":\"" + columnId + "\"}";

        TaskModel model = postObject("/task/", json, TaskModel.class);
        if (model != null) {
            task.setId(model.getId());
            task.setUpdatedAt(model.getUpdatedAt());
            return task;
        }
        return null;
    }

}


