package io.taskmanager.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Task extends ApiRequest{

    private int columnId;
    private String name;
    private String description;

    private LocalDate limitDate;

    private List<Dev> devs;
    private List<Tag> tags;

    public Task(RepositoryManager repository, int id, String name, String description, LocalDate limitDate,  List<Dev> devs, List<Tag> tags) {
        super(repository);
        this.id = id;
        setName(name);
        setDescription(description);
        setLimitDate(limitDate);
        this.devs = devs;
        this.tags = tags;
    }

    public Task(RepositoryManager repository, int id, String name, String description, LocalDate limitDate, List<Dev> devs) {
        this(repository,id, name, description, limitDate,  devs, new ArrayList<>());
    }

    public Task(RepositoryManager repository, int id, String name, String description, LocalDate limitDate) {
        this(repository,id, name, description,  limitDate,  new ArrayList<>(), new ArrayList<>());
    }

    public Task(RepositoryManager repository){
        this(repository, 0, "", "",  null, new ArrayList<>(), new ArrayList<>());
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
        return repositoryManager.getRepository().updateTask(this);
    }

    @Override
    protected boolean myUpdateFromRepo() {
        return false;
    }

    public Task(RepositoryManager repository, Task task){
        this(repository, task.getId(), task.getName(), task.getDescription(),  task.getLimitDate(),  task.getDevs(), task.tags);
    }

    public Task(Task task){
        this(task.repositoryManager, task.getId(), task.getName(), task.getDescription(),  task.getLimitDate(), task.getDevs(), task.tags);
        System.out.println("dev size from copy: "+ devs.size());
    }

    public void setDevs(List<Dev> devs) {
        this.devs = devs;
    }

    public void updateDevs(List<Dev> newDevs) throws ExecutionException, InterruptedException {
        List<Dev> tmp = new ArrayList<>();
        for (Dev dev: newDevs) {
            if (!devs.contains(dev)){
                addDev(dev);
            }
        }

        for (Dev dev: devs) {
            if (!newDevs.contains(dev)){
                tmp.add(dev);
            }
        }

         for (Dev dev: tmp) {
            removeDev(dev);
        }
    }

    public List<Dev> getDevs() {
        return devs;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public int getId() {
        return id;
    }

    public void addTag(Tag tag){
        tags.add(tag);
    }

    public void removeTag(Tag tag){
        tags.remove(tag);
    }

    public void addDev(Dev dev) throws ExecutionException, InterruptedException {
        devs.add(dev);
        if( repositoryManager != null){
            repositoryManager.getRepository().postDevTask(this, dev);
        }
    }

    public void removeDev(Dev dev) throws ExecutionException, InterruptedException {
        if( devs.remove(dev) && repositoryManager != null){
            repositoryManager.getRepository().deleteDevTAsk(this, dev);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public LocalDate getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(LocalDate limitDate) {
        this.limitDate = limitDate;
    }

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }
}
