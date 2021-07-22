package io.taskmanager.console;


import io.taskmanager.core.Dev;
import io.taskmanager.core.repository.RepositoryManager;

import java.io.Console;
import java.util.concurrent.ExecutionException;

public class CommandManager {
    private final String[] args;
    private final RepositoryManager repositoryManager;

    public CommandManager(String[] args, RepositoryManager repositoryManager) {
        this.args = args;
        this.repositoryManager = repositoryManager;
    }

    public void apply() {
        switch (args[0]) {
            case "show" -> show();
            case "create" -> create();
            case "update" -> update();
        }
    }

    private void show() {
        switch (args[1]) {
            case "dev" -> showDev();
            case "project" -> showProjects();
            case "column" -> showColumns();
            case "task" -> showTasks();
        }
    }

    private void showDev() {
        try {
            Dev dev = askToLogin();
            System.out.print(dev);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProjects() {
        try {
            Dev dev = askToLogin();
            //repositoryManager.(dev);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showColumns() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTasks() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void create() {
        switch (args[1]) {
            case "dev" -> createDev();
            case "project" -> createProject();
            case "column" -> createColumn();
            case "task" -> createTask();
        }
    }

    private void createDev() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createProject() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createColumn() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTask() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update() {
        switch (args[1]) {
            case "dev" -> updateDev();
            case "project" -> updateProject();
            case "column" -> updateColumn();
            case "task" -> updateTask();
        }
    }

    private void updateDev() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProject() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateColumn() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTask() {
        try {
            Dev dev = askToLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Dev askToLogin() throws Exception {
            Console console = System.console();
            System.out.println("Enter your login:");
            String login = console.readLine();
            System.out.println("Enter your password:");
            String password = String.valueOf(console.readPassword());
            int devId = repositoryManager.getRepository().loginDev(login, password);
            return repositoryManager.getDev(devId, true);
    }
}