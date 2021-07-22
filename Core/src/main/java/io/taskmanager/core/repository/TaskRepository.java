package io.taskmanager.core.repository;

import io.taskmanager.core.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface TaskRepository {

    int loginDev(String login, String password) throws ExecutionException, InterruptedException;
    void registerDev(String firstname, String lastname, String email, String password, String githubId) throws ExecutionException, InterruptedException;
    boolean registerDev(Dev dev) throws ExecutionException, InterruptedException;
    boolean updateDev(Dev dev)throws ExecutionException, InterruptedException;
    /**
     * @param devID the dev id
     * @return the map of all project this Dev participate
     */
    Map<Integer, DevStatus> getAllDevProject(int devID) throws ExecutionException, InterruptedException;

    /**
     * @param projectId the project id
     * @return the list of ID of all column of the project id
     */
    List<Integer> getProjectColumns(int projectId) throws ExecutionException, InterruptedException;

    List<Project> getProjects(Dev dev) throws ExecutionException, InterruptedException;

    Project getProject(Project project) throws ExecutionException, InterruptedException;
    Project getProject(int projectID)  throws ExecutionException, InterruptedException;

    List<Column> getColumns(Project project) throws ExecutionException, InterruptedException;
    List<Column> getColumns(int projectID) throws ExecutionException, InterruptedException;
    boolean updateProject(Project project) throws ExecutionException, InterruptedException;
    boolean postProject(Dev dev, Project project) throws ExecutionException, InterruptedException;

    boolean deleteTask(Task task) throws ExecutionException, InterruptedException;
    boolean deleteTask(int taskId) throws ExecutionException, InterruptedException;
    boolean updateTask(Task task) throws ExecutionException, InterruptedException;
    boolean updateTask(int taskId) throws ExecutionException, InterruptedException;
    Task postTask(String name, String description, LocalDate limitDate, int columnId) throws ExecutionException, InterruptedException;
    Task postTask(Task task, int columnId) throws ExecutionException, InterruptedException;
    boolean postTask(Task task) throws ExecutionException, InterruptedException;
    List<Task> getTasks(Project project) throws ExecutionException, InterruptedException;
    List<Task> getColumnTasks(int columnId) throws ExecutionException, InterruptedException;
    Task getTask(Column column) throws ExecutionException, InterruptedException;
    Task getTask(int id) throws ExecutionException, InterruptedException, RepositoryObjectDeleted;


    Map<Integer,DevStatus> getProjectDevs(int projectID) throws ExecutionException, InterruptedException;
    Dev getDev(int devID) throws ExecutionException, InterruptedException, RepositoryObjectDeleted;
    List<Dev> getAllDev() throws ExecutionException, InterruptedException;

    boolean postDevTask(Task task, Dev dev) throws ExecutionException, InterruptedException;
    boolean postDevTask(int taskId, int devId) throws ExecutionException, InterruptedException;
    List<Dev> getTaskDevs(int taskID) throws ExecutionException, InterruptedException;
    List<Integer> getTaskDevsID(int taskID) throws ExecutionException, InterruptedException;
    boolean deleteDevTAsk(Task task, Dev dev) throws ExecutionException, InterruptedException;
    boolean deleteDevTAsk(int taskId, int devId) throws ExecutionException, InterruptedException;



    boolean postParticipate(Project project, Dev dev) throws ExecutionException, InterruptedException;
    boolean postParticipate(int projectID, int devId) throws ExecutionException, InterruptedException;
    boolean deleteParticipate(Project project, Dev dev) throws ExecutionException, InterruptedException;
    boolean deleteParticipate(int projectID, int devId) throws ExecutionException, InterruptedException;





    int postColumn(Column column) throws ExecutionException, InterruptedException;
    Column postColumn(String name, int projectId) throws ExecutionException, InterruptedException;
    Column getColumn(Column column) throws ExecutionException, InterruptedException;
    Column getColumn(int id) throws ExecutionException, InterruptedException;
    boolean deleteColumn(Column column) throws ExecutionException, InterruptedException;
    boolean putColumn(Column column) throws ExecutionException, InterruptedException;

}
