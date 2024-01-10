package github.thinkframework.dfs;

import io.github.thinkframework.dfs.client.FileSystem;
import io.github.thinkframework.dfs.client.impl.FileSystemImpl;
import io.github.thinkframework.dfs.commons.discovery.DiscoveryClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileSystemTest {

    private final int count = 1; // 10个线程

    private final int batch = 1; // 每个线程写1000文件

    ExecutorService executorService;
    private FileSystem fileSystem;

    @Before
    public void setup() {
        executorService = Executors.newFixedThreadPool(count);
        fileSystem = new FileSystemImpl();
    }

    @Test
    public void testMkDir() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            executorService.execute(() -> {
                String uuid = UUID.randomUUID().toString();
                for (int j = 0; j < batch; j++) {
                    fileSystem.mkdir( File.separator + uuid + File.separator + UUID.randomUUID().toString());
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
    }


    @Test
    public void testUpload() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            executorService.execute(() -> {
                String uuid = UUID.randomUUID().toString();
                for (int j = 0; j < batch; j++) {
                    try {
                        FileSystemImpl fileSystem = new FileSystemImpl();
                        fileSystem.discoveryClient = new DiscoveryClient();
                        Path path = new File("/Users/lixiaobin/Downloads/testimage.png").toPath();
                        Files.writeString(path, UUID.randomUUID().toString());
                        fileSystem.send(new File("/Users/lixiaobin/Downloads/testimage.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
    }
    @Test
    public void testDownload() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            executorService.execute(() -> {
                String uuid = UUID.randomUUID().toString();
                for (int j = 0; j < batch; j++) {
                    try {
                        FileSystemImpl fileSystem = new FileSystemImpl();
                        fileSystem.discoveryClient = new DiscoveryClient();
                        Path path = new File("/Users/lixiaobin/Downloads/testimage.png").toPath();
                        Files.writeString(path, UUID.randomUUID().toString());
                        fileSystem.send(new File("/Users/lixiaobin/Downloads/testimage.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
    }

    @After
    public void teardown() {
        fileSystem = null;
//        while(!executorService.isTerminated()){
//            // 等待线程池关闭
//        }
    }
}
