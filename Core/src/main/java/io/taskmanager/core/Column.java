package io.taskmanager.core;

import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryEditionConflict;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.repository.RepositoryObjectDeleted;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Column extends RepositoryObject<Column> {

    private int projectId;
    private String name;

    private List<Task> tasks;

    public Column(RepositoryManager repository, int id, String name, int projectId, List<Task> tasks){
        super(repository);
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.tasks = tasks;
        try {
            updateToRepo();
        } catch (ExecutionException | InterruptedException | RepositoryEditionConflict | RepositoryObjectDeleted e) {
            e.printStackTrace();
        }
    }

    public Column(RepositoryManager repository, int id, String name, int projectId)  {
        this(repository, id, name, projectId, new ArrayList<>());
    }

    public Column( int id, String name, int projectId) {
        this(null, id, name, projectId, new ArrayList<>());
    }

    public Column(RepositoryManager repository, Column col){
        this(repository, col.getId(), col.getName(), col.projectId, col.getTasks() );
    }

    public Column(RepositoryManager repository) {
        this(repository, -1, "", -1, new ArrayList<>());
    }

    @Override
    public boolean compare(Column other) {
        return false;
    }

    @Override
    public boolean isConflict(Column other) {
        return !compare(other) &&
                updatedAt.isBefore(other.updatedAt);
    }

    @Override
    protected boolean myPost() throws ExecutionException, InterruptedException {
        return repositoryManager.getRepository().postColumn(this) != RepositoryObject.undefinedID;
    }

    @Override
    protected boolean myDelete() throws ExecutionException, InterruptedException {
        return repositoryManager.getRepository().deleteColumn(this);
    }

    @Override
    protected boolean myUpdateToRepo(boolean force) throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted {
        if(!edited){
            System.out.println("Column:myUpdateToRepo -> not edited");
            return true;
        }else{
            Column column = repositoryManager.getRepository().getColumn(id);
            //System.out.println("Column: myUpdateToRepo ->\nlocal: "+updatedAt+"\nrepo: "+column.updatedAt);
            if( column == null){
                System.out.println("Column:myUpdateToRepo -> deleted");
                throw new RepositoryObjectDeleted(this);
            }else if(!force && isConflict(column)){
                System.out.println("Column:myUpdateToRepo -> conflict\blocal: "+updatedAt+"\nrepo: "+column.updatedAt);
                throw new RepositoryEditionConflict( new RepositoryConflictHandler<Column>(this, column, repositoryManager));
            }else{
                System.out.println("Column:myUpdateToRepo -> no conflict (f:"+force+")");
                return repositoryManager.getRepository().putColumn(this);
            }
        }
    }

    @Override
    protected boolean myUpdateFromRepo() throws ExecutionException, InterruptedException {
        Column col = repositoryManager.getRepository().getColumn(id);
        return false;
    }

    @Override
    public Column merge(Column column) {
        if(id != column.id){
            return null;
        }else {
            Column col = new Column(null);

            if (name.equals(column.name)) {
                col.setName(name);
            }

            return col;
        }
    }

    public void removeDevFromAllTask(Dev dev) throws ExecutionException, InterruptedException, RepositoryObjectDeleted {
        for (Task task: tasks) {
            task.removeDev(dev);
        }
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) throws ExecutionException, InterruptedException, RepositoryObjectDeleted {

        if(!tasks.contains(task)){
            if( repositoryManager != null){
                Column column = repositoryManager.getRepository().getColumn(id);

                if( column == null){
                    throw  new RepositoryObjectDeleted(this);
                }

                tasks.add(task);
                repositoryManager.getRepository().postTask(task, id);
            }else{
                tasks.add(task);
            }
        }
    }

    public void addTask(List<Task> tasks) throws ExecutionException, InterruptedException, RepositoryObjectDeleted {
        for (Task task: tasks) {
            addTask(task);
        }
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
        edited = true;
    }

    public Task addNewTask(String name, String description, LocalDate limitDate) throws ExecutionException, InterruptedException {
        Task task = repositoryManager.getRepository().postTask(name, description, limitDate, id);
        tasks.add(task);
        return task;
    }

    public void removeTask(Task task) throws ExecutionException, InterruptedException {
        tasks.remove(task);
        if( repositoryManager != null){
            repositoryManager.getRepository().deleteTask(task);
        }
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setAll(Column col) {
        name = col.getName();
    }

    public int getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public RepositoryManager getRepository() {
        return repositoryManager;
    }

    public void setName(String name){
        this.name = name;
        edited = true;
    }

}
