import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Class representing a users group. Participant in Visitor, Composite and Iterator (through Collection) patterns.
 */
public class UserGroup extends AbstractCompositeUser { //
    protected ArrayList<AbstractUser> contents;

    protected int numberUsers;

    public UserGroup(String name) {
        super(name);
        this.contents = new ArrayList<>();
        this.numberUsers = 0;
    }

    public UserGroup(String name, AbstractCompositeUser parent) {
        super(name, parent);
        this.contents = new ArrayList<>();
        this.numberUsers = 0;
    }

    @Override
    public boolean add(AbstractUser abstractUser) {
        if (!this.contents.contains(abstractUser)) {
            this.contents.add(abstractUser);
            abstractUser.setParent(this); // Possibly redundant
            this.numberUsers = this.calculateSize(); // TODO:
            abstractUser.attachObserver(this);
            this.notifyObservers(abstractUser);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (!this.contents.contains(o)) {
            this.contents.remove(o);
            if (o instanceof AbstractUser) {
                ((AbstractUser) o).detachObserver(this);
                this.numberUsers = this.calculateSize(); // TODO:
            }
            this.notifyObservers(o);
            return true;
        }
        return false;
    }

    // Visitor related methods

    @Override
    public void acceptVisitor(IUserVisitor userVisitor) {
        userVisitor.visit(this);
    }

    // Observer related methods

    @Override
    public void update() {
        this.numberUsers = this.calculateSize();
    }

    @Override
    public void update(IObservable source) {
        if (this.contents.contains(source)) {
            if (source instanceof UserGroup) {
                this.numberUsers = this.calculateSize();
            }
        }
    }

    @Override
    public void update(IObservable source, Object content) {
        if (this.contents.contains(source)) {
            if (source instanceof UserGroup) {
                this.numberUsers = this.calculateSize();
            }
        }
    }

    // Collection related methods

    @Override
    public int size() {
        return this.numberUsers;
    }

    private int calculateSize() {
        int userCount = this.contents.size();

        for (AbstractUser u : this.contents) {
            if (u instanceof UserGroup) userCount += ((UserGroup) u).size()-1;
        }

        return userCount;
    }

    @Override
    public boolean add(User u) {
        return this.add((AbstractUser) u);
    }//*/

    @Override
    public boolean isEmpty() {
        return this.numberUsers == 0;
    }

    @Override
    public boolean contains(Object o) {
        return this.contents.contains(o) || subContains(o);
    }

    @Override
    public Iterator<User> iterator() {
        return new UserGroupIterator();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean removed = true;
        for (Object o : collection) if(!this.remove(o)) {
            removed = false;
        }
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean trimmed = true;
        for (Iterator<AbstractUser> iterator = this.contents.iterator(); iterator.hasNext(); ) {
            AbstractUser u = iterator.next();
            if (u instanceof UserGroup && !((UserGroup) u).retainAll(collection)) trimmed = false;
            else if (!collection.contains(u)) iterator.remove();
        }
        return trimmed;
    }

    @Override
    public void clear() {
        for (Iterator<AbstractUser> iterator = this.contents.iterator(); iterator.hasNext(); ) {
            AbstractUser u = iterator.next();
            if (u instanceof UserGroup) ((UserGroup) u).clear();
            else iterator.remove();
        }
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

    // Internal class representing the custom implementation of Iterator.

    /*
     * Not thread safe!
     * Could be generalized if we want to make users have trivial iterators.
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

        /**
         * May throw an exception if the rest of the list is empty usergroups.
         *
         * @return The next valid users
         */
        @Override
        public User next() {
            AbstractUser next;
            User         nextUser = null;

            outer:
            while (nextUser == null) { // While we have yet to fine a next
                if (this.sub != null) { // If there is a sub-iterator, use it
                    if (this.sub.hasNext()) {
                        nextUser = this.sub.next();
                        if (!this.sub.hasNext()) {
                            this.sub = null; // Invalidate if now empty
                            this.cursor++;   // And move on
                        }
                        break outer;
                    }

                    // This should be unreachable, but is here just in case
                    this.sub = null; // This sub-iterator is now empty
                    this.cursor++;   // Move on
                }
                if (this.cursor != contents.size()) { // If there are more
                    next = contents.get(cursor);

                    if (next instanceof UserGroup) { // If the next is a usergroup, make it the sub
                        this.sub = ((UserGroup) next).iterator();
                        continue outer; // try again
                    } else {
                        cursor++; // Else, we have a next, so move on
                        nextUser = (User) next;
                    }
                } else
                    throw new NoSuchElementException(); // This should only occur if asked when empty or done
            }
            return nextUser;
        }

        @Override
        public void remove() {
            if (this.sub != null) this.sub.remove();
            else if (cursor > 0) UserGroup.this.remove(contents.get(--this.cursor));
            else throw new IllegalStateException();
        }

        @Override
        public void forEachRemaining(Consumer<? super User> consumer) {
            while (this.hasNext()) consumer.accept(this.next());
        }
    }

    // Tree related methods

    @Override
    AbstractUser getChildAt(int i) {
        return this.contents.get(i);
    }

    @Override
    int getChildCount() {
        return this.contents.size();
    }

    @Override
    int getIndex(AbstractUser treeNode) {
        return this.contents.indexOf(treeNode);
    }

    @Override
    Collection<AbstractUser> children() {
        return this.contents;
    }
}
