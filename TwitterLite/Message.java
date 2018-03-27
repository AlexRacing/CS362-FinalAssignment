import java.util.Objects;

public class Message
{
    private int uuid;
    private IUserVisitable op;       // Interface to allow extension for groups to post messages, in the future
    private String message;
    
    public Message(IUserVisitable op, String message)
    {
        this.uuid = UUIDManager.getInstance().getNewUUID();
        this.op = op;
        this.message = message;
    }
    
    public int getUUID()
    {
        return this.uuid;
    }
    
    public IUserVisitable getOP()
    {
        return this.op;
    }
    
    public String getMessage()
    {
        return this.message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return uuid == message.uuid;
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }
}
