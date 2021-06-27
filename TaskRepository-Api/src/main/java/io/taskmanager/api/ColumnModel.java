package io.taskmanager.api;

public class ColumnModel {

    private final int id;
    private final String name;
    private final int project_id;
    private final String updatedAt;
    private final String createdAt;

    public ColumnModel(int id, String name, int project_id, String updatedAt, String createdAt) {
        this.id = id;
        this.name = name;
        this.project_id = project_id;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getProject_id() {
        return project_id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
