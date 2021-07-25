package io.taskmanager.ui.graphical.conflict;

import io.taskmanager.core.Column;
import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.ui.graphical.ColumnEditor;

import java.io.IOException;

public class ColumnConflictController extends RepositoryConflictController<Column> {

    public ColumnConflictController(RepositoryConflictHandler<Column> conflictHandler) throws IOException {
        super(conflictHandler);

        Column localTask = conflictHandler.getLocal();
        Column repoTask = conflictHandler.getRepo();
        RepositoryManager repository = conflictHandler.getRepository();

        mergedObject = localTask.merge(repoTask);
        ColumnEditor controller = new ColumnEditor(repository, localTask, false);
        controller.getButtonTypes().clear();
        localPane.getChildren().add(controller);

        controller = new ColumnEditor(repository, repoTask, false);
        controller.getButtonTypes().clear();
        repoPane.getChildren().add(controller);

        ColumnEditor mergedEditor = new ColumnEditor(repository, mergedObject);
        mergedEditor.getButtonTypes().clear();
        objectEditor = mergedEditor;

        mergedPane.getChildren().add(mergedEditor);
    }

}
