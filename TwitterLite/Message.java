public class Message
{
    int uuid;
    IUser op;
    String message;
    
    public Message(IUser op, String message)
    {
        uuid = UUIDManager.getInstance().getNewUUID();
        this.op = op;
        this.message = message;
    }
    
    public int getUUID()
    {
        return uuid;
    }
    
    public IUser getOP()
    {
        return op;
    }
    
    public String getMessage()
    {
        return message;
    }
}
