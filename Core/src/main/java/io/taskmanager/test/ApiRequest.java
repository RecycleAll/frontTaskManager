package io.taskmanager.test;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

public abstract class ApiRequest {

    public static final int undefinedID = -1;

    protected RepositoryManager repositoryManager;
    protected int id;
    protected LocalDate updateAt;

    public ApiRequest(RepositoryManager repository) {
        this.repositoryManager = repository;
        id = undefinedID;
    }

    public ApiRequest() {
        this.repositoryManager = null;
        id = undefinedID;
    }

    protected final boolean isInRepo(){
        return id != undefinedID;
    }

    public final boolean deleteFromRepo() throws ExecutionException, InterruptedException {
        if( isInRepo() && repositoryManager != null){
            return myDelete();
        }else{
            return false;
        }
    }

    public final boolean postToRepo() throws ExecutionException, InterruptedException {
        if( !isInRepo() && repositoryManager != null){
            return myPost();
        }else{
            return false;
        }
    }

    public final boolean updateToRepo() throws ExecutionException, InterruptedException {
        if (!isInRepo() && repositoryManager != null){
            return myPost();
        }else if( repositoryManager != null){
            return myUpdateToRepo();
        }else{
            return false;
        }
    }

    public final boolean updateFromRepo(){
        if (isInRepo() && repositoryManager != null){
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


    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        if( this.repositoryManager == null)
            this.repositoryManager = repositoryManager;
        else{
            //TODO
        }
    }
}