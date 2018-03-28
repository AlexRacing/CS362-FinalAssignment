import java.util.PriorityQueue;
import java.util.Queue;

public class SimpleAggregationVisitor extends MessageAggregationVisitor {
    protected final Queue<Message> feed;

    public SimpleAggregationVisitor() {
        this.feed = new PriorityQueue<>();
    }

    @Override
    protected boolean shouldSkip(User user) {
        return false;
    }

    @Override
    protected void consider(Message message) {
        this.feed.add(message);
    }

    /**
     * @return Message feed as queue, most recent to least recent.
     */
    public Queue<Message> getFeed() {
        return feed;
    }
}
