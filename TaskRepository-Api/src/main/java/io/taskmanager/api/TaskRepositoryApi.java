package io.taskmanager.api;

import com.google.gson.*;
import io.taskmanager.api.model.*;
import io.taskmanager.test.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TaskRepositoryApi implements TaskRepository {

    public final static DateTimeFormatter dataBaseDateFormatOut = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public final static DateTimeFormatter dataBaseDateFormatIn = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final HttpClient httpClient;
    private final Gson g;
    private final String apiUrl;

    private RepositoryManager repositoryManager;

    public TaskRepositoryApi(String apiUrl) {
        httpClient = HttpClient.newHttpClient();

        g = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDate.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDateFormatOut);
            }
        }).registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDateFormatOut);
            }
        }).create();
        this.apiUrl = apiUrl;
    }

    private String resolveUrlGetter(Class<? extends BaseModel> c){
        if( c == ColumnModel.class){
            return "/column/one/";
        }else if( c == ProjectModel.class){
            return "/project/one/";
        }else if( c == TaskModel.class){
            return "/task/one/";
        }else if( c == DevModel.class){
            return "/auth";
        }
        return "";
    }

    private Class<? extends BaseModel> convertApiRequestTypeToBaseModel(Class<? extends ApiRequest> c){

        if (Column.class.equals(c)) {
            return ColumnModel.class;
        } else if (Task.class.equals(c)) {
            return TaskModel.class;
        } else if (Project.class.equals(c)) {
            return ProjectModel.class;
        }

        return null;
    }

    public <T extends ApiRequest> T getObject(int id, Class<? extends ApiRequest> c) throws ExecutionException, InterruptedException {
        Class<? extends BaseModel> baseModelType =  convertApiRequestTypeToBaseModel(c);
        return getObject_(id, baseModelType);
    }

    private <T extends ApiRequest> T getObject_(int id, Class<? extends BaseModel> baseModelType) throws ExecutionException, InterruptedException {
        String requestStr = apiUrl + resolveUrlGetter(baseModelType) + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestStr))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = projectsAsJson.get();
        System.out.println("GET: " + baseModelType + " req: " + requestStr + " \n res: "+ response.statusCode());
        if( response.statusCode() == 200 ) {
            return ModelConverter.convert(g.fromJson(response.body(), baseModelType));
        }
        return null;
    }

    private <T extends ApiRequest> List<T> getObjectList(int id, Class<? extends BaseModel> baseModelType) throws ExecutionException, InterruptedException {
        String requestStr = apiUrl + resolveUrlGetter(baseModelType) + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestStr))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = projectsAsJson.get();
        System.out.println("GET: " + baseModelType + " req: " + requestStr + " \n res: "+ response.statusCode());
        if( response.statusCode() == 200 ) {
            return ModelConverter.convert(g.fromJson(response.body(), baseModelType));
        }
        return null;
    }

    @Override
    public Dev loginDev(String email, String password) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/auth/login"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST( HttpRequest.BodyPublishers.ofString("{ " +
                        "\"email\":\""+email+"\"," +
                        "\"password\":\""+password+"\"" +
                        "}"))
                .build();

        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        //System.out.println("code: "+projectsAsJson.get().statusCode());
        //System.out.println("json: "+projectsAsJson.get().body());
        if( projectsAsJson.get().statusCode() == 201 ) {
            SessionModel session = g.fromJson(projectsAsJson.get().body(), SessionModel.class);
            return getDev(session.getDev_id());
        }else{
            return null;
        }
    }

    @Override
    public Map<Integer, DevStatus> getAllDevProject(int devID) throws ExecutionException, InterruptedException {

        Map<Integer, DevStatus> res = new HashMap<>();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/participe/getAll/dev/"+ devID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> tmp = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = tmp.get();

        if( response.statusCode() == 200 ) {
            Participates[] participates = g.fromJson(response.body(), Participates[].class);

            for (Participates participate:participates ) {
                res.put(participate.getProject_id(), participate.isOwner()? DevStatus.OWNER : DevStatus.DEV );
            }
            return res;
        }
        return null;
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
        System.out.println("getProject: "+ projectID);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/project/one/" + projectID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        ProjectModel projectModel = g.fromJson(projectsAsJson.get(), ProjectModel.class);

        Map<Dev, DevStatus> devs = getProjectDevs(projectID);
        List<Column> columns = getColumns(projectID);

        for (Column col : columns) {
            for (Task task : col.getTasks()) {
                int[] assignedDevs = getTaskDevsID(task.getId());

                for (int devID : assignedDevs) {
                    Dev dev = devs.keySet().stream().filter(dev1 -> dev1.getId() == devID).findFirst().orElseThrow(() -> new Exception("Dev ID inside task is not present inside project"));
                    task.addDev(dev);
                }
            }
        }
        Project project = new Project(repositoryManager, projectModel.getId(), projectModel.getName(), "", columns, new ArrayList<>(), devs);

        System.out.println("getProject return: "+ project);
        return project;
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
    public Map<Dev,DevStatus> getProjectDevs(int projectID) throws ExecutionException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/participe/" + projectID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> columnsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        System.out.println("getProjectDevs json: "+columnsAsJson.get());

        Participates[] participation= g.fromJson(columnsAsJson.get(), Participates[].class);
        Map<Dev,DevStatus> devs = new HashMap<>();

        for (Participates participates: participation  ) {
            if (participates.isOwner())
                devs.put(getDev(participates.getDev_id()), DevStatus.OWNER);
            else {
                devs.put(getDev(participates.getDev_id()), DevStatus.DEV);
            }
        }
        System.out.println("getProjectDevs size: "+ devs.size());
        return devs;
    }

    private List<Project> getDevProject(int devID) throws ExecutionException, InterruptedException {
        List<Project> projects;
        System.out.println("getDevProject: "+ devID);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/participe/getAll/dev/"+ devID))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> columnsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        Participates[] participates = g.fromJson(columnsAsJson.get(), Participates[].class);

        for (Participates participate:participates ) {
            System.out.println("d: "+ participate.getDev_id() +" p: " +participate.getProject_id());
        }

        projects = Arrays.stream(participates)
                        .map(participate -> {
                            try {
                                return getProject(participate.getProject_id());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .collect(Collectors.toList());

        System.out.println("ps: "+ projects.size());
        for (Project p:projects ) {
            System.out.println("pp: "+ p.getId() );
        }

        return projects;
    }

    @Override
    public Dev getDev(int id) throws ExecutionException, InterruptedException{
        return getObject(id, Dev.class);
    }

    @Override
    public List<Dev> getAllDev() throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/auth/" ))
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/devTask"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST( HttpRequest.BodyPublishers.ofString("{ " +
                        "\"devId\":\""+devId+"\"," +
                        "\"taskId\":\""+taskId+"\"" +
                        "}"))
                .build();

        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return projectsAsJson.get().statusCode() == 201 ;
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
        return new ArrayList<>( Arrays.asList(devs) );
    }

    private DevTaskModel getDevTask(int taskId, int devId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/devTask/" + taskId+"/"+devId))
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/devTask/" + devTaskModel.getId()))
                .timeout(Duration.ofSeconds(10))
                .DELETE()
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return projectsAsJson.get().statusCode() == 200;
    }

    @Override
    public List<Column> getColumns(Project project) throws ExecutionException, InterruptedException {
        return  getColumns(project.getId());
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
        List<Column> columns = new ArrayList<>();

        for (Column col : columnsArray) {
            Column c = new Column(repositoryManager, col);
            columns.add(c);
            c.setTasks( getColumnTasks( col.getId()));
        }

        return columns;
    }

    @Override
    public boolean deleteTask(Task task) throws ExecutionException, InterruptedException {
        return deleteTask(task.getId());
    }

    @Override
    public boolean deleteTask(int taskId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/task/" + taskId))
                .timeout(Duration.ofSeconds(10))
                .DELETE()
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return projectsAsJson.get().statusCode() == 200;
    }

    @Override
    public boolean updateTask(Task task) throws ExecutionException, InterruptedException {
        return updateTask(task.getId());
    }

    @Override
    public boolean updateTask(int taskId) throws ExecutionException, InterruptedException {
        return false;
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
        Column col = g.fromJson(tasksAsJson.get(), Column.class);
        return new Column(repositoryManager, col.getId(), col.getName(), col.getProjectId(), col.getTasks());
    }

    @Override
    public Column getColumn(int id) throws ExecutionException, InterruptedException {
        return getObject(id, Column.class);
    }

    @Override
    public boolean deleteColumn(Column column) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/column/" + column.getId()))
                .timeout(Duration.ofSeconds(10))
                .DELETE()
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return projectsAsJson.get().statusCode() == 200;
    }

    @Override
    public boolean putColumn(Column column) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/column/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString("{ " +
                        "\"id\":\""+column.getId()+"\"," +
                        "\"name\":\""+column.getName()+"\"" +
                        "}"))
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());



        return projectsAsJson.get().statusCode() == 200;
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
        return new ArrayList<>( Arrays.asList(tasksArray));
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

        for (Task t: tasksArray) {
            System.out.println(t.getDevs());
        }

        return Arrays.stream(tasksArray).map(task -> {
            try {
                List<Dev> devs = getTaskDevs(task.getId());
                task.setDevs(devs);
                return new Task(repositoryManager, task);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
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
    public Task getTask(int id) throws ExecutionException, InterruptedException {
        return null;
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
                        "\"projectId\":\""+projectID+"\"," +
                        "\"devId\":\""+devId+"\"}"))
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return projectsAsJson.get().statusCode() == 201;
    }

    private Participates getParticipate(int projectID, int devId) throws ExecutionException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/participe/" + projectID+"/"+devId))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<String> taskAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        return g.fromJson(taskAsJson.get(), Participates.class);
    };

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
    public void postProject(Dev dev, Project project) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/project/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"name\":\""+project.getName()+"\",\"devId\":"+dev.getId() +"}"))
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
    public int postColumn(Column column) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/column/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ " +
                        "\"name\":\""+column.getName()+"\"," +
                        "\"projectId\":\""+column.getProjectId()+"\"}"))
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        if( projectsAsJson.get().statusCode() == 201 ) {
            ColumnModel columnModel = g.fromJson(projectsAsJson.get().body(), ColumnModel.class);
            column.setId(columnModel.getId());
            return columnModel.getId();
        }else{
            return -1;
        }
    }

    @Override
    public Task postTask(String name, String description, LocalDate limitDate, int columnId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/task/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"name\":\""+name+"\"," +
                        "\"description\":\""+description+"\"," +
                        "\"limitDate\":\""+limitDate.format(dataBaseDateFormatIn)+"\"," +
                        "\"columnId\":\""+columnId+"\"}"))
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        if( projectsAsJson.get().statusCode() == 201 ) {
            TaskModel taskModel = g.fromJson(projectsAsJson.get().body(), TaskModel.class);
            return new Task(repositoryManager, taskModel.getId(), taskModel.getName(), taskModel.getDescription(), taskModel.getLimitDate());
        }else{
            return null;
        }
    }

    @Override
    public Task postTask(Task task, int columnId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/task/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"name\":\""+task.getName()+"\"," +
                        "\"description\":\""+task.getDescription()+"\"," +
                        "\"limitDate\":\""+task.getLimitDate().format(dataBaseDateFormatIn)+"\"," +
                        "\"columnId\":\""+columnId+"\"}"))
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        if( projectsAsJson.get().statusCode() == 201 ) {
            TaskModel taskModel = g.fromJson(projectsAsJson.get().body(), TaskModel.class);
            task.setId(taskModel.getId());
            return task;
        }else{
            return null;
        }
    }

    @Override
    public Column postColumn(String name, int projectId) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/column/"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{ " +
                        "\"name\":\""+name+"\"," +
                         "\"projectId\":\""+projectId+"\"}"))
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        if( projectsAsJson.get().statusCode() == 201 ) {
            ColumnModel columnModel = g.fromJson(projectsAsJson.get().body(), ColumnModel.class);
            return new Column(repositoryManager, columnModel.getId(), columnModel.getName(), columnModel.getProject_id());
        }else{
            return null;
        }
    }
}


