package io.taskmanager.ui.graphical;

import io.taskmanager.test.ApiRequest;

public interface IObjectEditor <T extends ApiRequest<?>>{

    boolean validateChange();
    T getEditedObject();
}
