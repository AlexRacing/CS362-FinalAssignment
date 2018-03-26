import java.util.Objects;

public class Message
{
    private int uuid;
    private IUser op; // Should this be User?
    private String message;
    
    public Message(IUser op, String message)
    {
        this.uuid = UUIDManager.getInstance().getNewUUID();
        this.op = op;
        this.message = message;
    }
    
    public int getUUID()
    {
        return this.uuid;
    }
    
    public IUser getOP()
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
