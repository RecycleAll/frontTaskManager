package io.taslmanager.app;

import io.taskmanager.api.TaskRepositoryApi;
import io.taskmanager.console.CommandManager;
import io.taskmanager.test.TaskRepository;
import io.taskmanager.ui.graphical.App;

public class TaskManagerApplication {
    public static void main(String[] args) throws Exception {
        TaskRepository taskRepository = new TaskRepositoryApi("http://localhost:3000");
        if(args.length == 0){
            App.launchApp(taskRepository);
        }
        else {
            CommandManager commandManager = new CommandManager(args, taskRepository);
            commandManager.apply();
        }
    }
}
