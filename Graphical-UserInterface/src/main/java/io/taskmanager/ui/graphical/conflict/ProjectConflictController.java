package io.taskmanager.ui.graphical.conflict;

import io.taskmanager.core.Project;
import io.taskmanager.core.repository.RepositoryConflictHandler;
import io.taskmanager.core.repository.RepositoryManager;
import io.taskmanager.ui.graphical.ProjectEditorController;

import java.io.IOException;

public class ProjectConflictController extends RepositoryConflictController<Project>{
    public ProjectConflictController(RepositoryConflictHandler<Project> conflictHandler) throws IOException {
        super(conflictHandler);

        Project localProject = conflictHandler.getLocal();
        Project repoProject = conflictHandler.getRepo();
        RepositoryManager repositoryManager = conflictHandler.getRepository();

        ProjectEditorController controller = new ProjectEditorController(repositoryManager, localProject,false, false);
        controller.getButtonTypes().clear();
        localPane.getChildren().add( controller);

        controller = new ProjectEditorController(repositoryManager, repoProject, false,false);
        controller.getButtonTypes().clear();
        repoPane.getChildren().add( controller);

        mergedObject = localProject.merge(repoProject);
        ProjectEditorController mergedEditor = new ProjectEditorController(repositoryManager, mergedObject);
        mergedEditor.getButtonTypes().clear();
        objectEditor = mergedEditor;

        mergedPane.getChildren().add( mergedEditor );
    }
}
