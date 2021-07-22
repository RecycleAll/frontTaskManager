package io.taskmanager.ui.graphical.conflict;

import io.taskmanager.core.Project;
import io.taskmanager.core.Task;
import io.taskmanager.core.repository.RepositoryConflictHandler;

import java.io.IOException;

public class TaskConflictDialog extends RepositoryConflictDialog<Task> {

    public TaskConflictDialog(RepositoryConflictHandler<Task> conflictHandler, Project project) throws IOException {
        super(new TaskConflictController(conflictHandler, project));
    }

}
