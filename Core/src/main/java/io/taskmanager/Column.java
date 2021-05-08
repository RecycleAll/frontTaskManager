package io.taskmanager;

import java.util.ArrayList;

public class Column {
    private final int id;
    private String name;

    private final ArrayList<Task> tasks;

    public Column(int id, String name, ArrayList<Task> tasks) {
        this.id = id;
        setName(name);
        this.tasks = tasks;
    }

    public Column(int id, String name) {
        this(id, name, new ArrayList<>());
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public void removeTask(Task task){
        tasks.remove(task);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
