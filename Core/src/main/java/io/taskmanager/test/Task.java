package io.taskmanager.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Task {
    private final TaskRepository repository;
    private final int id;
    private String name;
    private String description;

    private LocalDateTime createdAt;
    private LocalDate limitDate;
    private LocalDateTime updatedAt;

    private List<Dev> devs;
    private List<Tag> tags;

    public Task(TaskRepository repository, int id, String name, String description, LocalDateTime createdAt, LocalDate limitDate, LocalDateTime lastUpdateDate, List<Dev> devs, List<Tag> tags) {
        this.id = id;
        this.repository = repository;
        setName(name);
        setDescription(description);
        setCreatedAt(createdAt);
        setLimitDate(limitDate);
        setLastUpdateDate(lastUpdateDate);
        this.devs = devs;
        this.tags = tags;
    }

    public Task(TaskRepository repository,int id, String name, String description, LocalDate limitDate, List<Dev> devs) {
        this(repository,id, name, description, LocalDateTime.now(), limitDate, null, devs, new ArrayList<>());
    }

    public Task(TaskRepository repository,int id, String name, String description, LocalDate limitDate) {
        this(repository,id, name, description, LocalDateTime.now(), limitDate, null, new ArrayList<>(), new ArrayList<>());
    }

    public Task(TaskRepository repository){
        this(repository, 0, "", "", LocalDateTime.now(), null, null, new ArrayList<>(), new ArrayList<>());
    }

    public Task(TaskRepository repository, Task task){
        this(repository, task.getId(), task.getName(), task.getDescription(), task.getCreatedAt(), task.getLimitDate(), task.getLastUpdateDate(), task.getDevs(), task.tags);
    }

    public Task(Task task){
        this(task.repository, task.getId(), task.getName(), task.getDescription(), task.getCreatedAt(), task.getLimitDate(), task.getLastUpdateDate(), task.getDevs(), task.tags);
        System.out.println("dev size from copy: "+ devs.size());
    }

    public void setDevs(List<Dev> devs) {
        this.devs = devs;
    }

    public void updateDevs(List<Dev> newDevs) throws ExecutionException, InterruptedException {
        for (Dev dev: newDevs) {
            if (!devs.contains(dev)){
                addDev(dev);
            }
        }

        for (Dev dev: devs) {
            if (!newDevs.contains(dev)){
                removeDev(dev);
            }
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
        if( repository != null){
            repository.postDevTask(this, dev);
        }
    }

    public void removeDev(Dev dev) throws ExecutionException, InterruptedException {
        if( devs.remove(dev) && repository != null){
            repository.deleteDevTAsk(this, dev);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(LocalDate limitDate) {
        this.limitDate = limitDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return updatedAt;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.updatedAt = lastUpdateDate;
    }

}
