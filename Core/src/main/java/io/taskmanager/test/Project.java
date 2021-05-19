package io.taskmanager.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Project {

    private TaskRepository api;

    private final int id;
    private String name;
    private String gitHubUrl;

    private List<Column> columns;
    private List<Tag> tags;
    private List<Dev> devs;

    public static Project loadFromApi(TaskRepository api, int projectID) throws Exception {
        return api.getProject(projectID);
    }

    public Project(TaskRepository api, int id, String name, String gitHubUrl, List<Column> columns, List<Tag> tags, List<Dev> devs) {
        this.api = api;
        this.id = id;
        setName(name);
        setGitHubUrl(gitHubUrl);
        this.columns = columns;
        this.tags = tags;
        this.devs = devs;
    }

    public Project(TaskRepository api, int id, String name, String gitHubUrl) {
        this(api, id, name, gitHubUrl, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public Project(TaskRepository api) {
        this(api, -1, "", "");
    }

    public Project() {
        this(null, -1, "", "", null, null, null);
    }

    public void setApi(TaskRepository api) {
        this.api = api;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;

       /* for (Column col: columns ) {
            for (Task task: col.getTasks() ) {
                this.devs.addAll( task.getDevs());
                //this.tags.addAll( task.getTags());
            }
        }*/
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setDevs(List<Dev> devs) {
        System.out.println("setDevs: "+devs.size());
        this.devs = devs;
    }

    public List<Dev> getDevs() {
        return devs;
       // return null;
    }

    public List<Column> getColumns() {
        return columns;
        //return null;
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

