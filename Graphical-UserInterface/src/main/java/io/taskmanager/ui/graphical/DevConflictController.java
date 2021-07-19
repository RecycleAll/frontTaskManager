package io.taskmanager.ui.graphical;

import io.taskmanager.test.Dev;
import io.taskmanager.test.RepositoryConflictHandler;

import java.io.IOException;

public class DevConflictController extends RepositoryConflictController<Dev>{

    public DevConflictController(RepositoryConflictHandler<Dev> conflictHandler) throws IOException {
        super(conflictHandler);
        //localPane.add( new DevEditorGrid((Dev) conflictHandler.getRepo()), 0, 1);
        Dev localDev = conflictHandler.getLocal();
        Dev repoDev = conflictHandler.getRepo();

        mergedObject =  localDev.merge(repoDev);

        localPane.getChildren().add( new DevEditorGrid( localDev , false));
        repoPane.getChildren().add( new DevEditorGrid( repoDev, false));
        DevEditorGrid mergedEditor = new DevEditorGrid( (Dev)mergedObject);

        objectEditor = mergedEditor;
        mergedPane.getChildren().add( mergedEditor );
    }


}
