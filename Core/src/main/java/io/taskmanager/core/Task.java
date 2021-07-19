package io.taskmanager.core;

import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryEditionConflict;
import io.taskmanager.core.repository.RepositoryManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Task extends RepositoryObject<Task> {

    private String name;
    private String description;

    private LocalDate limitDate;

    private List<Dev> devs;
    private List<Tag> tags;

    public Task(RepositoryManager repository, int id, String name, String description, LocalDate limitDate, List<Dev> devs, List<Tag> tags) {
        super(repository);
        this.id = id;
        this.name = name;
        this.description = description;
        this.limitDate = limitDate;
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

    public Task(){
        this((RepositoryManager) null);
    }

    @Override
    public boolean isConflict(Task other) {
        return !compare(other) &&
                updatedAt.isBefore(other.updatedAt);
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
    protected boolean myUpdateToRepo(boolean force) throws ExecutionException, InterruptedException, RepositoryEditionConflict {
        if(!edited){
            System.out.println("Task:myUpdateToRepo -> not edited");
            return true;
        }else{
            Task task = repositoryManager.getRepository().getTask(id);
            System.out.println("Task: myUpdateToRepo ->\nlocal: "+updatedAt+"\nrepo: "+task.updatedAt);
            if(!force && isConflict(task)){
                System.out.println("Task:myUpdateToRepo ->conflict");
                throw new RepositoryEditionConflict( new RepositoryConflictHandler<Task>(this, task, repositoryManager));
            }else{
                System.out.println("Task:myUpdateToRepo -> no conflict (f:"+force+")");
                return repositoryManager.getRepository().updateTask(this);
            }
        }
    }

    @Override
    protected boolean myUpdateFromRepo() {
        return false;
    }

    public boolean compare(Task task){
        if(devs != null && task.devs != null){
            if( devs.size() != task.devs.size()){
                return false;
            }else {
                if( devs.stream().anyMatch(dev -> task.devs.stream().anyMatch(dev1 -> dev.id != dev1.id)) ){
                    return false;
                }
            }
        }else if(devs != task.devs){
            return false;
        }

        return  name.equals(task.name) &&
                description.equals(task.description) &&
                limitDate.isEqual(task.limitDate);
    }

    @Override
    public Task merge(Task task){
        if(id != task.id){
            return null;
        }else {
            Task mergedTask = new Task();
            mergedTask.setId(id);

            if (name.equals(task.name))
                mergedTask.setName(name);

            if (description.equals(task.description))
                mergedTask.setDescription(description);

            if (limitDate.isEqual(task.limitDate))
                mergedTask.setLimitDate(limitDate);

            return mergedTask;
        }
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

    @Override
    public void setAll(Task task) {
        id = task.id;
        name = task.name;
        description = task.description;
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
        edited = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        edited = true;
    }


    public LocalDate getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(LocalDate limitDate){
        this.limitDate = limitDate;
        edited = true;
    }
}
