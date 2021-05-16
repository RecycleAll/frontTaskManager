import java.util.List;

public interface TaskRepository {
    List<Project> getProjects();
    List<Task> getProject();
}
