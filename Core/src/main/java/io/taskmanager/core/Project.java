package io.taskmanager.core;

import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryEditionConflict;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.repository.RepositoryObjectDeleted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Project extends RepositoryObject<Project> {

    private String name;
    private String gitHubUrl;

    private List<Column> columns;
    private List<Tag> tags;
    private Map<Dev, DevStatus> devs;

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
    public boolean compare(Project project) {
        return super.compare((RepositoryObject<Project>) project) &&
                name.equals(project.name) &&
                gitHubUrl.equals(project.gitHubUrl);
    }

    @Override
    public boolean isConflict(Project project) {
        return !compare(project) &&
                updatedAt.isBefore(project.updatedAt);
    }

    @Override
    protected boolean myPost() throws ExecutionException, InterruptedException {

        if (devs.isEmpty()) {
            return false;
        }

        Dev owner = null;

        for (Dev dev : devs.keySet()) {
            dev.postToRepo();
            if (owner == null && devs.get(dev) == DevStatus.OWNER) {
                owner = dev;
            }
        }

        return repositoryManager.getRepository().postProject(owner, this);
    }

    @Override
    protected boolean myDelete() throws ExecutionException, InterruptedException {
        return this.repositoryManager.getRepository().deleteProject(this);
    }

    @Override
    protected boolean myUpdateToRepo(boolean force) throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted {
        if (!edited) {
            //System.err.println("Project:myUpdateToRepo -> not edited");
            return true;
        } else {
            Project project = repositoryManager.getRepository().getProject(id);
            if (project == null) {
                throw new RepositoryObjectDeleted(this);
            }
            //System.err.println("Project: myUpdateToRepo ->\nlocal: " + updatedAt + "\nrepo: " + project.updatedAt);
            if (!force && isConflict(project)) {
                //System.err.println("Project:myUpdateToRepo ->conflict");
                throw new RepositoryEditionConflict(new RepositoryConflictHandler<>(this, project, repositoryManager));
            } else {
                //System.err.println("Project:myUpdateToRepo -> no conflict (f:" + force + ")");
                edited = false;
                return repositoryManager.getRepository().updateProject(this);
            }
        }
    }

    @Override
    protected boolean myUpdateFromRepo() throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted {
        Project project = repositoryManager.getRepository().getProject(id);
        if (edited) {
            //System.err.println("Project:myUpdateFromRepo -> edited");
            throw new RepositoryEditionConflict(new RepositoryConflictHandler<>(this, project, repositoryManager));
        } else if (project == null) {
            //System.err.println("Project:myUpdateFromRepo -> deleted");
            throw new RepositoryObjectDeleted(this);
        } else {
            //System.err.println("Project:myUpdateFromRepo -> no conflict");
            setAll(project);
            edited = false;
            return true;
        }
    }

    @Override
    public Project merge(Project project) {
        if (id != project.id) {
            return null;
        } else {
            Project mergedProject = new Project(null);
            mergedProject.setId(id);

            if (name.equals(project.name))
                mergedProject.setName(name);

            if (gitHubUrl.equals(project.gitHubUrl))
                mergedProject.setGitHubUrl(gitHubUrl);

            return mergedProject;
        }
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void setDevs(Map<Dev, DevStatus> devs) {
        //System.err.println("setDevs: " + devs.size());
        this.devs = devs;
    }

    public List<Dev> getDevs() {
        return new ArrayList<>(devs.keySet());
        // return null;
    }

    public DevStatus getDevStatus(Dev dev) {
        return devs.get(dev);
    }

    public void setDevStatus(Dev dev, DevStatus status) {
        devs.put(dev, status);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setAll(Project project) {
        setAll((RepositoryObject<Project>) project);
        name = project.name;
        gitHubUrl = project.gitHubUrl;
    }

    public void addColumn(Column column) throws ExecutionException, InterruptedException {
        //System.err.println("addCOl" + columns + " " + column);
        if (!columns.contains(column)) {

            column.setProjectId(this.id);

            if (repositoryManager != null) {
                repositoryManager.getRepository().postColumn(column);
            }

            columns.add(column);
        }
    }

    public void removeColumn(Column column) throws ExecutionException, InterruptedException {
        columns.remove(column);
        //System.err.println("removeColumn: " + column.getName());
        column.deleteFromRepo();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public void addDev(Dev dev) throws ExecutionException, InterruptedException {
        if (!devs.containsKey(dev)) {
            devs.put(dev, DevStatus.DEV);
            dev.addProject(this);
            postToRepo();
            if (repositoryManager != null) {
                repositoryManager.getRepository().postParticipate(this, dev);
            }
        }
    }


    public void removeTask(Task task) throws ExecutionException, InterruptedException {
        for (Column col : columns) {
            col.removeTask(task);
        }
    }

    public void updateDevs(List<Dev> newDevs) throws ExecutionException, InterruptedException, RepositoryObjectDeleted {
        List<Dev> tmp = new ArrayList<>();
        for (Dev dev : newDevs) {
            if (!devs.containsKey(dev)) {
                addDev(dev);
            }
        }

        for (Dev dev : devs.keySet()) {
            if (!newDevs.contains(dev)) {
                tmp.add(dev);
            }
        }

        for (Dev dev : tmp) {
            removeDev(dev);
        }
    }

    public void addDev(Dev dev, DevStatus devStatus) throws ExecutionException, InterruptedException {
        devs.put(dev, devStatus);

        dev.addProject(this);
        postToRepo();
        if (repositoryManager != null) {
            repositoryManager.getRepository().postParticipate(this, dev);
        }
    }

    public void removeDev(Dev dev) throws ExecutionException, InterruptedException, RepositoryObjectDeleted {
        if (devs.remove(dev) != null) {
            dev.removeProject(this);
            if (repositoryManager != null) {
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
        edited = true;
    }

    public String getGitHubUrl() {
        return gitHubUrl;
    }

    public void setGitHubUrl(String gitHubUrl) {
        this.gitHubUrl = gitHubUrl;
        edited = true;
    }

    public RepositoryManager getRepository() {
        return repositoryManager;
    }
}

