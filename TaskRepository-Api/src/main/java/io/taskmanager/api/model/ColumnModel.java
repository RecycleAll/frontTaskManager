package io.taskmanager.api.model;

import java.time.LocalDate;

public class ColumnModel extends BaseModel{

    private final String name;
    private final int project_id;

    public ColumnModel(int id, String name, int project_id, LocalDate updatedAt, LocalDate createdAt) {
        super(id, updatedAt, createdAt);
        this.name = name;
        this.project_id = project_id;
    }
    public String getName() {
        return name;
    }

    public int getProject_id() {
        return project_id;
    }

}
