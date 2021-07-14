package io.taskmanager.test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Dev extends ApiRequest{
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

    public ArrayList<Project> getProjects() {
        return projects;
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
    protected boolean myUpdateToRepo() throws ExecutionException, InterruptedException {
        return false;
    }

    @Override
    protected boolean myUpdateFromRepo() {
        return false;
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

    public void removeProject(Project project) throws ExecutionException, InterruptedException {
        if( projects.remove(project) ){
            project.removeDev(this);
        }
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGithub_id() {
        return github_id;
    }

    public void setGithub_id(int github_id) {
        this.github_id = github_id;
    }

    @Override
    public String toString(){
        return firstname;
    }
}
