package io.taskmanager.test;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RepositoryManager {

    private TaskRepository repository;
    private final RepositoryObjectManager<Dev> devs;
    private final RepositoryObjectManager<Task> tasks;
    private final RepositoryObjectManager<Project> projects;
    private final RepositoryObjectManager<Column> columns;

    public RepositoryManager(TaskRepository repository) {
        this.repository = repository;
        devs = new RepositoryObjectManager<>(repository);
        tasks = new RepositoryObjectManager<>(repository);
        projects = new RepositoryObjectManager<>(repository);
        columns = new RepositoryObjectManager<>(repository);
    }

    public RepositoryManager(){
        this(null);
    }

    public void setRepository(TaskRepository repository) {
        if(this.repository == null)
            this.repository = repository;
    }

    public TaskRepository getRepository() {
        return repository;
    }



    public Column getColumn(int id) throws ExecutionException, InterruptedException {
        Column col = columns.getObject(id);
        if(col == null){
            col = repository.getColumn(id);



        }
        return col;
    }

    public Task getTask(int id) throws ExecutionException, InterruptedException{
        Task obj = tasks.getObject(id);
        if(obj == null){
            obj = repository.getTask(id);


        }
        return obj;
    }

    public Dev getDev(int id) throws Exception {
        Dev obj = devs.getObject(id);
        if(obj == null){
            obj = repository.getDev(id);

            Map<Integer, DevStatus> projects = repository.getAllDevProject(id);
            if(projects != null){
                for (Map.Entry<Integer, DevStatus> entry: projects.entrySet()) {
                    Project project = getProject( entry.getKey());
                    obj.addProject(project);
                }
            }else{
                // TODO
            }
        }
        return obj;
    }

    public Project getProject(int id) throws Exception {
        Project obj = projects.getObject(id);
        if(obj == null){
            obj = repository.getProject(id);
        }
        return obj;
    }

}
