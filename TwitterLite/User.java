import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;

import javax.swing.tree.TreeNode;

/**
 * Class representing a user. Participant in Visitor, Observer, Composite and Iterator patterns.
 */
public class User extends AbstractUser implements Iterable<Message> {
    // Users related to this one
    private ArrayList<User> followers;
    private ArrayList<User> following;

    // The messages created by this user
    private ArrayList<Message> messages;

    // The object that contains the message feed, if created
    private MessageAggregationVisitor feedVisitor = null;

    /**
     * Creates empty user.
     *
     * @param name The User's name.
     */
    public User(String name) {
        super(name);
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    /**
     * Creates empty user.
     *
     * @param name The User's name.
     * @param parent The parent
     */
    public User(String name, AbstractCompositeUser parent) {
        super(name, parent);
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public Message spawnMessage(String message) {
        Message newMessage = new Message(this, message);
        this.messages.add(newMessage);
        StatisticsTracker.getInstance().count(newMessage);
        notifyObservers(newMessage);

        return newMessage;
    }

    // Observer related methods

    public void attachObserver(IObserver obs) {
        super.attachObserver(obs);
        if (obs instanceof User && !this.followers.contains(obs)) this.followers.add((User) obs);
    }

    public void detachObserver(IObserver obs) {
        super.detachObserver(obs);
        if (obs instanceof User) this.followers.remove(obs);
    }

    public void follow(User user) {
        if (!this.following.contains(user)) {
            this.following.add(user);
            user.attachObserver(this);
            this.notifyObservers(user);
        }
    }

    public void unfollow(User user) {
        this.following.remove(user);
        user.detachObserver(this);
        this.notifyObservers(user);
    }

    public void update() {
        this.feedVisitor = null; // The feed is now invalid
    }

    // Getters

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public ArrayList<User> getFollowing() {
        return following;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    // TODO: The following methods should be moved out of user and into the controller classes

    public Queue<Message> getFeed() {
        if (this.feedVisitor == null) {
            this.feedVisitor = new FollowingAggregationFilter(this, new SimpleAggregationVisitor());

            this.following.forEach(this.feedVisitor::visit);
            //or: (x) -> {if (x instanceof User) this.feedVisitor.visit((User) x);});
        }

        return this.feedVisitor.getFeed();
    }

    public Queue<Message> getFeed(int limit) {
        if (this.feedVisitor == null) {
            this.feedVisitor =
                    new FollowingAggregationFilter(this, new LimitedAggregationVisitor(limit));

            this.following.forEach(this.feedVisitor::visit);
            //(x) -> {if (x instanceof User) this.feedVisitor.visit((User) x);});
        }

        return this.feedVisitor.getFeed();
    }

    // Methods related to the getters

    /**
     * Call this method the clear any existing feed and thus prevent further updating of that feed.
     */
    public void clearFeed() {
        this.feedVisitor = null;
    }

    // Visitor related methods

    @Override
    public void acceptVisitor(IUserVisitor userVisitor) {
        userVisitor.visit(this);
    }

    // Iterator related methods

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


    // Tree related methods
    /*

    public TreeNode getChildAt(int i) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public int getIndex(AbstractUser treeNode) {
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration children() {
        return null;
    }//*/
}
