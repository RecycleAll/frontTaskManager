package io.taskmanager.core.repository;

import io.taskmanager.core.RepositoryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RepositoryObjectManager <T extends RepositoryObject<?>>{

    private final List<T> list;

    public RepositoryObjectManager(TaskRepository repository) {
        list = new ArrayList<>();
    }

    public T getObject(int id){
        Optional<T> res = list.stream().filter(column -> column.getId() == id).findFirst();
        return res.orElse(null);
    }

    public void addObject(T obj){
        if(list.stream().noneMatch(t -> t.getId() == obj.getId())){
            list.add(obj);
        }
    }

    public void addObject(List<T> objs){
        for (T obj : objs) {
            addObject(obj);
        }
    }

    public final boolean remove(T obj){
        return list.remove(obj);
    }

    public final List<T> getList() {
        return Collections.unmodifiableList(list);
    }
}
