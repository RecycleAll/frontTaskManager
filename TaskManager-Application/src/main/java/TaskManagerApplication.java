import io.taskmanager.api.TaskRepositoryApi;
import io.taskmanager.test.TaskRepository;
import io.taskmanager.ui.graphical.App;
import javafx.application.Application;

import java.io.IOException;

public class TaskManagerApplication {
    public static void main(String[] args) throws IOException {
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
