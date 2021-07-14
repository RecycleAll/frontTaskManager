package io.taskmanager.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositoryObjectManager <T extends ApiRequest>{

    private TaskRepository repository;
    private final List<T> list;

    public RepositoryObjectManager(TaskRepository repository) {
        list = new ArrayList<>();
        this.repository = repository;
    }

    public T getObject(int id){
        Optional<T> res = list.stream().filter(column -> column.id == id).findFirst();
        if( res.isPresent()){
            return res.get();
        }else{
            return null;
        }
    }

    public void addObject(T obj){
        list.add(obj);
    }

}
