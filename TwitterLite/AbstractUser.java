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

    public boolean hasName(String name) {
        return this.getName().equals(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String toDetailedString() {
        return super.toString()+"{" +
               "'" + name + '\'' +
               " in " + parent +
               " watched by " + observers +
               " id: " + uuid +
               '}';
    }

    protected void setParent(AbstractCompositeUser newParent) {
        this.parent = newParent;
    }

    public AbstractUser getParent() {
        return this.parent;
    }

    public AbstractCompositeUser getRoot() {
        if (this.parent == null) {
            if (this instanceof AbstractCompositeUser) return (AbstractCompositeUser) this;
            else return null;
        }
        AbstractCompositeUser root = this.parent;
        while (root.parent != null) root = root.parent;
        return root;
    }

    public void removeFromParent() {
        this.parent.remove(this);
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
