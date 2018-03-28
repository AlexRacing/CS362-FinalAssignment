/**
 * Interface used for Observer pattern.
 */
public interface IObserver {
    /**
     * Notifies of an update.
     */
    void update();

    /**
     * Notifies of an update.
     *
     * @param source The source of the change.
     */
    void update(IObservable source);

    /**
     * Notifies of an update.
     *
     * @param source The source of the change.
     * @param content The content of the change.
     */
    void update(IObservable source, Object content);
}
