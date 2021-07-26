package io.taskmanager.app;

import io.taskmanager.api.TaskRepositoryApi;
import io.taskmanager.console.CommandManager;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.core.repository.TaskRepository;
import io.taskmanager.ui.graphical.App;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;

public class TaskManagerApplication {
    public static void main(String[] args) throws Exception {

        if(args.length < 1){
            System.err.println("You need to precise the API address");
            return;
        }

        RepositoryManager repositoryManager = new RepositoryManager();
        TaskRepository repository = new TaskRepositoryApi(args[0]);
        repositoryManager.setRepository(repository);

        if(args.length == 1){
            App.launchApp(repositoryManager);
        }
        else {
            Console console = System.console();
            if(console == null){
                ArrayList<String> arrays = new ArrayList<>(Arrays.asList("cmd", "/c", "start", "cmd", "/k", "java -jar out/artifacts/TaskManager_Application_jar/TaskManager-Application.jar"));
                arrays.addAll(Arrays.asList(args));
                Runtime.getRuntime().exec(arrays.stream().reduce((aze,aze2)-> aze+" "+aze2).orElseThrow());
            }
            else{
                CommandManager commandManager = new CommandManager(args, repositoryManager);
                commandManager.apply();
            }

        }
    }
}