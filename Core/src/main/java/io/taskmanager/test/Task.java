package io.taskmanager.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Task {
    private final int id;
    private String name;
    private String description;

    private LocalDateTime createdAt;
    private LocalDate limitDate;
    private LocalDateTime updatedAt;

    private ArrayList<Dev> devs;
    private ArrayList<Tag> tags;

    public Task(int id, String name, String description, LocalDateTime createdAt, LocalDate limitDate, LocalDateTime lastUpdateDate, ArrayList<Dev> devs, ArrayList<Tag> tags) {
        this.id = id;
        setName(name);
        setDescription(description);
        setCreatedAt(createdAt);
        setLimitDate(limitDate);
        setLastUpdateDate(lastUpdateDate);
        this.devs = devs;
        this.tags = tags;
    }

    public Task(int id, String name, String description, LocalDate limitDate, ArrayList<Dev> devs) {
        this(id, name, description, LocalDateTime.now(), limitDate, null, devs, new ArrayList<>());
    }

    public Task(int id, String name, String description, LocalDate limitDate) {
        this(id, name, description, LocalDateTime.now(), limitDate, null, new ArrayList<>(), new ArrayList<>());
    }

    public Task(){
        this(0, "", "", null, null, null, null, null);
    }

    public Task(Task task){
        this(task.getId(), task.getName(), task.getDescription(), task.getCreatedAt(), task.getLimitDate(), task.getLastUpdateDate(), task.getDevs(), task.tags);
        System.out.println("dev size from copy: "+ devs.size());
    }

    public void setDevs(ArrayList<Dev> devs) {
        this.devs = devs;
    }

    public ArrayList<Dev> getDevs() {
        return devs;
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

    @Override
    public String toString(){
        return name;
    }
}
