package io.taskmanager.test;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class Project extends ApiRequest{

    private String name;
    private String gitHubUrl;

    private List<Column> columns;
    private List<Tag> tags;
    private Map<Dev, DevStatus> devs;

    public static Project loadFromApi(TaskRepository api, int projectID) throws Exception {
        return api.getProject(projectID);
    }

    public Project(RepositoryManager repository, int id, String name, String gitHubUrl, List<Column> columns, List<Tag> tags, Map<Dev, DevStatus> devs) {
        super(repository);
        this.id = id;
        setName(name);
        setGitHubUrl(gitHubUrl);
        this.columns = columns;
        this.tags = tags;
        this.devs = devs;
    }

    public Project(RepositoryManager repository, int id, String name, String gitHubUrl) {
        this(repository, id, name, gitHubUrl, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    public Project(RepositoryManager repository) {
        this(repository, -1, "", "");
    }

    @Override
    protected boolean myPost() throws ExecutionException, InterruptedException {
        return false;
    }

    @Override
    protected boolean myDelete() throws ExecutionException, InterruptedException {
        return false;
    }

    @Override
    protected boolean myUpdateToRepo() throws ExecutionException, InterruptedException {
        return false;
    }

    @Override
    protected boolean myUpdateFromRepo() {
        return false;
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

    public Column addNewColumn(String name) throws ExecutionException, InterruptedException {
        System.out.println("add new COl: "+ name);
        if( repositoryManager != null) {
            System.out.println("add new COl repo: "+ name);
            Column col = repositoryManager.getRepository().postColumn(name, id);
            columns.add(col);
            return col;
        }
        return null;
    }

    public void addColumn(Column column) throws ExecutionException, InterruptedException {
        System.out.println("addCOl"+ columns +" "+ column);
        if( column.getProjectId() == ApiRequest.undefinedID && !columns.contains(column) ) {
            column.setProjectId(this.id);
            columns.add(column);
        }
    }

    public void removeColumn(Column column) throws ExecutionException, InterruptedException {
        columns.remove(column);
        System.out.println("removeColumn: "+ column.getName());
        column.deleteFromRepo();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public void addDev(Dev dev) throws ExecutionException, InterruptedException {
        if( !devs.containsKey(dev)) {
            devs.put(dev, DevStatus.DEV);
            dev.addProject(this);
            if( repositoryManager != null){
                repositoryManager.getRepository().postParticipate(this, dev);
            }
        }
    }

    public void updateDevs(List<Dev> newDevs) throws ExecutionException, InterruptedException {
        List<Dev> tmp = new ArrayList<>();
        for (Dev dev: newDevs) {
            if (!devs.containsKey(dev)){
                addDev(dev);
            }
        }

        for (Dev dev : devs.keySet()) {
            if (!newDevs.contains(dev)) {
                tmp.add(dev);
            }
        }

        for (Dev dev: tmp ) {
            removeDev(dev);
        }
    }

    public void addDev(Dev dev, DevStatus devStatus) {
        devs.put(dev, devStatus);
    }

    public void removeDev(Dev dev) throws ExecutionException, InterruptedException {
        if( devs.remove(dev) != null){
            dev.removeProject(this);
            if( repositoryManager != null){
                repositoryManager.getRepository().deleteParticipate(this, dev);
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

    public RepositoryManager getRepository() {
        return repositoryManager;
    }

}

