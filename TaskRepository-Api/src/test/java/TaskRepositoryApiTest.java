import io.taskmanager.api.TaskRepositoryApi;
import io.taskmanager.test.Column;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class TaskRepositoryApiTest {

    TaskRepositoryApi repositoryApi = new TaskRepositoryApi("http://localhost:3000");

    @Test
    public void test_column() throws ExecutionException, InterruptedException {
        Column col = repositoryApi.getObject(1, Column.class);

        Assertions.assertNotNull(col);
        Assertions.assertEquals(1, col.getId());
    }

}
