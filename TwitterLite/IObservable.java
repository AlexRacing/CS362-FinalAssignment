public interface IObservable
{
    public void attachObserver(IObserver obs);
    public void notifyObservers();
}
