package io.taskmanager.test;

public class RepositoryEditionConflict extends Exception {

    private final RepositoryConflictHandler<? extends ApiRequest<?>> conflictHandler;

    public RepositoryEditionConflict(RepositoryConflictHandler<? extends ApiRequest<?>>  conflictHandler ) {
        this.conflictHandler = conflictHandler;
    }

    public RepositoryConflictHandler<? extends ApiRequest<?>> getConflictHandler() {
        return conflictHandler;
    }
}
