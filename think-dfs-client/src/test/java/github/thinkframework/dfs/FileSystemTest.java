package github.thinkframework.dfs;

import io.github.thinkframework.dfs.FileSystemImpl;
import io.thinkframework.dfs.rpc.ThinkFileSystemGrpc;
import org.junit.Test;

public class FileSystemTest {

    @Test
    public void test(){
        new FileSystemImpl().mkdir("abc");
    }
}
