package io.taskmanager.ui.graphical.conflict;

import io.taskmanager.core.RepositoryObject;
import io.taskmanager.core.repository.RepositoryObjectDeleted;

import java.util.concurrent.ExecutionException;

public interface IObjectEditor <T extends RepositoryObject<?>>{

    boolean validateChange();
    boolean applyChange() throws ExecutionException, RepositoryObjectDeleted, InterruptedException;
    T getEditedObject();
}
