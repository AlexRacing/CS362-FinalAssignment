public class StringStructureAggregatorVisitor implements IUserVisitor {
    private final StringBuilder out;
    private final String delim;

    public StringStructureAggregatorVisitor() {
        this.out = new StringBuilder();
        this.delim = "\n";
    }

    public StringStructureAggregatorVisitor(StringBuilder out) {
        this.out = out;
        this.delim = "\n";
    }

    public StringStructureAggregatorVisitor(String delim) {
        this.out = new StringBuilder();
        this.delim = delim;
    }

    public StringStructureAggregatorVisitor(StringBuilder out, String delim) {
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
