package io.taskmanager.api.model;

import java.time.LocalDate;

public abstract class ObjectBaseModel<T> extends BaseModel {


    public ObjectBaseModel(int id, LocalDate updatedAt, LocalDate createdAt) {
        super(id, updatedAt, createdAt);
    }

    public abstract T convert();
}
