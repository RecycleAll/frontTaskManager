package io.taskmanager.console;


import io.taskmanager.core.*;
import io.taskmanager.core.repository.RepositoryManager;

import java.io.Console;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
            List<String> projects = dev.getProjects().stream()
                    .map(Project::getName)
                    .collect(Collectors.toList());
            projects.forEach(pro -> {
                System.out.println("Project Name : " + pro);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showColumns() {
        try {
            Dev dev = askToLogin();
            List<String> columns = dev.getProjects().stream()
                    .filter(project -> project.getName().equals(args[2]))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"))
                    .getColumns().stream()
                    .map(Column::getName)
                    .collect(Collectors.toList());
            columns.forEach(column -> {
                System.out.println("Column Name : " + column);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTasks() {
        try {
            Dev dev = askToLogin();
            List<Task> tasks = dev.getProjects().stream()
                    .filter(project -> project.getName().equals(args[2]))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"))
                    .getColumns().stream()
                    .flatMap(column -> column.getTasks().stream())
                    .collect(Collectors.toList());
            tasks.forEach(task -> {
                System.out.println("Task Name : " + task.getName());
                System.out.println("Task Description : " + task.getDescription());
            });
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
            Dev dev = askDevInformationAndRegister();
            if (dev != null) {
                System.out.println("Dev created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createProject() {
        try {
            Dev dev = askToLogin();
            Project project = askProjectInformation();
            project.addDev(dev, DevStatus.OWNER);
            project.postToRepo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createColumn() {
        try {
            Dev dev = askToLogin();
            Project project = dev.getProjects().stream()
                    .filter(project1 -> project1.getName().equals(args[2]))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            Column column = askColumnInformation(project);
            project.addColumn(column);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTask() {
        try {
            Dev dev = askToLogin();
            Project project = dev.getProjects().stream()
                    .filter(project1 -> project1.getName().equals(args[2]))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            Column column = project.getColumns().stream()
                    .filter(column1 -> column1.getName().equals(args[3]))
                    .findFirst().orElseThrow(() -> new RuntimeException("Column not found"));
            Task task = askTaskInformation();
            column.addTask(task);
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

    private Dev askDevInformationAndRegister() throws Exception {
        Console console = System.console();
        System.out.println("Enter your firstname:");
        String firstname = console.readLine();
        System.out.println("Enter your lastname:");
        String lastname = console.readLine();
        System.out.println("Enter your email:");
        String email = console.readLine();
        System.out.println("Enter your password:");
        String password = String.valueOf(console.readPassword());
        Dev newDev = new Dev(0, firstname, lastname, email, password, 0);
        repositoryManager.getRepository().registerDev(newDev);
        int devId = repositoryManager.getRepository().loginDev(email, password);
        return repositoryManager.getDev(devId, true);
    }


    private Project askProjectInformation() {
        Console console = System.console();
        System.out.println("Enter project name :");
        String name = console.readLine();
        return new Project(repositoryManager, -1, name, null);
    }

    private Column askColumnInformation(Project project) {
        Console console = System.console();
        System.out.println("Enter column name :");
        String name = console.readLine();
        return new Column(repositoryManager, -1, name, project.getId());
    }

    private Task askTaskInformation() {
        Console console = System.console();
        System.out.println("Enter task name :");
        String name = console.readLine();
        System.out.println("Enter task description :");
        String description = console.readLine();
        System.out.println("Enter task ending date (year/month/date):");
        String date = console.readLine();
        LocalDate localDate = LocalDate.of(Integer.parseInt(date.split("/")[0]), Integer.parseInt(date.split("/")[1]), Integer.parseInt(date.split("/")[2]));
        return new Task(repositoryManager, -1, name, description, localDate);
    }
}