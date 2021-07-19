package io.taskmanager.test;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

public abstract class ApiRequest<T> {

    public static final int undefinedID = -1;

    protected RepositoryManager repositoryManager;
    protected int id;
    protected LocalDateTime updatedAt;
    protected boolean edited;

    public ApiRequest(RepositoryManager repository) {
        this.repositoryManager = repository;
        id = undefinedID;
        edited = false;
    }

    public ApiRequest() {
        this(null);
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

    public final boolean updateToRepo() throws ExecutionException, InterruptedException, RepositoryEditionConflict {
        return updateToRepo(false);
    }

    public final boolean updateToRepo(boolean force) throws ExecutionException, InterruptedException, RepositoryEditionConflict {
        if (!isInRepo() && repositoryManager != null){
            return myPost();
        }else if( repositoryManager != null){
            return myUpdateToRepo(force);
        }else{
            return true;
        }
    }

    public final boolean updateFromRepo() throws ExecutionException, InterruptedException, RepositoryEditionConflict {
        if (isInRepo() && repositoryManager != null){
            return myUpdateFromRepo();
        }else{
            return false;
        }
    }

    public final boolean hasBeenUpdated(ApiRequest<T> other){
        return !updatedAt.isEqual(other.updatedAt);
    }
    public boolean compare(ApiRequest<T> other){
        return id == other.id && updatedAt.isEqual(other.updatedAt);
    }

    public abstract boolean isConflict(T other);

    protected abstract boolean myPost() throws ExecutionException, InterruptedException;
    protected abstract boolean myDelete() throws ExecutionException, InterruptedException;
    protected abstract boolean myUpdateToRepo(boolean force) throws ExecutionException, InterruptedException, RepositoryEditionConflict;
    protected abstract boolean myUpdateFromRepo() throws ExecutionException, InterruptedException;
    public abstract T merge(T other);

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public abstract void setAll(T object);
}
