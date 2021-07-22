package io.taskmanager.api.model;

import java.time.LocalDateTime;

public abstract class ObjectBaseModel<T> extends BaseModel {


    public ObjectBaseModel(int id, LocalDateTime updatedAt, LocalDateTime createdAt) {
        super(id, updatedAt, createdAt);
    }

    public abstract T convert();
}
