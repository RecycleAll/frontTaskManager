package io.taskmanager.test;

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

    public Column() {
        this(-1, "");
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public void removeTask(Task task){
        tasks.remove(task);
    }

    public ArrayList<Task> getTasks(){
        return tasks;
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
