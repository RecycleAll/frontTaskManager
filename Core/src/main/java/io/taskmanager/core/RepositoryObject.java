package io.taskmanager.core;

import io.taskmanager.core.repository.RepositoryEditionConflict;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.repository.RepositoryObjectDeleted;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

public abstract class RepositoryObject<T> {

    public static final int undefinedID = -1;

    protected RepositoryManager repositoryManager;
    protected int id;
    protected LocalDateTime updatedAt;
    protected boolean edited;

    public RepositoryObject(RepositoryManager repository) {
        this.repositoryManager = repository;
        id = undefinedID;
        edited = false;
    }

    public RepositoryObject() {
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

    public final boolean updateToRepo() throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted {
        return updateToRepo(false);
    }

    public final boolean updateToRepo(boolean force) throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted {
        if (!isInRepo() && repositoryManager != null){
            return myPost();
        }else if( repositoryManager != null){
            return myUpdateToRepo(force);
        }else{
            return true;
        }
    }

    public final boolean updateFromRepo() throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted {
        if (isInRepo() && repositoryManager != null){
            return myUpdateFromRepo();
        }else{
            return false;
        }
    }

    public boolean compare(RepositoryObject<T> other){
        return id == other.id && updatedAt.isEqual(other.updatedAt);
    }
    public abstract boolean compare(T other);

    public abstract boolean isConflict(T other);

    protected abstract boolean myPost() throws ExecutionException, InterruptedException;
    protected abstract boolean myDelete() throws ExecutionException, InterruptedException;
    protected abstract boolean myUpdateToRepo(boolean force) throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted;
    protected abstract boolean myUpdateFromRepo() throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted;
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

    }
    public final void setAll(RepositoryObject<T> object){
        id = object.id;
        updatedAt = object.updatedAt;
        edited = object.edited;
    }

    public abstract void setAll(T object);
}
