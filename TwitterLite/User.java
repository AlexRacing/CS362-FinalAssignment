import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;

public class User implements IUserVisitable, IObserver, IObservable, Iterable<Message> {
    private int                    uuid;
    private ArrayList<IObserver>   followers;
    private ArrayList<IObservable> following;
    private ArrayList<Message> messages;
    private MessageFeedVisitor feedVisitor = null;

    public User() {
        this.uuid = UUIDManager.getInstance().getNewUUID();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public void attachObserver(IObserver obs) {
        this.followers.add(obs);
    }

    public void detachObserver(IObserver obs) {
        this.followers.remove(obs);
    }

    public void notifyObservers() {
        for (IObserver obs : this.followers) obs.update(this);
    }

    public void follow(IObservable obs) {
        this.following.add(obs);
        obs.attachObserver(this);
    }

    public void unfollow(IObservable obs) {
        this.following.remove(obs);
        obs.detachObserver(this);
    }

    public void update() {
        this.feedVisitor = null; // The feed is now invalid
    }

    public void update(IObservable source) {
        if (source instanceof User) this.feedVisitor.visit((User) source);
    }

    public ArrayList<IObserver> getFollowers() {
        return followers;
    }

    public ArrayList<IObservable> getFollowing() {
        return following;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public Queue<Message> getFeed() {
        if (this.feedVisitor == null) {
            this.feedVisitor = new MessageFeedVisitor(this);

            this.following.forEach((x) -> {if (x instanceof User) this.feedVisitor.visit((User) x);});
        }

        return this.feedVisitor.getFeed();
    }

    public Queue<Message> getFeed(int limit) {
        if (this.feedVisitor == null) {
            this.feedVisitor = new LimitingMessageFeedVisitor(this, limit);

            this.following.forEach((x) -> {if (x instanceof User) this.feedVisitor.visit((User) x);});
        }

        return this.feedVisitor.getFeed();
    }

    @Override
    public Iterator<Message> iterator() {
        return this.messages.iterator();
    }

    @Override
    public void forEach(Consumer<? super Message> consumer) {
        this.messages.forEach(consumer);
    }

    @Override
    public Spliterator<Message> spliterator() {
        return this.messages.spliterator();
    }

    @Override
    public void acceptVisitor(IUserVisitor userVisitor) {
        userVisitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User messages = (User) o;
        return uuid == messages.uuid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
