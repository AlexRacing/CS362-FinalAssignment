import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Aggregation visitor that functions like a SimpleAggregationVisitor but limits the feed to a set
 * number until it is requested, then it functions exactly as a SimpleAggregationVisitor
 */
public class LimitedAggregationVisitor extends SimpleAggregationVisitor {
    protected final int limit;
    protected int count = 0;
    protected Message cut = null;
    protected final Queue<Message> recent;
    protected boolean feedRequested = false;

    public LimitedAggregationVisitor(int limit) {
            super();
            this.limit = limit;
            this.recent = new PriorityQueue<>(((Comparator<? super Message>) Message::compareTime).reversed());
    }

    @Override
    protected boolean shouldSkip(User user) {
        return false;
    }

    @Override
    protected void consider(Message message) {
        if (feedRequested) {
            super.consider(message);
        }
        if (count < limit) {
            this.recent.add(message);
            count++;
            if (this.cut == null || this.cut.isNewerThan(message)) this.cut = message;
        } else if (message.isNewerThan(this.cut)) {
            this.recent.add(message);
            this.cut = this.recent.remove();
        }
    }

    /**
     * @return Message feed as queue, most recent to least recent.
     */
    public Queue<Message> getFeed() {
        if (feedRequested) return super.getFeed();

        else {
            this.feedRequested = true;
            this.feed.addAll(recent);

            return feed;
        }
    }
}
