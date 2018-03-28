/**
 * Interface used for Observer pattern.
 */
public interface IObservable {
    /**
     * @param obs Observer to attach
     */
    void attachObserver(IObserver obs);

    /**
     * @param obs Observer to detach
     */
    void detachObserver(IObserver obs);

    /**
     * Notifies the observers
     */
    void notifyObservers();

    /**
     * Notifies the observers
     *
     * @param content The content of the change
     */
    void notifyObservers(Object content);
}
