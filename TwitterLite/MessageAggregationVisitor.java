import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Abstract implementation of a message aggregator, as a visitor, implementing the
 * Template Method pattern and participant in the Decorator pattern.
 */
public abstract class MessageAggregationVisitor implements IUserVisitor, IObserver {
    // The users seen and most recent messages aggregated from that user.
    protected final Map<User, Message> seen;

    public MessageAggregationVisitor() {
        this.seen = new HashMap<>();
    }

    // Observer related methods

    /**
     * This method will be slow and should be avoided...
     */
    @Override
    public void update() {
        this.seen.keySet().forEach(this::visit);
    }

    @Override
    public void update(IObservable source) {
        if (source instanceof User) {
            User u = (User) source;
            if (this.shouldSkip(u) && this.seen.get(u) != null) {
                this.purgeUser(u);
                this.seen.remove(u);
            } else this.visit(u);
        }
    }

    @Override
    public void update(IObservable source, Object content) {
        if (content instanceof Message) {
            this.newMessage((Message) content);
        }
    }

    /**
     * Call this method when finishing with an aggregator.
     */
    protected void stopObserving() {
        this.seen.keySet().forEach(x -> x.detachObserver(this));
    }

    // Visitor related methods

    /**
     * Considers the user's messages for addition.
     *
     * @param user User to traverse
     */
    @Override
    public void visit(User user) {
        if (this.shouldSkip(user)) return;

        Message lastSeen = this.seen.get(user);

        if (lastSeen != null) { // If the user has been seen before
            Message newMostRecent = lastSeen;
            for (Message m : user)
                if (m.isNewerThan(lastSeen)) {
                    if (m.isNewerThan(newMostRecent)) newMostRecent = m;
                    this.consider(m);
                }
        } else { // If the user has not been seen before
            user.attachObserver(this);
            for (Message m : user) {
                if (m.isNewerThan(lastSeen)) lastSeen = m;
                this.consider(m);
            }
            this.seen.put(user, lastSeen); // The user has now been seen
        }
    }

    /**
     * Used to add newly created messages to the feed.
     *
     * @param message Message to consider
     */
    public void newMessage(Message message) {
        if (!this.shouldSkip(message.getOP())) this.consider(message);
    }

    // Template method related methods

    /**
     * If the user should be skipped.
     *
     * @param user User to consider
     *
     * @return Whether the user should be skipped.
     */
    protected abstract boolean shouldSkip(User user);

    /**
     * Consider this message for aggregation using the criteria of the subclass.
     *
     * @param message The message to possibly add.
     */
    protected abstract void consider(Message message);

    /**
     * Requests a user be purged from the feed.
     *
     * @param user The user to purge.
     */
    protected abstract void purgeUser(User user);

    /**
     * @return Message feed as queue, most recent to least recent.
     */
    public abstract Queue<Message> getFeed();
}
