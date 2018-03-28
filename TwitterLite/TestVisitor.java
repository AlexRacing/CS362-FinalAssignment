import java.io.PrintStream;

public class TestVisitor implements IUserVisitor {
    private final PrintStream out;

    public TestVisitor() {this.out = System.out;}

    public TestVisitor(PrintStream out) {this.out = out;}

    @Override
    public void visit(User user) {
        this.out.println("Visiting User: "+user.toDetailedString());
        user.forEach(m -> this.out.println("Visiting User: "+m.toDetailedString()));
    }

    @Override
    public void visit(UserGroup userGroup) {
        this.out.println("Visiting User: "+userGroup.toDetailedString());
        userGroup.children().forEach(u -> u.acceptVisitor(this));
    }
}
