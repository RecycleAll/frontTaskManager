package io.taskmanager;

import java.util.ArrayList;

public class Project {
    private final int id;
    private String name;
    private String gitHubUrl;

    private final ArrayList<Column> columns;
    private final ArrayList<Tag> tags;
    private final ArrayList<Dev> devs;

    public Project(int id, String name, String gitHubUrl, ArrayList<Column> columns, ArrayList<Tag> tags, ArrayList<Dev> devs) {
        this.id = id;
        setName(name);
        setGitHubUrl(gitHubUrl);
        this.columns = columns;
        this.tags = tags;
        this.devs = devs;
    }

    public Project(int id, String name, String gitHubUrl) {
        this(id, name, gitHubUrl, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public int getId() {
        return id;
    }

    public void addColumn(Column column){
        columns.add(column);
    }

    public void removeColumn(Column column){
        columns.remove(column);
    }

    public void addTag(Tag tag){
        tags.add(tag);
    }

    public void removeTag(Tag tag){
        tags.remove(tag);
    }

    public void addDev(Dev dev){
        devs.add(dev);
    }

    public void removeDev(Dev dev){
        devs.remove(dev);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGitHubUrl() {
        return gitHubUrl;
    }

    public void setGitHubUrl(String gitHubUrl) {
        this.gitHubUrl = gitHubUrl;
    }
}
