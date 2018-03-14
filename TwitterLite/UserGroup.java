public class UserGroup implements IUser
{
    int uuid;
    
    public UserGroup()
    {
        uuid = UUIDManager.getInstance().getNewUUID();
    }
}
