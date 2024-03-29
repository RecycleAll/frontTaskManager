package io.taskmanager.api.model;

import io.taskmanager.core.Task;

import java.time.LocalDate;

public class TaskModel extends ObjectBaseModel<Task> {

    private final int column_id;
    private final String name;
    private final String description;
    private final LocalDate limitDate;

    public TaskModel(int id, int column_id, String name, String description, LocalDate limitDate) {
        super(id, null, null);
        this.column_id = column_id;
        this.name = name;
        this.description = description;
        this.limitDate = limitDate;
    }

    public int getColumn_id() {
        return column_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getLimitDate() {
        return limitDate;
    }

    @Override
    public Task convert() {
        Task task = new Task(null, id, name, description, limitDate);
        task.setUpdatedAt(updatedAt);
        return task;
    }
}
