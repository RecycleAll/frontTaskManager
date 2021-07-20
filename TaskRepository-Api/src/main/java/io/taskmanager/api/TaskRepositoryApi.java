package io.taskmanager.api;

import com.google.gson.*;
import io.taskmanager.api.model.*;
import io.taskmanager.core.*;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.RepositoryObject;
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

    private RepositoryManager repositoryManager;

    public TaskRepositoryApi(String apiUrl) {
        httpClient = HttpClient.newHttpClient();

        g = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                try{
                    return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDateFormatOut);
                }catch (DateTimeParseException e){
                    return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDate2FormatOut);
                }

            }
        }).registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                try{
                    return LocalDate.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDateFormatOut);
                }catch (DateTimeParseException e){
                    return LocalDate.parse(json.getAsJsonPrimitive().getAsString(), dataBaseDate2FormatOut);
                }
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

    private Class<? extends BaseModel> convertApiRequestTypeToBaseModel(Class<? extends RepositoryObject> c){

        if (Column.class.equals(c)) {
            return ColumnModel.class;
        } else if (Task.class.equals(c)) {
            return TaskModel.class;
        } else if (Project.class.equals(c)) {
            return ProjectModel.class;
        }

        return null;
    }

    private String getJson(String url) throws ExecutionException, InterruptedException {
        String requestStr = apiUrl + url;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestStr))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> responseCompletableFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET JSON: req: " + requestStr );
        HttpResponse<String> response = responseCompletableFuture.get();
        System.out.println("res: "+ response.statusCode());

        if( response.statusCode() == 200 ) {
            return response.body();
        }
        return null;
    }

    private String postJson(String url, String jsonStr) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST( HttpRequest.BodyPublishers.ofString(jsonStr))
                .build();

        CompletableFuture<HttpResponse<String>> responseCompletableFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST JSON: url: "+ url+" \n req: " + jsonStr );
        HttpResponse<String> response = responseCompletableFuture.get();
        System.out.println("res: "+ response.statusCode());
        if( response.statusCode() == 201 ) {
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
        System.out.println("PUT JSON: url: "+ url+" \n req: " + jsonStr );
        HttpResponse<String> response = responseCompletableFuture.get();
        System.out.println("res: "+ response.statusCode());

        if( response.statusCode() == 200 ){
            BaseModel model = g.fromJson(response.body(), (Type) clazz);
            return model.getUpdatedAt();
        }else{
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

        System.out.println("DELETE JSON: url: "+ url );
        HttpResponse<String> response = responseCompletableFuture.get();
        System.out.println("res: "+ response.statusCode());

        return response.statusCode() == 200;
    }

    private <T> T postObject(String url, String jsonStr, Class<?> objectType) throws ExecutionException, InterruptedException {
        String responseJson = postJson(url, jsonStr);
        if(responseJson != null){
            return g.fromJson(responseJson, (Type) objectType);
        }else{
            return null;
        }
    }

    private <T> T getObject2(String url, Class<?> c) throws ExecutionException, InterruptedException {
        String json = getJson(url);
        return g.fromJson(json, (Type) c);
    }

    public <T extends RepositoryObject> T getObject(int id, Class<? extends RepositoryObject> c) throws ExecutionException, InterruptedException {
        Class<? extends BaseModel> baseModelType =  convertApiRequestTypeToBaseModel(c);
        return getObject_(id, baseModelType);
    }

    private <T extends RepositoryObject> T getObject_(int id, Class<? extends BaseModel> baseModelType) throws ExecutionException, InterruptedException {
        String requestStr = apiUrl + resolveUrlGetter(baseModelType) + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestStr))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> projectsAsJson = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = projectsAsJson.get();

        if( response.statusCode() == 200 ) {
            return ModelConverter.convert(g.fromJson(response.body(), baseModelType));
        }
        return null;
    }

    private <T extends RepositoryObject> List<T> getObjectList(int id, Class<? extends BaseModel> baseModelType) throws ExecutionException, InterruptedException {
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
    public int loginDev(String email, String password) throws ExecutionException, InterruptedException {
        String requestJson = "{ " +
                "\"email\":\""+email+"\"," +
                "\"password\":\""+password+"\"" +
                "}";

        SessionModel model = postObject("/auth/login", requestJson, SessionModel.class );

        if( model != null ) {
            return model.getDev_id();
        }else{
            return -1;
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
    public List<Integer> getProjectColumns(int projectId) throws ExecutionException, InterruptedException {
        ColumnModel[] cols = getObject2("/column/all/" + projectId, ColumnModel[].class);
        return Arrays.stream(cols).map(BaseModel::getId).collect(Collectors.toList());
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
        ProjectModel projectModel = getObject2("/project/one/" + projectID, ProjectModel.class);
        return projectModel.convert();
    }

    @Override
    public Map<Integer,DevStatus> getProjectDevs(int projectID) throws ExecutionException, InterruptedException{
        Participates[] participation= getObject2( "/participe/" + projectID, Participates[].class);
        Map<Integer,DevStatus> devs = new HashMap<>();

        for (Participates participates: participation  ) {
            devs.put( participates.getDev_id(), participates.isOwner()?  DevStatus.OWNER: DevStatus.DEV);
        }
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
    public Dev getDev(int id) throws ExecutionException, InterruptedException, RepositoryObjectDeleted {
        DevModel devModel = getObject2("/auth/"+id, DevModel.class);
        if(devModel != null) {
            return devModel.convert();
        }else{
            return null;
            //throw new RepositoryObjectDeleted("get dev: "+id);
        }
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
        String json = "{\"devId\":\""+devId+"\"," +
                        "\"taskId\":\""+taskId+"\"" +
                        "}";
        return postObject("/devTask", json, DevTaskModel.class) != null;
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

    @Override
    public List<Integer> getTaskDevsID(int taskID) throws ExecutionException, InterruptedException{
        DevTaskModel[] devTaskModels = getObject2("/devTask/" + taskID, DevTaskModel[].class);
        return Arrays.stream(devTaskModels).map(DevTaskModel::getDev_id).collect(Collectors.toList());
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
        return deleteObject("/devTask/" + devTaskModel.getId());
    }

    @Override
    public List<Column> getColumns(Project project) throws ExecutionException, InterruptedException {
        return  getColumns(project.getId());
    }

    @Override
    public List<Column> getColumns(int projectID) throws ExecutionException, InterruptedException {
        ColumnModel[] cols = getObject2("/column/all/" + projectID, ColumnModel[].class);
        System.out.println("getColumns: "+ cols.length);
        return Arrays.stream(cols).map(ColumnModel::convert).collect(Collectors.toList());
    }

    @Override
    public boolean updateProject(Project project) throws ExecutionException, InterruptedException {
        String json = "{ \"id\":\""+project.getId()+"\"," +
                        "\"name\":\""+project.getName()+"\"}";

        LocalDateTime time = putObject("/project/", json, ProjectModel.class);
        if( time != null) {
            project.setUpdatedAt(time);
            return true;
        }else{
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
        String json = "{ \"id\":\""+task.getId()+"\"," +
                "\"name\":\""+task.getName()+"\"," +
                "\"description\":\""+task.getDescription()+"\"," +
                "\"limitDate\":\""+task.getLimitDate().format(dataBaseDateFormatIn)+"\"}";

        LocalDateTime time = putObject("/task/", json, TaskModel.class);
        if( time != null) {
            task.setUpdatedAt(time);
            return true;
        }else{
            return false;
        }
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
        ColumnModel model = getObject2("/column/one/" +id, ColumnModel.class);
        if( model != null){
            return model.convert();
        }else{
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
                                            "\"id\":\""+column.getId()+"\"," +
                                            "\"name\":\""+column.getName()+"\"" +
                                            "}",
                                            ColumnModel.class);
        if( time != null) {
            column.setUpdatedAt(time);
            return true;
        }else{
            return false;
        }
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
        TaskModel[] tasksArray = getObject2("/task/all/" + columnId, TaskModel[].class);
        return Arrays.stream(tasksArray).map(TaskModel::convert).collect(Collectors.toList());
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
        TaskModel model = getObject2("/task/one/"+id, TaskModel.class);
        if( model != null) {
            return model.convert();
        }else{
            return null;
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
                .uri(URI.create(apiUrl + "/auth/register"))
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
    public boolean updateDev(Dev dev) throws ExecutionException, InterruptedException {
        String json = "{ \"id\":\""+dev.getId()+"\"," +
                        "\"firstname\":\""+dev.getFirstname()+"\"," +
                        "\"lastname\":\""+dev.getLastname()+"\"," +
                        "\"email\":\""+dev.getEmail()+"\"," +
                //TODO: "\"password\":\""+dev.getPassword()+"\"," +
                        "\"githubId\":\""+dev.getGithub_id()+"\"}";

        LocalDateTime time = putObject("/auth/", json,
                ColumnModel.class);
        if( time != null) {
            dev.setUpdatedAt(time);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public int postColumn(Column column) throws ExecutionException, InterruptedException {
        String jsonRequest = "{ " +
                "\"name\":\""+column.getName()+"\"," +
                "\"projectId\":\""+ column.getProjectId() +"\"}";

        ColumnModel model = postObject("/column/", jsonRequest, ColumnModel.class);

        if( model != null) {
            column.setId(model.getId());
            return model.getId();
        }else{
            return -1;
        }
    }

    @Override
    public Task postTask(String name, String description, LocalDate limitDate, int columnId) throws ExecutionException, InterruptedException {
        String json = "{ \"name\":\""+name+"\"," +
                "\"description\":\""+description+"\"," +
                "\"limitDate\":\""+limitDate.format(dataBaseDateFormatIn)+"\"," +
                "\"columnId\":\""+columnId+"\"}";

        TaskModel model = postObject("/task/", json, TaskModel.class);
        if( model != null) {
            return model.convert();
        }
        return null;
    }

    @Override
    public Task postTask(Task task, int columnId) throws ExecutionException, InterruptedException {
        String json = "{ \"name\":\""+task.getName()+"\"," +
                "\"description\":\""+task.getDescription()+"\"," +
                "\"limitDate\":\""+task.getLimitDate().format(dataBaseDateFormatIn)+"\"," +
                "\"columnId\":\""+columnId+"\"}";

        TaskModel model = postObject("/task/", json, TaskModel.class);
        if( model != null) {
            task.setId(model.getId());
            task.setUpdatedAt(model.getUpdatedAt());
            return task;
        }
        return null;
    }

    @Override
    public boolean postTask(Task task) throws ExecutionException, InterruptedException {
        String json = "{ \"name\":\""+task.getName()+"\"," +
                "\"description\":\""+task.getDescription()+"\"," +
                "\"limitDate\":\""+task.getLimitDate().format(dataBaseDateFormatIn)+"\"," +
                "\"columnId\":\""+0+"\"}";

        TaskModel model = postObject("/task/", json, TaskModel.class);
        if( model != null) {
            task.setId(model.getId());
            task.setUpdatedAt(model.getUpdatedAt());
            return true;
        }
        return false;
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


