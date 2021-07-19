package io.taskmanager.api.model;

import io.taskmanager.core.RepositoryObject;
import io.taskmanager.core.Column;

import java.util.concurrent.ExecutionException;

public class ModelConverter {

    public static <T extends RepositoryObject, G extends BaseModel> T convert(G model) throws ExecutionException, InterruptedException {

        if( model instanceof ColumnModel){
            return (T) new Column(null, model.getId(), ((ColumnModel) model).getName(), ((ColumnModel) model).getProject_id());
        }else {
            return null;
        }
    }

}
