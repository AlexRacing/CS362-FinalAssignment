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
    
    public getUUID()
    {
        return uuid;
    }
    
    public getOP()
    {
        return op;
    }
    
    public getMessage()
    {
        return message;
    }
}
