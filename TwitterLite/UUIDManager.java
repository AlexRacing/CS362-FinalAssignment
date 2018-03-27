public class UUIDManager
{
   /*
    *   Singleton to generate UUIDs for whatever requests it
    *   Functional up to ~2.1 billion UUIDs
    *   Sequential--the lower the UUID, the older it is
    *   Therefore comparing age is a matter of comparing UUIDs
    */

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
