package io.taskmanager.api.model;

import io.taskmanager.test.Column;

import java.time.LocalDate;

public class ColumnModel extends ObjectBaseModel<Column>{

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

    @Override
    public Column convert() {
        return new Column(null, id, name, project_id);
    }
}
