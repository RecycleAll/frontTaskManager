package io.taskmanager.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Column extends ApiRequest{

    private int projectId;
    private String name;

    private List<Task> tasks;

    public Column(TaskRepository repository, int id, String name, int projectId, List<Task> tasks) throws ExecutionException, InterruptedException {
        super(repository);
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.tasks = tasks;
    }

    public Column(TaskRepository repository, int id, String name, int projectId) throws ExecutionException, InterruptedException {
        this(repository, id, name, projectId, new ArrayList<>());
    }

    public Column(TaskRepository repository, Column col) throws ExecutionException, InterruptedException {
        this(repository, col.getId(), col.getName(), col.projectId, col.getTasks() );
    }

    public Column(TaskRepository repository) throws ExecutionException, InterruptedException {
        this(repository, -1, "", -1);
    }

    @Override
    protected boolean myPost() throws ExecutionException, InterruptedException {
        return repository.postColumn(this) != ApiRequest.undefinedID;
    }

    @Override
    protected boolean myDelete() throws ExecutionException, InterruptedException {
        return repository.deleteColumn(this);
    }

    @Override
    protected boolean myUpdateToRepo() throws ExecutionException, InterruptedException {
        return repository.putColumn(this);
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

    public void addTask(Task task){
        tasks.add(task);
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Task addNewTask(String name, String description, LocalDate limitDate) throws ExecutionException, InterruptedException {
        Task task = repository.postTask(name, description, limitDate, id);
        tasks.add(task);
        return task;
    }

    public void removeTask(Task task) throws ExecutionException, InterruptedException {
        tasks.remove(task);
        if( repository != null){
            repository.deleteTask(task);
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

    public TaskRepository getRepository() {
        return repository;
    }

    public void setName(String name) throws ExecutionException, InterruptedException {
        this.name = name;
        System.out.println("column set name"+this.id);
        if(repository != null){
            System.out.println("repo");
            repository.putColumn(this);
        }
    }

}
