import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class MainTest {

    @AfterEach
     void tearDown() throws IOException {
        File f = new File("tasks.txt");
        if (f != null && f.exists()) {
            f.delete();
        }
    }

    @Test
    void test_loadTasksFromFile() throws Exception {
        Files.writeString(Path.of("tasks.txt"),
                "0|Do Laundry\n" +
                        "1|Go to gym\n");
        Main main = new Main();
        main.loadTasks();
        main.saveTasks();
        List<String> content = Files.readAllLines(Path.of("tasks.txt")); // stores content one line at a time in List<>
        assertEquals(2, content.size()); // should be 2 as we wrote 2 tasks
        assertEquals("0|Do Laundry", content.get(0)); // index 0
        assertEquals("1|Go to gym", content.get(1)); // index 1
    }
}