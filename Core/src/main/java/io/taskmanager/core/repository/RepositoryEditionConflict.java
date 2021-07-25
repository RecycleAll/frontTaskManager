package io.taskmanager.core.repository;

import io.taskmanager.core.RepositoryObject;

public class RepositoryEditionConflict extends Exception {

    private final RepositoryConflictHandler<? extends RepositoryObject<?>> conflictHandler;

    public RepositoryEditionConflict(RepositoryConflictHandler<? extends RepositoryObject<?>>  conflictHandler ) {
        this.conflictHandler = conflictHandler;
    }

    public RepositoryConflictHandler<? extends RepositoryObject<?>> getConflictHandler() {
        return conflictHandler;
    }
}
