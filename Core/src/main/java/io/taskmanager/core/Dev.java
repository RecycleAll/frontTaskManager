package io.taskmanager.core;

import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryEditionConflict;
import io.taskmanager.core.repository.RepositoryObjectDeleted;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Dev extends RepositoryObject<Dev> {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private int github_id;

    private final ArrayList<Project> projects;

    public Dev(int id, String firstname, String lastname, String email, String password, int github_id, ArrayList<Project> projects) {
        super(null);
        this.id = id;
        setFirstname(firstname);
        setLastname(lastname);
        setEmail(email);
        setPassword(password);
        setGithub_id(github_id);
        this.projects = projects;
    }

    public Dev(int id, String firstname, String lastname, String email, String password, int github_id){
        this(id, firstname, lastname, email, password, github_id, new ArrayList<>());
    }

    public Dev(Dev dev){
        this(dev.id, dev.firstname, dev.lastname, dev.email, dev.password, dev.github_id, dev.projects);
    }

    public Dev(){
        this(-1, "", "", "", "", 0);
    }

    @Override
    public boolean isConflict(Dev dev) {
        return  !compare(dev) &&
                updatedAt.isBefore(dev.updatedAt);
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    @Override
    public void setAll(Dev dev){
        id = dev.id;
        firstname = dev.firstname;
        lastname = dev.lastname;
        email = dev.email;
        password = dev.password;
        github_id = dev.github_id;
    }

    public boolean compare(Dev dev){
        return  super.compare(dev) &&
                firstname.equals(dev.firstname) &&
                lastname.equals(dev.lastname) &&
                email.equals(dev.email) &&
                github_id == dev.github_id &&
                password.equals(dev.password);
    }

    @Override
    protected boolean myPost() throws ExecutionException, InterruptedException {
        return false;
    }

    @Override
    protected boolean myDelete() throws ExecutionException, InterruptedException {
        return false;
    }

    @Override
    protected boolean myUpdateToRepo(boolean force) throws ExecutionException, InterruptedException, RepositoryEditionConflict, RepositoryObjectDeleted {
        if( !edited){
            System.out.println("Dev:myUpdateToRepo -> not edited");
            return true;
        }else{
            Dev dev = repositoryManager.getRepository().getDev(id);
            if(!force && isConflict(dev)){
                System.out.println("Dev:myUpdateToRepo -> conflict");
                throw new RepositoryEditionConflict( new RepositoryConflictHandler<Dev>(this, dev,  repositoryManager));
            }else {
                System.out.println("Dev:myUpdateToRepo -> no conflict (f:"+force+")");
                return repositoryManager.getRepository().updateDev(this);
            }
        }
    }

    @Override
    protected boolean myUpdateFromRepo() {
        return false;
    }

    @Override
    public Dev merge(Dev other) {
        if( id != other.id){
            return null;
        }else {
            Dev dev = new Dev();
            dev.setId(id);

            if(firstname.equals(other.firstname)){
                dev.firstname = firstname;
            }

            if(lastname.equals(other.lastname)){
                dev.lastname = lastname;
            }

            if(email.equals(other.email)){
                dev.email = email;
            }

            if(github_id == other.github_id){
                dev.github_id = github_id;
            }

            if(password.equals(other.password)){
                dev.password = password;
            }
            return dev;
        }
    }

    public int getId() {
        return id;
    }

    public void addProject(Project project) throws ExecutionException, InterruptedException {
        if( !projects.contains( project)){
            projects.add(project);
            project.addDev(this);
        }
    }

    public void removeProject(Project project) throws ExecutionException, InterruptedException, RepositoryObjectDeleted {
        if( projects.remove(project) ){
            project.removeDev(this);
        }
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
        edited = true;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
        edited = true;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        edited = true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        edited = true;
    }

    public int getGithub_id() {
        return github_id;
    }

    public void setGithub_id(int github_id) {
        this.github_id = github_id;
        edited = true;
    }

    @Override
    public String toString(){
        return firstname;
    }
}
