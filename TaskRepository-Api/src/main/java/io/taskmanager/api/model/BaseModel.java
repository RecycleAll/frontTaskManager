package io.taskmanager.api.model;

import java.time.LocalDateTime;

public abstract class BaseModel {

    protected final int id;
    protected final LocalDateTime updatedAt;
    protected final LocalDateTime createdAt;

    public BaseModel(int id, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.id = id;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


}
