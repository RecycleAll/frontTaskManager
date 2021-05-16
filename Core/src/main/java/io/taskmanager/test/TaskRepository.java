package io.taskmanager.test;

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
    List<Task> getColumnTasks(int columnId) throws ExecutionException, InterruptedException;


    List<Dev> getProjectDevs(int projectID) throws ExecutionException, InterruptedException;
    Dev getDev(int devID) throws ExecutionException, InterruptedException;

    List<Dev> getTaskDevs(int taskID) throws ExecutionException, InterruptedException;

    Task getTask(Column column) throws ExecutionException, InterruptedException;
}
