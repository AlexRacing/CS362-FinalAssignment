import java.io.PrintStream;

public class StringAgregatorVisitor implements IUserVisitor {
    private final PrintStream out;

    public StringAgregatorVisitor() {this.out = System.out;}

    public StringAgregatorVisitor(PrintStream out) {this.out = out;}

    @Override
    public void visit(User user) {
        this.out.println("Visiting User: "+user.toDetailedString());
    }

    @Override
    public void visit(UserGroup userGroup) {
        this.out.println("Visiting User: "+userGroup.toDetailedString());
        userGroup.children().forEach(u -> u.acceptVisitor(this));
    }
}
