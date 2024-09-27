package messageWorker;

import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
@Getter
public class ListMessage extends AbstractMessage{
    private final List<String> files;

    public ListMessage(Path dir) throws Exception{
    files = Files.list(dir).map(path-> path.getFileName().toString()).collect(Collectors.toList());
setCommand(Command.LIST_MESSAGE);
    }
}
