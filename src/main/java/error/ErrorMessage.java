package error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
    String error;

    public ErrorMessage(String error) {
        this.error = error;
    System.err.println(error);
    }
}
