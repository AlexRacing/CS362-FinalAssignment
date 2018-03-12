public class UUIDManager
{
    private static UUIDManager instance = new UUIDManager();
    
    private UUIDManager()
    {
    }

    public UUIDManager getInstance()
    {
        return instance;
    }
}
