import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class UserGroup implements IUserVisitable, Iterable<User> {
    protected int              uuid;
    protected ArrayList<IUserVisitable> contents;

    public UserGroup() {
        this.uuid = UUIDManager.getInstance().getNewUUID();
        this.contents = new ArrayList<>();
    }

    @Override
    public Iterator<User> iterator() {
        return new UserGroupIterator();
    }

    @Override
    public void forEach(Consumer<? super User> consumer) {
        // This should work but is less pattern-y
        /*for (IUserVisitable u : this.contents) {
            if (u instanceof UserGroup) ((UserGroup) u).forEach(consumer);
            else consumer.accept(u);
        }*/
        this.iterator().forEachRemaining(consumer);
    }

    @Override
    public void acceptVisitor(IUserVisitor userVisitor) {
        userVisitor.visit(this);
    }

    /*
     * Not thread safe!
     * Could be generalized if we want to make user have trivial iterators.
     */
    private class UserGroupIterator implements Iterator<User> {
        private int cursor;
        private Iterator<User> sub = null;

        public UserGroupIterator() {
            this.cursor = 0;
        }

        public UserGroupIterator(int first) {
            if (first >= contents.size())
                throw new NoSuchElementException();
            this.cursor = first;
        }

        @Override
        public boolean hasNext() {
            if (this.sub != null) return this.sub.hasNext();
            return this.cursor != contents.size();
        }

        @Override
        public User next() {
            IUserVisitable next = null;
            while (next == null) {
                if (this.sub != null) {
                    if (this.sub.hasNext()) return this.sub.next();

                    this.sub = null;
                    this.cursor++;
                }
                if (this.cursor != contents.size()) {
                    next = contents.get(cursor);

                    if (next instanceof UserGroup) {
                        this.sub = ((UserGroup) next).iterator();
                        next = null;
                    } else cursor++;
                } else throw new NoSuchElementException();
            }
            return (User) next;
        }

        @Override
        public void remove() {
            if (this.sub != null) this.sub.remove();
            else if (cursor > 0) contents.remove(--this.cursor);
            else throw new IllegalStateException();
        }

        @Override
        public void forEachRemaining(Consumer<? super User> consumer) {
            while (this.hasNext()) consumer.accept(this.next());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGroup users = (UserGroup) o;
        return uuid == users.uuid;
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }
}
