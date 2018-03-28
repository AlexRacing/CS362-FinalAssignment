/**
 * The interface used to implement Element for Visitor pattern.
 */
public interface IUserVisitable {
    /**
     * @param userVisitor UserVisitor to accept.
     */
    void acceptVisitor(IUserVisitor userVisitor);
}
