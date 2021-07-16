package io.taskmanager.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Column extends ApiRequest{

    private int projectId;
    private String name;

    private List<Task> tasks;

    public Column(RepositoryManager repository, int id, String name, int projectId, List<Task> tasks){
        super(repository);
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.tasks = tasks;
    }

    public Column(RepositoryManager repository, int id, String name, int projectId){
        this(repository, id, name, projectId, new ArrayList<>());
    }

    public Column(RepositoryManager repository, Column col){
        this(repository, col.getId(), col.getName(), col.projectId, col.getTasks() );
    }

    public Column(RepositoryManager repository){
        this(repository, -1, "", -1, null);
    }

    @Override
    protected boolean myPost() throws ExecutionException, InterruptedException {
        return repositoryManager.getRepository().postColumn(this) != ApiRequest.undefinedID;
    }

    @Override
    protected boolean myDelete() throws ExecutionException, InterruptedException {
        return repositoryManager.getRepository().deleteColumn(this);
    }

    @Override
    protected boolean myUpdateToRepo() throws ExecutionException, InterruptedException {
        return repositoryManager.getRepository().putColumn(this);
    }

    @Override
    protected boolean myUpdateFromRepo() {
        return false;
    }

    public void removeDevFromAllTask(Dev dev) throws ExecutionException, InterruptedException {
        for (Task task: tasks) {
            task.removeDev(dev);
        }
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) throws ExecutionException, InterruptedException {
        tasks.add(task);
        if( repositoryManager != null){
            repositoryManager.getRepository().postTask(task, id);
        }
    }

    public void addTask(List<Task> tasks) throws ExecutionException, InterruptedException {
        for (Task task: tasks) {
            addTask(task);
        }
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Task addNewTask(String name, String description, LocalDate limitDate) throws ExecutionException, InterruptedException {
        Task task = repositoryManager.getRepository().postTask(name, description, limitDate, id);
        tasks.add(task);
        return task;
    }

    public void removeTask(Task task) throws ExecutionException, InterruptedException {
        tasks.remove(task);
        if( repositoryManager != null){
            repositoryManager.getRepository().deleteTask(task);
        }
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public int getId() {
        return id;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public RepositoryManager getRepository() {
        return repositoryManager;
    }

    public void setName(String name) throws ExecutionException, InterruptedException {
        this.name = name;
        System.out.println("column set name"+this.id);
        if(repositoryManager != null){
            System.out.println("repo");
            repositoryManager.getRepository().putColumn(this);
        }
    }

}
