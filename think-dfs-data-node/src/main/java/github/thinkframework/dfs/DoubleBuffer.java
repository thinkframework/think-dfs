package github.thinkframework.dfs;

/***
 * 双缓冲
 */
public class DoubleBuffer {

    private Long Log_Buffer_limit;;

    private Long MaxTxId = 0L;

    class FileLogeBuffer {
        public void write(FileLog fileLog) {

        }

        public long size(){
            return 0L;
        }
    }
}
