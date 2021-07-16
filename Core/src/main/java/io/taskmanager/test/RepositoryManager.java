package io.taskmanager.test;

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
        }
        return dev;
    }

    public Project getProject(int projectID) throws Exception {
        Project project = projectsManager.getObject(projectID);

        if(project == null){
            project = repository.getProject(projectID);

            Map<Integer, DevStatus> devs = repository.getProjectDevs(projectID);
            for (Map.Entry<Integer, DevStatus> entry: devs.entrySet()) {
                project.addDev( getDev(entry.getKey(), false), entry.getValue());
            }

            List<Column> cols = repository.getColumns(projectID);
            columnsManager.addObject(cols);

            project.setColumns(cols);
            for (Column col: cols) {
                List<Task> columnTasks = repository.getColumnTasks(col.id);
                tasksManager.addObject(columnTasks);
                col.setTasks(columnTasks);

                for (Task task: columnTasks) {
                    List<Integer> devsID = repository.getTaskDevsID(task.id);
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
