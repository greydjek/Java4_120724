package messageWorker;

import lombok.Getter;

@Getter
public class StringMessage extends AbstractMessage{
    String msg;

    public StringMessage(String msg) {
        this.msg = msg;
        setCommand(Command.AUTENTIFCATION_MESSAGE);
    }
}
