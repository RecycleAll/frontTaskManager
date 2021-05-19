import io.taskmanager.api.TaskRepositoryApi;
import io.taskmanager.test.TaskRepository;
import io.taskmanager.ui.graphical.App;
import javafx.application.Application;

public class TaskManagerApplication {
    public static void main(String[] args) {
        TaskRepository taskRepository = new TaskRepositoryApi("http://localhost:3000");
        if(args.length == 0){
            Application.launch(App.class);
        }
        else {
            CommandManager commandManager = new CommandManager(args, taskRepository);
            commandManager.apply();
        }
    }
}
