package io.taskmanager.api.model;

public class Participates extends BaseModel {

    private int dev_id;
    private int project_id;
    private boolean owner;

    public Participates() {
        super(-1, null, null);
    }

    public int getDev_id() {
        return dev_id;
    }

    public int getProject_id() {
        return project_id;
    }

    public boolean isOwner() {
        return owner;
    }
}

