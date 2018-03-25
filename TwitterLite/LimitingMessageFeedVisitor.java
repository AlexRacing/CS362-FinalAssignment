import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class LimitingMessageFeedVisitor extends MessageFeedVisitor {
    private final int minCount;
    private int count = 0;
    private int cut = Integer.MAX_VALUE;
    protected final Queue<Message> recent;
    private boolean feedRequested = false;

    public LimitingMessageFeedVisitor(User user, int limit) {
        super(user);
        this.minCount = limit;
        this.recent = new PriorityQueue<>(Comparator.comparingInt(Message::getUUID).reversed());
    }

    @Override
    public void visit(User user) {
        if (feedRequested) super.visit(user);
        else {
            if (this.seen.contains(user)) {
                for (Message m : user) if (!this.recent.contains(m)) this.limitingAdd(m);
            } else {
                this.seen.add(user);
                user.forEach(this::limitingAdd);
            }
        }
    }

    private void limitingAdd(Message message) {
        if (count < minCount) {
            this.recent.add(message);
            count++;
            this.cut = Math.min(message.getUUID(), this.cut);
        } else if (message.getUUID() > this.cut) {
            this.recent.add(message);
            this.cut = this.recent.remove().getUUID();
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
