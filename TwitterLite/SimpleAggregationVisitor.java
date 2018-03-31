import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class SimpleAggregationVisitor extends MessageAggregationVisitor {
    protected final Set<Message> messages;

    public SimpleAggregationVisitor() {
        this.messages = new HashSet<>();
    }

    @Override
    protected boolean shouldSkip(User user) {
        return false;
    }

    @Override
    protected void consider(Message message) {
        this.messages.add(message);
    }

    @Override
    protected void purgeUser(User user) {
        this.messages.removeIf(m -> user.equals(m.getOP()));
    }

    /**
     * @return Message messages as queue, most recent to least recent.
     */
    protected Collection<Message> getMessages() {
        return messages;
    }
}
