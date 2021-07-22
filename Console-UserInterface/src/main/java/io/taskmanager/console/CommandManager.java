package io.taskmanager.console;

import io.taskmanager.test.Dev;
import io.taskmanager.test.TaskRepository;

import java.io.Console;
import java.util.concurrent.ExecutionException;

public class CommandManager {
    private final String[] args;
    private final TaskRepository taskRepository;

    public CommandManager(String[] args, TaskRepository taskRepository) {
        this.args = args;
        this.taskRepository = taskRepository;
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

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showProjects() {
        try {
            Dev dev = askToLogin();
            taskRepository.getProjects(dev);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showColumns() {
        try {
            Dev dev = askToLogin();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showTasks() {
        try {
            Dev dev = askToLogin();

        } catch (ExecutionException | InterruptedException e) {
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

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createProject() {
        try {
            Dev dev = askToLogin();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createColumn() {
        try {
            Dev dev = askToLogin();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createTask() {
        try {
            Dev dev = askToLogin();

        } catch (ExecutionException | InterruptedException e) {
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

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateProject() {
        try {
            Dev dev = askToLogin();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateColumn() {
        try {
            Dev dev = askToLogin();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateTask() {
        try {
            Dev dev = askToLogin();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Dev askToLogin() throws ExecutionException, InterruptedException {
        Console console = System.console();
        System.out.println("Enter your login:");
        String login = console.readLine();
        System.out.println("Enter your password:");
        String password = String.valueOf(console.readPassword());
        return taskRepository.loginDev(login, password);
    }
}