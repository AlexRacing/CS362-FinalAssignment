import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * We haven't discussed this, but I think it's likely necessary.
 */
public class Tracker implements IUserVisitor, IObservable {
    private static Tracker instance = new Tracker();

    // Observable dependencies
    private ArrayList<IObserver> observers;

    // Databases
    private final Map<String, AbstractUser> nameMap;
    private final Map<Integer, UUIDed>      idMap;
    //private final Map<Integer, Message>      messageMap;

    private Tracker() {
        this.observers = new ArrayList<>();
        nameMap = new HashMap<>();
        idMap = new HashMap<>();
        //messageMap = new HashMap<>();
    }

    public static Tracker getInstance() {
        return instance;
    }

    public void register(AbstractUser newContent) {
        if (newContent == null) return;

        idMap.put(newContent.getUUID(), newContent);
        nameMap.put(newContent.getName(), newContent);

        notifyObservers(newContent);
    }

    public void register(UUIDed newContent) {
        if (newContent == null) return;

        idMap.put(newContent.getUUID(), newContent);

        notifyObservers(newContent);
    }

    public boolean userNameTaken(String name) {
        return nameMap.get(name) != null;
    }

    public AbstractUser getUser(String name) {
        return nameMap.get(name);
    }

    public AbstractUser getUser(int id) {
        UUIDed ided = idMap.get(id);
        return (ided != null && ided instanceof AbstractUser) ? (AbstractUser) ided : null;
    }

    public Message getMessage(int id) {
        UUIDed ided = idMap.get(id);
        return (ided != null && ided instanceof Message) ? (Message) ided : null;
    }

    /**
     * Use this method to completely poll an entire users structure.
     *
     * @param root
     */
    public void registerAll(IUserVisitable root) {
        root.acceptVisitor(this);
    }

    // Visitor related methods

    /**
     * Use this method to completely poll an entire users structure.
     *
     * @param user User to count, including evey message
     */
    @Override
    public void visit(User user) {
        this.register(user);
        user.forEach(this::register);
    }

    /**
     * Use this method to completely poll an entire users structure.
     *
     * @param userGroup UserGroup to count, including every users and message
     */
    @Override
    public void visit(UserGroup userGroup) {
        this.register(userGroup);
        userGroup.children().forEach(u -> u.acceptVisitor(this));
    }

    // Observer related methods

    public void attachObserver(IObserver obs) {
        if (!this.observers.contains(obs)) this.observers.add(obs);
    }

    public void detachObserver(IObserver obs) {
        this.observers.remove(obs);
    }

    public void notifyObservers() {
        for (IObserver obs : this.observers) obs.update(this);
    }

    public void notifyObservers(Object content) {
        for (IObserver obs : this.observers) obs.update(this, content);
    }
}
