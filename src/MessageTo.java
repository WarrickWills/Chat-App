
/**
 *
 * @author Warrick Wills 13831575
 * @author Keenen Leyson 13828049
 */
public class MessageTo extends Message {

    private String name;

    public MessageTo(String message, String name) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
