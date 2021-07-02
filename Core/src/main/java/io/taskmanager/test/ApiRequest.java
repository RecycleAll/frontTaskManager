package io.taskmanager.test;

import java.util.concurrent.ExecutionException;

public abstract class ApiRequest {

    public static final int undefinedID = -1;

    protected final TaskRepository repository;
    protected int id;

    public ApiRequest(TaskRepository repository) {
        this.repository = repository;
        id = undefinedID;
    }

    protected final boolean isInRepo(){
        return id != undefinedID;
    }

    public final boolean delete() throws ExecutionException, InterruptedException {
        if( isInRepo()){
            return myDelete();
        }else{
            return false;
        }
    }

    public final boolean post() throws ExecutionException, InterruptedException {
        if( !isInRepo()){
            return myPost();
        }else{
            return false;
        }
    }

    public final boolean updateToRepo() throws ExecutionException, InterruptedException {
        if (!isInRepo()){
            return myPost();
        }else{
            return myUpdateToRepo();
        }
    }

    public final boolean updateFromRepo(){
        if (isInRepo()){
            return myUpdateFromRepo();
        }else{
            return false;
        }
    }

    protected abstract boolean myPost() throws ExecutionException, InterruptedException;
    protected abstract boolean myDelete() throws ExecutionException, InterruptedException;
    protected abstract boolean myUpdateToRepo() throws ExecutionException, InterruptedException;
    protected abstract boolean myUpdateFromRepo();

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
