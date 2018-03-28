import java.util.ArrayList;

/**
 * Abstract class used for the Composite pattern.
 */
public abstract class AbstractUser extends UUIDed implements IObserver, IObservable, IUserVisitable {
    protected final String name;
    protected AbstractCompositeUser parent;

    // Observable dependencies
    protected ArrayList<IObserver> observers;

    /**
     * @param name The name
     */
    protected AbstractUser(String name) {
        super();
        this.name = name;
        this.parent = null;
        this.observers = new ArrayList<>();
    }

    /**
     * @param name The name
     * @param parent The parent
     */
    protected AbstractUser(String name, AbstractCompositeUser parent) {
        super();
        this.name = name;
        this.parent = parent;
        this.observers = new ArrayList<>();
    }

    /**
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    protected void setParent(AbstractCompositeUser newParent) {
        this.parent = newParent;
    }

    public AbstractUser getParent() {
        return this.parent;
    }

    public void removeFromParent() {
        this.parent.remove(this);
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

    // Observer related stubs

    @Override
    public void update() {

    }

    @Override
    public void update(IObservable source) {

    }

    @Override
    public void update(IObservable source, Object content) {

    }

    // Tree related method defaults

    int getChildCount() {return 0;}

    boolean getAllowsChildren() {
        return false;
    }

    boolean isLeaf() {
        return true;
    }
}
