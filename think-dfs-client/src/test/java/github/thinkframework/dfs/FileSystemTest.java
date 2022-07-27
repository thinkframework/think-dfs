package github.thinkframework.dfs;

import io.github.thinkframework.dfs.FileSystem;
import io.github.thinkframework.dfs.FileSystemImpl;
import org.junit.Before;
import org.junit.Test;

public class FileSystemTest {
    private FileSystem fileSystem;
    @Before
    public void setup(){
        fileSystem = new FileSystemImpl();
    }
    @Test
    public void test(){
        fileSystem.mkdir("/a/b/c");
    }
}
