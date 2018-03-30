import java.util.Collection;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This class Adapts our users constructs into the JTree model.
 */
public class TreeNodeAdapter extends DefaultMutableTreeNode implements IObserver {

    protected TreeNodeAdapter(AbstractUser content) {
        super(content, content instanceof AbstractCompositeUser);
        content.attachObserver(this);
        //System.out.println("Created: "+this.toDetailedString());
    }

    protected TreeNodeAdapter(AbstractUser content, boolean populate) {
        super(content, content instanceof AbstractCompositeUser);
        content.attachObserver(this);
        if (populate && content instanceof AbstractCompositeUser) {
            ((AbstractCompositeUser) content).children().forEach(this::populate);
        }
        //System.out.println(((populate)?"Populated: ":"Created: ")+this.toDetailedString());
    }

    public void add(AbstractUser abstractUser) {
        this.add(abstractUser, false);
    }

    public void populate(AbstractUser abstractUser) {
        this.add(abstractUser, true);
    }

    public void add(AbstractUser abstractUser, boolean populate) {
        super.add(new TreeNodeAdapter(abstractUser, populate));
        //System.out.println(this+((populate) ? " Populated " : " Added: ")+abstractUser);
    }

    public void addIfMissing(AbstractUser abstractUser) {
        this.addIfMissing(abstractUser, false);
    }

    public void populateIfMissing(AbstractUser abstractUser) {
        this.addIfMissing(abstractUser, true);
    }

    public void addIfMissing(AbstractUser abstractUser, boolean populate) {
        if (!anyChildContains(abstractUser)) {
            super.add(new TreeNodeAdapter(abstractUser, populate));
        }
    }

    protected void rebind() {
        //System.out.println(this+" rebinding...");
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
            if (anyChildContains(u) && !composite.children().contains(u)) remove(u);
            else if (composite.children().contains(u)) this.populate(u);
        }
    }

    @Override
    public String toString() {
        if (getUserObject() instanceof AbstractUser) {
            return ((AbstractUser) getUserObject()).getName();//+'*'
        }
        return getUserObject().toString();
    }

    @Override
    public void update() {
        //System.out.println(this+" updated...");
        rebind();
    }

    @Override
    public void update(IObservable source) {
        //System.out.println(this+" updated by "+source);
        Object o = getUserObject();
        if (o == null || !(o instanceof AbstractUser)) return;
        if (source.equals(o)) rebind();
    }

    @Override
    public void update(IObservable source, Object content) {
        //System.out.println(this+" updated by "+source+" with "+content);
        Object o = getUserObject();
        if (o == null || !(o instanceof AbstractUser)) return;
        if (source.equals(o)) {
            if (content instanceof AbstractUser) {
                if (inContentsChildren(content)) {
                    addIfMissing((AbstractUser) content);
                } else if (anyChildContains(content)) {
                    this.remove(content);
                }
            }
        }
    }

    protected boolean inContentsChildren(Object o) {
        if (o == null) return false;
        Object uo = this.getUserObject();
        if (uo instanceof AbstractCompositeUser) {
            AbstractCompositeUser u = (AbstractCompositeUser) uo;
            return u.children().contains(o);
        }
        return false;
    }

    protected boolean anyChildContains(Object o) {
        if (o == null) return false;
        if (this.getChildCount() > 0 && this.children != null) {
            return this.children.stream().anyMatch(c -> (c instanceof TreeNodeAdapter) &&
                                                        ((TreeNodeAdapter) c)
                                                                .getUserObject().equals(o));
        }
        return false;
    }

    protected void remove(Object o) {
        if (o == null) return;
        for (Object c : this.children) {
            if ((c instanceof TreeNodeAdapter) && ((TreeNodeAdapter) c).getUserObject().equals(o)) {
                this.remove((TreeNodeAdapter) c);
                break;
            }
        }
    }

    protected void retainIf(Collection collection) {
        if (collection == null) return;
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

    public String toDetailedString() {
        return super.toString()+"{" +
               "in " + parent +
               " containing " + children +
               " wrapping " + userObject +
               " fillable: " + allowsChildren +
               '}';
    }
}
