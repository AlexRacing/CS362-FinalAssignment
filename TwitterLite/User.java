import java.util.ArrayList;

public class User implements IUser, IObserver, IObservable
{
    private int uuid;
    private ArrayList<IObserver> followers;
    private ArrayList<IObserver> following;
    
    public User()
    {
        uuid = UUIDManager.getInstance().getNewUUID();
        followers = new ArrayList<IObserver>();
        following = new ArrayList<IObserver>();
    }
    
    public void attachObserver(IObserver obs)
    {
        followers.add(obs);
    }
    
    public void notifyObservers()
    {
        for (IObserver obs : followers)
        {
            obs.update();
        }
    }
    
    public void update()
    {
    }
}
