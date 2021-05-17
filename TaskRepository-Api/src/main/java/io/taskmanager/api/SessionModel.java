package io.taskmanager.api;

public class SessionModel {

    private int id;
    private String token;
    private int dev_id;

    public SessionModel() {
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public int getDev_id() {
        return dev_id;
    }
}

