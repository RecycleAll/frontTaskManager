package io.taskmanager.core.repository;

import io.taskmanager.core.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RepositoryManager {

    private TaskRepository repository;
    private final RepositoryObjectManager<Dev> devsManager;
    private final RepositoryObjectManager<Task> tasksManager;
    private final RepositoryObjectManager<Project> projectsManager;
    private final RepositoryObjectManager<Column> columnsManager;

    public RepositoryManager(TaskRepository repository) {
        this.repository = repository;
        devsManager = new RepositoryObjectManager<>(repository);
        tasksManager = new RepositoryObjectManager<>(repository);
        projectsManager = new RepositoryObjectManager<>(repository);
        columnsManager = new RepositoryObjectManager<>(repository);
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
        Column col = columnsManager.getObject(id);
        if(col == null){
            col = repository.getColumn(id);



        }
        return col;
    }

    public void removeColumn(Column column) throws ExecutionException, InterruptedException {
        for (Project project: projectsManager.getList()) {
            project.removeColumn(column);
        }
    }

    public Task getTask(int id) throws ExecutionException, InterruptedException{
        Task obj = tasksManager.getObject(id);
        if(obj == null){
            obj = repository.getTask(id);


        }
        return obj;
    }

    public Dev getDev(int id, boolean loadProject) throws Exception {
        Dev dev = devsManager.getObject(id);
        if(dev == null){
            dev = repository.getDev(id);
            devsManager.addObject(dev);
            if(loadProject) {
                Map<Integer, DevStatus> projects = repository.getAllDevProject(id);
                if (projects != null) {
                    for (Map.Entry<Integer, DevStatus> entry : projects.entrySet()) {
                        Project project = getProject(entry.getKey());
                        dev.addProject(project);
                    }
                } else {
                    // TODO
                }
            }
            dev.setRepositoryManager(this);
        }
        return dev;
    }

    public List<Dev> getAllDev() throws ExecutionException, InterruptedException {
        List<Dev> devs = repository.getAllDev();
        devsManager.addObject(devs);
        return devsManager.getList();
    }

    public void removeDev(Dev dev) throws ExecutionException, RepositoryObjectDeleted, InterruptedException {
        for (Project project: projectsManager.getList()) {
            project.removeDev(dev);
        }
    }

    public void removeDev(List<Dev> devs) throws ExecutionException, RepositoryObjectDeleted, InterruptedException {
        for (Dev dev: devs){
            removeDev(dev);
        }
    }

    public Project getProject(int projectID) throws Exception {
        Project project = projectsManager.getObject(projectID);

        if(project == null){
            project = repository.getProject(projectID);
            projectsManager.addObject(project);

            Map<Integer, DevStatus> devs = repository.getProjectDevs(projectID);
            for (Map.Entry<Integer, DevStatus> entry: devs.entrySet()) {
                project.addDev( getDev(entry.getKey(), false), entry.getValue());
            }

            List<Column> cols = repository.getColumns(projectID);
            columnsManager.addObject(cols);

            project.setColumns(cols);
            for (Column col: cols) {
                List<Task> columnTasks = repository.getColumnTasks(col.getId());
                tasksManager.addObject(columnTasks);
                col.setTasks(columnTasks);

                for (Task task: columnTasks) {
                    List<Integer> devsID = repository.getTaskDevsID(task.getId());
                    System.out.println(devsID.toString());
                    for (Integer id: devsID) {
                        task.addDev( getDev(id, false));
                    }
                    task.setRepositoryManager(this);
                }
                col.setRepositoryManager(this);
            }
            System.out.println("loaded project: "+ project+"  devs:"+devs.size()+"  col: "+cols.size());

            project.setRepositoryManager(this);
            for (Dev dev: project.getDevs()) {
                dev.setRepositoryManager(this);
            }
        }

        return project;
    }

}
