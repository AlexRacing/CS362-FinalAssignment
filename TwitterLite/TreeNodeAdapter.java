import java.util.Collection;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This class Adapts our users constructs into the JTree model.
 */
public class TreeNodeAdapter extends DefaultMutableTreeNode implements IObserver {

    protected TreeNodeAdapter(AbstractUser content) {
        super(content, content instanceof AbstractCompositeUser);
    }

    protected TreeNodeAdapter(AbstractUser content, boolean populate) {
        super(content, content instanceof AbstractCompositeUser);
        content.attachObserver(this);
        if (populate && content instanceof AbstractCompositeUser) {
            ((AbstractCompositeUser) content).children().forEach(this::populate);
        }
    }

    public void add(AbstractUser abstractUser) {
        this.add(abstractUser, false);
    }

    public void populate(AbstractUser abstractUser) {
        this.add(abstractUser, true);
    }

    public void add(AbstractUser abstractUser, boolean populate) {
        super.add(new TreeNodeAdapter(abstractUser, populate));
    }

    public void addIfMissing(AbstractUser abstractUser) {
        this.addIfMissing(abstractUser, false);
    }

    public void populateIfMissing(AbstractUser abstractUser) {
        this.addIfMissing(abstractUser, true);
    }

    public void addIfMissing(AbstractUser abstractUser, boolean populate) {
        if (!childContains(abstractUser)) {
            super.add(new TreeNodeAdapter(abstractUser, populate));
        }
    }

    protected void rebind() {
        Object o = getUserObject();
        if (o == null || !(o instanceof AbstractUser)) return;

        AbstractUser u = (AbstractUser) o;

        if (u instanceof AbstractCompositeUser) {
            AbstractCompositeUser composite = (AbstractCompositeUser) u;
            this.retainIf(composite.children());
            composite.forEach(this::populateIfMissing);
        }
    }

    protected void rebind(AbstractUser u) {
        Object o = getUserObject();
        if (o == null || !(o instanceof AbstractUser)) return;
        if (u.equals(o)) rebind();

        if (o instanceof AbstractCompositeUser) {
            AbstractCompositeUser composite = (AbstractCompositeUser) o;
            if (childContains(u) && !composite.children().contains(u)) remove(u);
            else if (!composite.children().contains(u)) this.populate(u);
        }
    }

    @Override
    public String toString() {
        if (getUserObject() instanceof AbstractUser) {
            return ((AbstractUser) getUserObject()).getName();
        }
        return getUserObject().toString();
    }

    @Override
    public void update() {
        rebind();
    }

    @Override
    public void update(IObservable source) {
        Object o = getUserObject();
        if (o == null || !(o instanceof AbstractUser)) return;
        if (source.equals(o)) rebind();
    }

    @Override
    public void update(IObservable source, Object content) {
        Object o = getUserObject();
        if (o == null || !(o instanceof AbstractUser)) return;
        if (source.equals(o)) {
            if (content instanceof AbstractUser && childContains(content)) {
                addIfMissing((AbstractUser) content);
            } else {
                this.remove(content);
            }
        }
    }

    protected boolean childContains(Object o) {
        if (this.getChildCount() > 0 && this.children != null) {
            return this.children.stream().anyMatch(c -> (c instanceof TreeNodeAdapter) &&
                                                        o.equals(((TreeNodeAdapter) c)
                                                                         .getUserObject()));
        }
        return false;
    }

    protected void remove(Object o) {
        for (Object c : this.children) {
            if ((c instanceof TreeNodeAdapter) && o.equals(((TreeNodeAdapter) c).getUserObject())) {
                this.remove((TreeNodeAdapter) c);
                break;
            }
        }
    }

    protected void retainIf(Collection collection) {
        for (Object c : this.children) {
            if ((c instanceof TreeNodeAdapter) &&
                !collection.contains(((TreeNodeAdapter) c).getUserObject())) {
                this.remove((TreeNodeAdapter) c);
            }
        }
    }

    @Override
    public void setUserObject(Object o) {
        super.setUserObject(o);
        rebind();
    }
}
