public interface IUserVisitor {
    void visit(User user);
    void visit(UserGroup userGroup);
}
