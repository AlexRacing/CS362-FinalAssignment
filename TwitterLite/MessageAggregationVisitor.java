import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Abstract implementation of a message aggregator, as a visitor, implementing the
 * Template Method pattern and participant in the Decorator pattern.
 */
public abstract class MessageAggregationVisitor implements IUserVisitor, IObserver, IObservable {
    // The users seen and most recent messages aggregated from that users.
    protected final Map<User, Message>   seen;

    // Observable dependencies
    protected       ArrayList<IObserver> observers;

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
        this.notifyObservers();
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
        this.notifyObservers();
    }

    @Override
    public void update(IObservable source, Object content) {
        if (content instanceof Message) {
            this.newMessage((Message) content);
        }
        this.notifyObservers();
    }

    // Observer related methods

    @Override
    public void attachObserver(IObserver obs) {
        //System.out.print(this.name+" attaching "+obs+"; ");
        if (!this.observers.contains(obs)) {
            this.observers.add(obs);
            //System.out.println("Successful!");
        }
        //else System.out.println("Failed!");
    }

    @Override
    public void detachObserver(IObserver obs) {
        this.observers.remove(obs);
    }

    @Override
    public void notifyObservers() {
        for (IObserver obs : this.observers) obs.update(this);
    }

    @Override
    public void notifyObservers(Object content) {
        for (IObserver obs : this.observers) obs.update(this, content);
    }

    /**
     * Call this method when finishing with an aggregator.
     */
    protected void stopObserving() {
        this.seen.keySet().forEach(x -> x.detachObserver(this));
    }

    // Visitor related methods

    /**
     * Considers the users's messages for addition.
     *
     * @param user User to traverse
     */
    @Override
    public void visit(User user) {
        if (this.shouldSkip(user)) return;

        Message lastSeen = this.seen.get(user);

        if (lastSeen != null) { // If the users has been seen before
            Message newMostRecent = lastSeen;
            for (Message m : user)
                if (m.isNewerThan(lastSeen)) {
                    if (m.isNewerThan(newMostRecent)) newMostRecent = m;
                    this.consider(m);
                }
        } else { // If the users has not been seen before
            user.attachObserver(this);
            for (Message m : user) {
                if (m.isNewerThan(lastSeen)) lastSeen = m;
                this.consider(m);
            }
            this.seen.put(user, lastSeen); // The users has now been seen
        }

        notifyObservers();
    }

    /**
     * Used to add newly created messages to the feed.
     *
     * @param message Message to consider
     */
    public void newMessage(Message message) {
        if (!this.shouldSkip(message.getOP())) {
            this.consider(message);
            this.notifyObservers();
        }
    }

    // Template method related methods

    /**
     * If the users should be skipped.
     *
     * @param user User to consider
     *
     * @return Whether the users should be skipped.
     */
    protected abstract boolean shouldSkip(User user);

    /**
     * Consider this message for aggregation using the criteria of the subclass.
     *
     * @param message The message to possibly add.
     */
    protected abstract void consider(Message message);

    /**
     * Requests a users be purged from the feed.
     *
     * @param user The users to purge.
     */
    protected abstract void purgeUser(User user);

    /**
     * @return Message feed as queue, most recent to least recent.
     */
    public abstract Queue<Message> getFeed();
}
