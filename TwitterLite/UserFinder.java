import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * We haven't discussed this, but I think it's likely necessary.
 */
public class UserFinder implements IUserVisitor, IObservable {
    private static UserFinder instance = new UserFinder();

    // Observable dependencies
    private ArrayList<IObserver> observers;

    // Databases
    private final Map<String, AbstractUser> nameMap;
    private final Map<Integer, AbstractUser>      idMap;

    private UserFinder() {
        this.observers = new ArrayList<>();
        nameMap = new HashMap<>();
        idMap = new HashMap<>();
    }

    public static UserFinder getInstance() {
        return instance;
    }

    public void register(AbstractUser newContent) {
        if (newContent == null) return;

        idMap.put(newContent.getUUID(), newContent);
        nameMap.put(newContent.getName(), newContent);

        notifyObservers(newContent);
    }

    public boolean userNameTaken(String name) {
        return nameMap.get(name) != null;
    }

    public AbstractUser get(String name) {
        return nameMap.get(name);
    }

    public AbstractUser get(int id) {
        return idMap.get(id);
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
