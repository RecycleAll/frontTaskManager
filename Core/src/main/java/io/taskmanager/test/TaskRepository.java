package io.taskmanager.test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface TaskRepository {

    Dev loginDev(String login, String password) throws ExecutionException, InterruptedException;

    List<Project> getProjects(Dev dev) throws ExecutionException, InterruptedException;

    Project getProject(Project project) throws ExecutionException, InterruptedException;
    Project getProject(int projectID) throws Exception;

    List<Column> getColumns(Project project) throws ExecutionException, InterruptedException;
    List<Column> getColumns(int projectID) throws ExecutionException, InterruptedException;




    List<Task> getTasks(Project project) throws ExecutionException, InterruptedException;
    List<Task> getColumnTasks(int columnId) throws ExecutionException, InterruptedException;

    Map<Dev,DevStatus> getProjectDevs(int projectID) throws ExecutionException, InterruptedException;
    Dev getDev(int devID) throws ExecutionException, InterruptedException;
    List<Dev> getAllDev() throws ExecutionException, InterruptedException;

    List<Dev> getTaskDevs(int taskID) throws ExecutionException, InterruptedException;

    Task getTask(Column column) throws ExecutionException, InterruptedException;

    void postProject(Dev dev, Project project) throws ExecutionException, InterruptedException;

    void registerDev(String firstname, String lastname, String email, String password, String githubId) throws ExecutionException, InterruptedException;

    void postTask(String name, String description, LocalDate limitDate, int columnId) throws ExecutionException, InterruptedException;

    void postColumn(String name, int projectId) throws ExecutionException, InterruptedException;
    Column getColumn(Column column) throws ExecutionException, InterruptedException;
    boolean deleteColumn(Column column) throws ExecutionException, InterruptedException;
    boolean putColumn(Column column) throws ExecutionException, InterruptedException;
}
