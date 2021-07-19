package io.taskmanager.ui.graphical.conflict;

import io.taskmanager.core.RepositoryObject;

public interface IObjectEditor <T extends RepositoryObject<?>>{

    boolean validateChange();
    boolean applyChange();
    T getEditedObject();
}
