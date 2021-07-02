package io.taskmanager.test;

import java.util.ArrayList;

public class RepositoryManager {

    private final TaskRepository repository;
    private final ArrayList<Dev> devs;
    private final ArrayList<Task> tasks;
    private final ArrayList<Project> projects;
    private final ArrayList<Column> columns;

    public RepositoryManager(TaskRepository repository) {
        this.repository = repository;
        devs = new ArrayList<>();
        tasks = new ArrayList<>();
        projects = new ArrayList<>();
        columns = new ArrayList<>();
    }









}
