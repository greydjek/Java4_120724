package messageWorker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Builder
public class FileMessage extends AbstractMessage {
    private static final int BUTCH_SIZE = 8192;
    private String name;
    private long size;
    private byte[] bytes;
    private boolean isFirstButch;
    private int endByteNum;
    private final boolean isFinishBatch;

    public FileMessage(Path path,
                       byte[] bytes,
                       int endByteNum,
                       boolean isFirstButch,
                       boolean isFinishBatch) throws IOException {
        setCommand(Command.FILE_MESSAGE);
        this.isFirstButch = isFirstButch;
        this.isFinishBatch = isFinishBatch;
        this.bytes = bytes;
        this.endByteNum = endByteNum;
        this.name = path.getFileName().toString();
        size = Files.size(path);

    }

}
