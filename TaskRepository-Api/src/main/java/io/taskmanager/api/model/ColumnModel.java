package io.taskmanager.api.model;

import io.taskmanager.core.Column;

import java.time.LocalDateTime;

public class ColumnModel extends ObjectBaseModel<Column> {

    private final String name;
    private final int project_id;

    public ColumnModel(int id, String name, int project_id, LocalDateTime updatedAt, LocalDateTime createdAt) {
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
        Column col = new Column(id, name, project_id);
        col.setUpdatedAt(updatedAt);
        return col;
    }
}
