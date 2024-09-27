package messageWorker;

public class AutentificationMessage extends AbstractMessage {
    public AutentificationMessage(String message) {
        setCommand(Command.AUTENTIFCATION_MESSAGE);
    }
}
