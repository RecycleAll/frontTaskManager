package io.taskmanager.test;

import java.util.ArrayList;

public class Dev {
    private final int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int github_id;

    private ArrayList<Project> projects;

    public Dev(int id, String firstName, String lastName, String email, String password, int github_id, ArrayList<Project> projects) {
        this.id = id;
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        setGithub_id(github_id);
        this.projects = projects;
    }

    public Dev(int id, String firstName, String lastName, String email, String password, int github_id){
        this(id, firstName, lastName, email, password, github_id, new ArrayList<>());
    }

    public Dev(Dev dev){
        this(dev.id, dev.firstName, dev.lastName, dev.email, dev.password, -dev.github_id, dev.projects);
    }

    public Dev(){
        this(-1, "", "", "", "", 0);
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public int getId() {
        return id;
    }

    public void addProject(Project project){
        projects.add(project);
    }

    public void removeProject(Project project){
        projects.remove(project);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        return firstName;
    }
}
