package io.taskmanager.api;

public class Participates {

    private int id;
    private int dev_id;
    private int project_id;
    private boolean owner;

    public Participates() {
    }

    public int getId() {
        return id;
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

