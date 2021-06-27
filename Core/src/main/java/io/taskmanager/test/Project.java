package io.taskmanager.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Project {

    private final TaskRepository repository;
    private final int id;
    private String name;
    private String gitHubUrl;

    private List<Column> columns;
    private List<Tag> tags;
    private Map<Dev, DevStatus> devs;

    public static Project loadFromApi(TaskRepository api, int projectID) throws Exception {
        return api.getProject(projectID);
    }

    public Project(TaskRepository repository, int id, String name, String gitHubUrl, List<Column> columns, List<Tag> tags, Map<Dev, DevStatus> devs) {
        this.repository = repository;
        this.id = id;
        setName(name);
        setGitHubUrl(gitHubUrl);
        this.columns = columns;
        this.tags = tags;
        this.devs = devs;
    }

    public Project(TaskRepository repository, int id, String name, String gitHubUrl) {
        this(repository, id, name, gitHubUrl, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    public Project(TaskRepository repository) {
        this(repository, -1, "", "");
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setDevs(Map<Dev, DevStatus> devs) {
        System.out.println("setDevs: " + devs.size());
        this.devs = devs;
    }

    public List<Dev> getDevs() {
        return new ArrayList<>(devs.keySet());
        // return null;
    }

    public DevStatus getDevStatus(Dev dev){
        return devs.get(dev);
    }

    public void setDevStatus(Dev dev, DevStatus status){
        devs.put(dev, status);
    }

    public List<Column> getColumns() {
        return columns;
        //return null;
    }

    public int getId() {
        return id;
    }

    public void addColumn(Column column) throws ExecutionException, InterruptedException {
        System.out.println("addCOl"+ columns +" "+ column);
        columns.add(column);
        if( repository != null){
            repository.postColumn(column.getName(), this.id);
        }
    }

    public void removeColumn(Column column) throws ExecutionException, InterruptedException {
        columns.remove(column);
        if( repository != null){
            repository.deleteColumn(column);
        }
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public void addDev(Dev dev) {
        if( !devs.containsKey(dev)) {
            devs.put(dev, DevStatus.DEV);
            dev.addProject(this);
        }
    }

    public void addDev(Dev dev, DevStatus devStatus) {
        devs.put(dev, devStatus);
    }

    public void removeDev(Dev dev) throws ExecutionException, InterruptedException {
        if( devs.remove(dev) != null){
            dev.removeProject(this);
            if( repository != null){
                repository.deleteParticipate(this, dev);
            }
            for (Column col : columns) {
                col.removeDevFromAllTask(dev);
            }
        }

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

    public TaskRepository getRepository() {
        return repository;
    }
}

