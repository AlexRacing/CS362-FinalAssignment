/**
 * Interface for implementing Visitor Pattern.
 */
public interface IUserVisitor {
    /**
     * @param user User to operate on.
     */
    void visit(User user);

    /**
     * @param userGroup UserGroup to operate on.
     */
    default void visit(UserGroup userGroup) {
        userGroup.children().forEach(u -> u.acceptVisitor(this));
    }
}
