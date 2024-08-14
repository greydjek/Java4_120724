package common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
@Getter
public class AbstractMessage implements Serializable {
private String message;

    public AbstractMessage(String message) {
        this.message = message;
    }
}
