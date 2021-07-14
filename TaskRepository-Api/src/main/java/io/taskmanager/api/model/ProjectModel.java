package io.taskmanager.api.model;

public class ProjectModel extends BaseModel{

    private int id;
    private String name;

    public ProjectModel(int id) {
        super(id, null, null);
    }

    public String getName() {
        return name;
    }
}
