import java.util.Collection;
import java.util.Queue;

/**
 * Abstract class with default implementations of its methods representing the Decoration in the
 * Decoration pattern used by the AggregationVisitors.
 */
public abstract class MessageAggregationFilter extends MessageAggregationVisitor {
    protected final MessageAggregationVisitor aggregator;

    public MessageAggregationFilter(MessageAggregationVisitor aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    protected boolean shouldSkip(User user) {
        return this.aggregator.shouldSkip(user);
    }

    @Override
    protected void consider(Message message) {
        this.aggregator.consider(message);
    }

    @Override
    protected void purgeUser(User user) {
        this.aggregator.purgeUser(user);
    }

    @Override
    public Collection<Message> getMessages() {
        return this.aggregator.getMessages();
    }
}
