public interface IObserver {
    void update();

    void update(IObservable source);
}
