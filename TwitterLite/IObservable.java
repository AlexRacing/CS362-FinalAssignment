public interface IObservable {
    void attachObserver(IObserver obs);

    void detachObserver(IObserver obs);

    void notifyObservers();
}
