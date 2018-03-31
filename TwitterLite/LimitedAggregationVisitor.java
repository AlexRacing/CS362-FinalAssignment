import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Aggregation visitor that functions like a SimpleAggregationVisitor but limits the messages to a set
 * number until it is requested, then it functions exactly as a SimpleAggregationVisitor
 */
public class LimitedAggregationVisitor extends SimpleAggregationVisitor {
    protected final int limit;
    protected int count = 0;
    protected Message cut = null;
    protected final Queue<Message> recent;
    protected boolean messagesRequested = false;

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
        if (messagesRequested) {
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

    @Override
    protected void purgeUser(User user) {
        if (messagesRequested) super.purgeUser(user);
        else for (Iterator<Message> iterator = this.recent.iterator(); iterator.hasNext(); ) {
            Message message = iterator.next();
            if (user.equals(message.getOP())) {
                iterator.remove();
                count--;
            }
        }
    }

    /**
     * @return Message messages as queue, most recent to least recent.
     */
    @Override
    public Collection<Message> getMessages() {
        if (messagesRequested) return super.getMessages();
        else {
            this.messagesRequested = true;
            this.messages.addAll(recent);

            return messages;
        }
    }
}
