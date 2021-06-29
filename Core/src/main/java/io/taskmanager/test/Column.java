package io.taskmanager.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Column {

    private final TaskRepository repository;
    private final int id;
    private String name;

    private List<Task> tasks;

    public Column(TaskRepository repository, int id, String name, List<Task> tasks) throws ExecutionException, InterruptedException {
        this.repository = repository;
        this.id = id;
        this.name = name;
        this.tasks = tasks;
    }

    public Column(TaskRepository repository, int id, String name) throws ExecutionException, InterruptedException {
        this(repository, id, name, new ArrayList<>());
    }

    public Column(TaskRepository repository, Column col) throws ExecutionException, InterruptedException {
        this(repository, col.getId(), col.getName(), col.getTasks());
    }

    public Column(TaskRepository repository) throws ExecutionException, InterruptedException {
        this(repository, -1, "");
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

    public Task addNewTask(String name, String description, LocalDate limitDate) throws ExecutionException, InterruptedException {
        Task task = repository.postTask(name, description, limitDate, id);
        tasks.add(task);
        return task;
    }

    public void removeTask(Task task){
        tasks.remove(task);
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public int getId() {
        return id;
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
