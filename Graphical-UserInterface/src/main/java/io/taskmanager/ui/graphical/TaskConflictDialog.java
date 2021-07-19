package io.taskmanager.ui.graphical;

import io.taskmanager.test.Project;
import io.taskmanager.test.RepositoryConflictHandler;
import io.taskmanager.test.Task;

import java.io.IOException;

public class TaskConflictDialog extends RepositoryConflictDialog<Task>{

    public TaskConflictDialog(RepositoryConflictHandler<Task> conflictHandler, Project project) throws IOException {
        super(new TaskConflictController(conflictHandler, project));
    }

}
