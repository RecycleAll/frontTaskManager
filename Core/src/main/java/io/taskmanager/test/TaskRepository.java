package io.taskmanager.test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface TaskRepository {

    List<Project> getProjects(Dev dev) throws ExecutionException, InterruptedException;

    Project getProject(Project project) throws ExecutionException, InterruptedException;
    Project getProject(int projectID) throws ExecutionException, InterruptedException;

    List<Column> getColumns(Project project) throws ExecutionException, InterruptedException;
    List<Column> getColumns(int projectID) throws ExecutionException, InterruptedException;

    Column getColumn(Column column) throws ExecutionException, InterruptedException;

    List<Task> getTasks(Project project) throws ExecutionException, InterruptedException;

    Task getTask(Column column) throws ExecutionException, InterruptedException;

    public void postProject(Dev dev, String name) throws ExecutionException, InterruptedException;

    void registerDev(String firstname, String lastname, String email, String password, String githubId) throws ExecutionException, InterruptedException;

    void postTask(String name, String description, Duration limitDate, int columnId) throws ExecutionException, InterruptedException;
}
