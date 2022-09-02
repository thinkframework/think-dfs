package io.github.thinkframework.dfs.commons.management;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.thinkframework.dfs.commons.config.ApplicationConfiguration;
import io.github.thinkframework.dfs.commons.config.Constants;
import io.github.thinkframework.dfs.commons.domain.NameNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class NameNodeService {

    private static final Logger logger = LoggerFactory.getLogger(NameNodeService.class);

    /**
     * 根节点,id为0
     */
    private NameNode root = new NameNode("/");

    private volatile String checkpoint;

    private volatile long editLogsId;

    private volatile boolean loaded;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private ApplicationConfiguration applicationConfiguration;

    /**
     * 加载FsImage,EditLog
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        Thread load = new Thread(() -> {
            checkPoint();
            loadFSImage();
            replayEditLogs();
        });
        load.start();
        load.join(); // 优先加载本地文件
    }


    /**
     * 创建目录
     * @param path
     */
    public void mkdir(String path) throws IOException {
        Map map = gson.fromJson(path, Map.class);
        editLogsId = ((Double)map.get("id")).longValue();
        List<String> names = Arrays.stream(map.get("content").toString().substring(1).split(File.separator))
        .filter(name -> name != null && name.length() != 0)
        .collect(Collectors.toList());
        NameNode parent = root;
        for (int i =0;i< names.size() -1;i++){
            parent = getNameNode(names.get(i), parent);
        }

        NameNode node = getNameNode(names.get(names.size()-1), parent);
    }


    /**
     * 更新checkpoint
     */
    private void writePoint(){
        Path path = Paths.get(System.getProperty("user.home") + File.separator
                + "tmp" + File.separator
                + applicationConfiguration.getWorkdir()
                + File.separator + "checkpoint.txt");
        File file;
        try {
            if(Files.readString(path).equals(checkpoint)){
                return;
            }
            Files.writeString(path,checkpoint);
            logger.info("checkpoint更新:{}.",checkpoint);
        } catch (FileNotFoundException e) {
            logger.error("文件不存在.",e);
        } catch (IOException e) {
            logger.error("IO异常.",e);
        }
    }

    /**
     * 加载checkpoint
     */
    private void checkPoint(){
        Path path = Paths.get(System.getProperty("user.home") + File.separator
                + "tmp" + File.separator
                + applicationConfiguration.getWorkdir()
                + File.separator + "checkpoint.txt");
        File file;
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
                Files.writeString(path,"0");
                logger.info("初始化checkpoint.");
            }
            checkpoint = Files.readString(path);
            logger.info("checkpoint加载成功:{}.",checkpoint);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载FSImage
     */
    private void loadFSImage(){
        Path path = Paths.get(System.getProperty("user.home") + File.separator
                + "tmp" + File.separator
                + applicationConfiguration.getWorkdir()
                + File.separator + "fsImages"
                + File.separator + "fsImage.txt");
        File file;
        if(checkpoint != null && checkpoint.length() > 0) {
            try {
                if (Files.notExists(path)) {
                    Files.createFile(path);
                    Files.writeString(path,gson.toJson(root));
                    logger.info("初始化fsImage.");
                }
                String json = Files.readString(path);
                logger.info("fsImage加载成功:{}.",json);
                root = gson.fromJson(json,NameNode.class);
                loaded = true;
            } catch (FileNotFoundException e) {
                logger.error("文件不存在.", e);
            } catch (IOException e) {
                logger.error("IO异常.", e);
            }
        }
    }


    /**
     * 重放EditLogs
     */
    private void replayEditLogs()  {
        Path path = Paths.get(System.getProperty("user.home") + File.separator
                + "tmp" + File.separator
                + applicationConfiguration.getWorkdir()
                + File.separator + "editLogs");
        if(Files.exists(path)) {
            List<Path> paths = null;
            try {
                paths = Files.list(path)
                        .filter(f -> Constants.EDIT_LOG_SUFFIX.equals(f.getFileName().toString().substring(f.getFileName().toString().lastIndexOf(".")+1)))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                logger.error("IO异常.", e);
            }
            if (paths.isEmpty()) {
                logger.info("editlogs不存在.");
                return; // 什么都没有的时候直接返回
            }
            // 补0,排序
            String format = "%0"+Integer.SIZE+"d";
            Collections.sort(paths, Comparator
                    .comparing(x -> String.format(format, Integer.valueOf(x.getFileName().toString()
                            .substring(0, x.getFileName().toString().indexOf("_"))))));
            logger.debug("排序后的文件名: {}.", paths.stream().map(p -> p.getFileName().toString()).collect(Collectors.joining(",")));
            // 文件名称排序,大于自己的全加载
            paths.stream().filter(f -> f.getFileName().toString().compareTo(checkpoint) >= 0)
                    .forEachOrdered(this::loadEditLog);
            logger.info("editlogs加载成功:{}.",gson.toJson(root));
        }
    }

    private void loadEditLog(Path path) {
        RandomAccessFile file = null;
        FileChannel fileChannel = null;
        try {
            file = new RandomAccessFile(path.toFile(),"rw");
            fileChannel = new RandomAccessFile(path.toFile(),"rw").getChannel();
            ByteBuffer fileBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE * 2);
            ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
            int count;
            for(;(count = fileChannel.read(byteBuffer)) > 0;byteBuffer.clear()){
                byteBuffer.flip();
                fileBuffer.put(byteBuffer);
            }

            fileBuffer.flip();
            byte[] bytes = new byte[fileBuffer.limit()];
            fileBuffer.get(bytes);
            String lines = new String(bytes);
            StringReader stringReader = new StringReader(lines);
            BufferedReader bufferedReader = new BufferedReader(stringReader);
            for(String line = bufferedReader.readLine();line != null && line.length() > 0 ; line = bufferedReader.readLine()){
                mkdir(line);
            }
            logger.info("load editLogs: {}", path.getFileName());
        } catch (FileNotFoundException e) {
            logger.error("文件不存在.",e);
        } catch (IOException e) {
            logger.error("IO异常.",e);
        } finally {
            try {
                if(fileChannel != null){
                    fileChannel.close();
                }
                if(file != null) {
                    file.close();
                }
            } catch (IOException e) {
                logger.error("IO异常.",e);
            }
        }
    }


    /**
     * 创建节点
     * @param name
     * @param parent
     * @return
     */
    private NameNode getNameNode(String name, NameNode parent) {
        NameNode node = parent.find(name)
                .orElseGet(() -> {
                    // todo 加锁? cas?
                    NameNode nameNode = new NameNode(name,parent);
                    return nameNode;
                });
        ;
        return node;
    }

    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public NameNode getRoot() {
        return root;
    }

    public void setRoot(NameNode root) {
        this.root = root;
    }

    public String getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(String checkpoint) {
        this.checkpoint = checkpoint;
    }

    public long getEditLogsId() {
        return editLogsId;
    }

    public void setEditLogsId(long editLogsId) {
        this.editLogsId = editLogsId;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
