package io.taskmanager.api.model;

import io.taskmanager.core.Project;

public class ProjectModel extends ObjectBaseModel<Project>{

    private String name;

    public ProjectModel(int id) {
        super(id, null, null);
    }

    public String getName() {
        return name;
    }

    @Override
    public Project convert() {
        return new Project(null, id, name, "");
    }
}
