import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public abstract class AbstractCompositeUser extends AbstractUser implements Collection<User> {

    /**
     * @param name The name
     */
    protected AbstractCompositeUser(String name) {
        super(name);
    }

    /**
     * @param name The name
     * @param parent The parent
     */
    protected AbstractCompositeUser(String name, AbstractCompositeUser parent) {
        super(name, parent);
    }

    public abstract boolean add(AbstractUser abstractUser);

    public abstract boolean remove(Object o);

    public User spawnUser(String name) {
        User newUser = new User(name, this);
        this.add(newUser);
        StatisticsTracker.getInstance().count(newUser);
        UserFinder.getInstance().register(newUser);
        return newUser;
    }

    public UserGroup spawnUserGroup(String name) {
        UserGroup newGroup = new UserGroup(name, this);
        this.add(newGroup);
        StatisticsTracker.getInstance().count(newGroup);
        UserFinder.getInstance().register(newGroup);
        return newGroup;
    }

    public User getUserByName(String name) {
        for (User u : this) if (u.hasName(name)) return u;
        return null;
    }

    public AbstractUser getContentsByName(String name) {
        for (AbstractUser u : this.children()) {
            if (u.hasName(name)) return u;
            if (u instanceof AbstractCompositeUser) {
                AbstractUser subContent = ((AbstractCompositeUser) u).getContentsByName(name);
                if (subContent != null) return subContent;
            }
        }
        return null;
    }

    public User getUserByID(int id) {
        for (User u : this) if (u.getUUID() == id) return u;
        return null;
    }

    public AbstractUser getContentsByID(int id) {
        for (AbstractUser u : this.children()) {
            if (u.getUUID() == id) return u;
            if (u instanceof AbstractCompositeUser) {
                AbstractUser subContent = ((AbstractCompositeUser) u).getContentsByID(id);
                if (subContent != null) return subContent;
            }
        }
        return null;
    }

    public boolean subContains(Object o) {
        for (AbstractUser u : this.children()) {
            if (u instanceof AbstractCompositeUser && ((AbstractCompositeUser) u).contains(o)) return true;
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        return new ArrayList<>(this).toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return new ArrayList<>((Collection<T>) this).toArray(ts);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        boolean contains = true;
        for (Object o : collection) if(!this.contains(o)) {
            contains = false;
            break;
        }
        return contains;
    }

    @Override
    public boolean addAll(Collection<? extends User> collection) {
        boolean added = true;
        for (User u : collection) if(!this.add(u)) {
            added = false;
        }
        return added;
    }

    // Tree related methods

    /*
     * Not thread safe!
     */
    private class CompositeUserEnumerator implements Enumeration<AbstractUser> {
        private int cursor;

        public CompositeUserEnumerator() {
            this.cursor = 0;
        }

        public CompositeUserEnumerator(int first) {
            if (first >= getChildCount())
                throw new NoSuchElementException();
            this.cursor = first;
        }

        @Override
        public boolean hasMoreElements() {
            return this.cursor != getChildCount();
        }

        @Override
        public AbstractUser nextElement() {
            AbstractUser next = null;
            if (this.hasMoreElements()) { // If there are more
                next = getChildAt(cursor);
                cursor++; // Move on
            } else
                throw new NoSuchElementException(); // This should only occur if asked when empty or done
            return next;
        }
    }

    // Tree related methods

    abstract AbstractUser getChildAt(int i);

    abstract int getChildCount();

    abstract int getIndex(AbstractUser treeNode);

    boolean getAllowsChildren() {
        return true;
    }

    boolean isLeaf() {
        return false;
    }

    abstract Collection<AbstractUser> children();

    Enumeration enumeration() {
        return new CompositeUserEnumerator();
    }
}
