package messageWorker;

import lombok.Builder;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class FileMessage extends AbstractMessage {
    private static final int BUTCH_SIZE = 8192;
    private String name;
    private long size;
    private byte[] bytes;
    private boolean isFirstButch;
    private final boolean isFinishBatch;
    private int endByteNum;

    public FileMessage(Path path,
                       byte[] bytes,
                       String name,
                       boolean isFirstButch,
                       boolean isFinishBatch,
                       int endByteNum) throws IOException {
        setCommand(Command.FILE_MESSAGE);
        this.isFirstButch = isFirstButch;
        this.isFinishBatch = isFinishBatch;
        this.bytes = bytes;
        this.endByteNum = endByteNum;
        this.name = name;
        size = Files.size(path);

    }

}



