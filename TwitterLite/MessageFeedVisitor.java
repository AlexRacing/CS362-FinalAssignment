import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class MessageFeedVisitor implements IUserVisitor {
    protected final User           user;
    protected final Queue<Message> feed;
    protected final Set<User>      seen;

    public MessageFeedVisitor(User user) {
        this.user = user;
        this.feed = new PriorityQueue<>(Comparator.comparingInt(Message::getUUID)); // This might be reversed...
        this.seen = new HashSet<>();
    }

    @Override
    public void visit(User user) {
        if (!this.user.getFollowing().contains(user)) return;

        if (this.seen.contains(user)) {
            for (Message m : user) if (!this.feed.contains(m)) this.feed.add(m);
        } else {
            this.seen.add(user);
            user.forEach(this.feed::add);
        }
    }

    @Override
    public void visit(UserGroup userGroup) {
        userGroup.forEach(this::visit);
    }

    public void newMessage(Message message) {
        this.feed.add(message);
    }

    /**
     * @return Message feed as queue, most recent to least recent.
     */
    public Queue<Message> getFeed() {
        return feed;
    }
}
