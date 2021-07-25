package io.taskmanager.ui.graphical.conflict;

import io.taskmanager.core.Project;
import io.taskmanager.core.Task;
import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.ui.graphical.TaskController;

import java.io.IOException;

public class TaskConflictController extends RepositoryConflictController<Task> {

    public TaskConflictController(RepositoryConflictHandler<Task> conflictHandler, Project project) throws IOException {
        super(conflictHandler);

        Task localTask = conflictHandler.getLocal();
        Task repoTask = conflictHandler.getRepo();
        RepositoryManager repository = conflictHandler.getRepository();

        TaskController controller = new TaskController(repository, project, localTask, false);
        controller.getButtonTypes().clear();
        localPane.getChildren().add(controller);

        controller = new TaskController(repository, project, repoTask, false);
        controller.getButtonTypes().clear();
        repoPane.getChildren().add(controller);

        mergedObject = localTask.merge(repoTask);
        TaskController mergedEditor = new TaskController(repository, project, mergedObject);
        mergedEditor.getButtonTypes().clear();
        objectEditor = mergedEditor;

        mergedPane.getChildren().add(mergedEditor);
    }

}
