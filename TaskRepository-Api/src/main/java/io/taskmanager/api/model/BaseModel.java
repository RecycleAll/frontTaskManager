package io.taskmanager.api.model;

import java.time.LocalDate;

public abstract class BaseModel {

    protected final int id;
    protected final LocalDate updatedAt;
    protected final LocalDate createdAt;

    public BaseModel(int id, LocalDate updatedAt, LocalDate createdAt) {
        this.id = id;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }


}
