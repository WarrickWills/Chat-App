
import java.io.Serializable;

/**
 *
 * @author Warrick Wills 13831575
 * @author Keenen Leyson 13828049
 */
public abstract class Message implements Serializable {

    private String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
