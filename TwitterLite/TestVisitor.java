public class TestVisitor implements IUserVisitor {
    @Override
    public void visit(User user) {
        System.out.println("Visiting User: "+user.toDetailedString());
    }

    @Override
    public void visit(UserGroup userGroup) {
        System.out.println("Visiting User: "+userGroup.toDetailedString());
        userGroup.children().forEach(u -> u.acceptVisitor(this));
    }
}
