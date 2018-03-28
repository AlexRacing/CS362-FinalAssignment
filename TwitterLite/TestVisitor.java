public class TestVisitor implements IUserVisitor {
    @Override
    public void visit(User user) {
        System.out.println("Visiting User: "+user);
    }

    @Override
    public void visit(UserGroup userGroup) {
        System.out.println("Visiting User: "+userGroup);
        userGroup.forEach(this::visit);
    }
}
