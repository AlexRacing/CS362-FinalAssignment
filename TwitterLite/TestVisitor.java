public class TestVisitor implements IUserVisitor {
    private final StringBuilder out;
    private final String delim;

    public TestVisitor() {
        this.out = new StringBuilder();
        this.delim = "\n";
    }

    public TestVisitor(StringBuilder out) {
        this.out = out;
        this.delim = "\n";
    }

    public TestVisitor(String delim) {
        this.out = new StringBuilder();
        this.delim = delim;
    }

    public TestVisitor(StringBuilder out, String delim) {
        this.out = out;
        this.delim = delim;
    }

    @Override
    public void visit(User user) {
        this.out.append(user.toDetailedString());
        this.out.append(delim);
    }

    @Override
    public void visit(UserGroup userGroup) {
        this.out.append(userGroup.toDetailedString());
        this.out.append(delim);
        userGroup.children().forEach(u -> u.acceptVisitor(this));
    }
}
