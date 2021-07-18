package io.taskmanager.test;

public class RepositoryConflictHandler <T extends ApiRequest> {

    private T local, repo, merged;
    private TaskRepository repository;

    public RepositoryConflictHandler(T local, T repo, TaskRepository repository) {
        this.local = local;
        this.repo = repo;
        this.repository = repository;
    }

    public T getLocal() {
        return local;
    }

    public T getRepo() {
        return repo;
    }

    public TaskRepository getRepository() {
        return repository;
    }
}
