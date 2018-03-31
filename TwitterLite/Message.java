import java.util.Objects;

/**
 * Class representing a users's message.
 */
public class Message extends UUIDed {
    private User   op;
    private String message;
    private long   timecode;

    public Message(User op, String message) {
        super();
        this.timecode = System.currentTimeMillis();
        this.op = op;
        this.message = message;
    }

    public int getUUID() {
        return this.uuid;
    }

    public long getTimecode() {
        return this.timecode;
    }

    public User getOP() {
        return this.op;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return uuid == message.uuid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return this.op.getName()+": "+this.message;
    }

    public String toDetailedString() {
        return super.toString()+"{" +
               "'" + message + '\'' +
               " by " + op +
               " at " + timecode +
               " id: " + uuid +
               '}';
    }

    // Time comparable related methods

    public int compareTime(Message message) {
        int compare = Long.compare(this.getTimecode(), message.getTimecode());
        if (compare == 0) compare = Long.compare(this.getUUID(), message.getUUID());
        return compare;
    }

    public int compareTime(long timecode) {
        return Long.compare(this.getTimecode(), timecode);
    }

    public boolean isNewerThan(Message message) {
        return compareTime(message) > 0;
        // This might be backwards
    }

    public boolean isNewerThan(long timecode) {
        return compareTime(timecode) > 0;
        // This might be backwards
    }
}
