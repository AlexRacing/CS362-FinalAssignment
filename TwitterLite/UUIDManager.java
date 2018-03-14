public class UUIDManager
{
    private static UUIDManager instance = new UUIDManager();
    private static int currentId;
    
    private UUIDManager()
    {
        currentId = 0;
    }

    public static UUIDManager getInstance()
    {
        return instance;
    }
    
    public int getNewUUID()
    {
        return currentId++;
    }
}
