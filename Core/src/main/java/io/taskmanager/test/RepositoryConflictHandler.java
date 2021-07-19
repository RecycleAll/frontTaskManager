package io.taskmanager.test;

public class RepositoryConflictHandler <T extends ApiRequest> {

    private T local, repo, merged;
    private RepositoryManager repository;

    public RepositoryConflictHandler(T local, T repo, RepositoryManager repository) {
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

    public RepositoryManager getRepository() {
        return repository;
    }
}
