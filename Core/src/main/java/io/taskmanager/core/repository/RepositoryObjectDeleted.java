package io.taskmanager.core.repository;

import io.taskmanager.core.RepositoryObject;

import java.util.ArrayList;
import java.util.List;

public class RepositoryObjectDeleted extends Exception{

    private final List<RepositoryObject<?>> objects;

    public RepositoryObjectDeleted(String str) {
        objects = null;
    }

    public RepositoryObjectDeleted() {
        objects = new ArrayList<>();
    }

    public RepositoryObjectDeleted(List<RepositoryObject<?>> objects) {
        this.objects = objects;
    }

    public RepositoryObjectDeleted(RepositoryObject<?> object) {
        objects = new ArrayList<>();
        addObject(object);
    }

    public List<RepositoryObject<?>> getObjects() {
        return objects;
    }

    public void addObject(RepositoryObject<?> object){
        objects.add(object);
    }

    public void addObject( List<RepositoryObject<?>> object){
        objects.addAll(object);
    }
}
