package messageWorker;

import lombok.Getter;

@Getter
public class FileRequest extends AbstractMessage{
private final String name;

    public FileRequest(String name) {
        this.name = name;
    setCommand(Command.FILE_REQUEST);
    }

}
