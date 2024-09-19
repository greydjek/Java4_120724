package messageWorker;

import java.io.Serializable;

public class AbstractMessage implements Serializable {
    private String message;
    private Command command;

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public AbstractMessage(String text) {
    }

    public AbstractMessage() {
    }

}
