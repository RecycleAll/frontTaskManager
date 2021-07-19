package io.taskmanager.ui.graphical;

import io.taskmanager.test.*;

import java.io.IOException;

public class TaskConflictController extends  RepositoryConflictController<Task>{

    public TaskConflictController(RepositoryConflictHandler<Task> conflictHandler, Project project) throws IOException {
        super(conflictHandler);

        Task localTask = conflictHandler.getLocal();
        Task repoTask = conflictHandler.getRepo();
        RepositoryManager repository = conflictHandler.getRepository();

        mergedObject = localTask.merge(repoTask);
        TaskController controller = new TaskController(repository, project, localTask, false);
        controller.getButtonTypes().clear();
        localPane.getChildren().add( controller);

        controller = new TaskController(repository, project, repoTask, false);
        controller.getButtonTypes().clear();
        repoPane.getChildren().add( controller);

        TaskController mergedEditor = new TaskController(repository, project, mergedObject);
        objectEditor = mergedEditor;

        mergedPane.getChildren().add( mergedEditor );
    }

}
