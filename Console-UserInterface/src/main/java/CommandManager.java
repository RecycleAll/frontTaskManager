import io.taskmanager.test.TaskRepository;

public class CommandManager {
    private String[] args;
    private TaskRepository taskRepository;

    public CommandManager(String[] args, TaskRepository taskRepository){
        this.args = args;
        this.taskRepository = taskRepository;
    }

    public void apply(){
        switch (args[0]){

        }
    }
}
