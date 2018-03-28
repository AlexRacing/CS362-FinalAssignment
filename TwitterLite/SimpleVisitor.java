import java.util.function.Consumer;

public class SimpleVisitor implements IUserVisitor {
    protected final Consumer<User> consumer;

    public SimpleVisitor(Consumer<User> consumer) {this.consumer = consumer;}

    @Override
    public void visit(User user) {
        consumer.accept(user);
    }
}
