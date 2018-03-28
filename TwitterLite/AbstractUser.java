import java.util.ArrayList;

/**
 * Abstract class used for the Composite pattern.
 */
public abstract class AbstractUser extends UUIDed implements IObserver, IObservable, IUserVisitable {
    protected final String name;

    // Observable dependencies
    protected ArrayList<IObserver> observers;

    /**
     * @param name The name
     */
    protected AbstractUser(String name) {
        super();
        this.name = name;
        this.observers = new ArrayList<>();
    }

    /**
     * @return The name
     */
    public String getName() {
        return this.name;
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

    //protected int size() {return 1;}
}
