package io.taskmanager.test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Task {
    private final int id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime limitDate;
    private LocalDateTime lastUpdateDate;

    private final ArrayList<Dev> devs;
    private final ArrayList<Tag> tags;

    public Task(int id, String name, String description, LocalDateTime creationDate, LocalDateTime limitDate, LocalDateTime lastUpdateDate, ArrayList<Dev> devs, ArrayList<Tag> tags) {
        this.id = id;
        setName(name);
        setDescription(description);
        setCreationDate(creationDate);
        setLimitDate(limitDate);
        setLastUpdateDate(lastUpdateDate);
        this.devs = devs;
        this.tags = tags;
    }

    public Task(int id, String name, String description, LocalDateTime limitDate, ArrayList<Dev> devs) {
        this(id, name, description, LocalDateTime.now(), limitDate, null, devs, new ArrayList<>());
    }

    public Task(int id, String name, String description, LocalDateTime limitDate) {
        this(id, name, description, LocalDateTime.now(), limitDate, null, new ArrayList<>(), new ArrayList<>());
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(LocalDateTime limitDate) {
        this.limitDate = limitDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public String toString(){
        return name;
    }
}
