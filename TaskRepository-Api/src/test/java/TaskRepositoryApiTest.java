import io.taskmanager.api.TaskRepositoryApi;
import io.taskmanager.core.Column;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TaskRepositoryApiTest {

    TaskRepositoryApi repositoryApi = new TaskRepositoryApi("http://localhost:3000");

    @Test
    public void test_column() throws ExecutionException, InterruptedException {
        Column col = repositoryApi.getObject(1, Column.class);

        Assertions.assertNotNull(col);
        Assertions.assertEquals(1, col.getId());
    }

    @Test
    public void test_columns() throws ExecutionException, InterruptedException {
        List<Column> cols = repositoryApi.getColumns(1);

        for (Column col: cols) {
            System.out.println(col.getName()+" "+col.getId());
        }
        Assertions.assertNotNull(cols);
        Assertions.assertEquals(18, cols.size());
    }

}
