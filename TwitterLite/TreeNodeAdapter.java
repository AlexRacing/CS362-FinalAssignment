import javax.swing.tree.DefaultMutableTreeNode;

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
            this.children.removeIf(c -> !(c instanceof TreeNodeAdapter) ||
                                        !composite.contains(((TreeNodeAdapter) c).getUserObject()));
            composite.forEach(this::populateIfMissing);
        }
    }

    /*
    protected void rebind(AbstractUser u) {
        Object o = getUserObject();
        if (o == null || !(o instanceof AbstractUser)) return;

        AbstractUser u = (AbstractUser) o;

        if (u instanceof AbstractCompositeUser) {
            AbstractCompositeUser composite = (AbstractCompositeUser) u;
            this.children.removeIf(c -> !(c instanceof TreeNodeAdapter) ||
                                        !composite.contains(((TreeNodeAdapter) c).getUserObject()));
            composite.forEach(this::populateIfMissing);
        }
    }//*/

    @Override
    public String toString() {
        return getUserObject().toString();
    }

    @Override
    public void update() {
        rebind();
    }
/*
    @Override
    public void update(IObservable source) {
        if (this.childContains(source)) {
            AbstractUser content = (AbstractUser) this.getUserObject();
            if (content instanceof AbstractCompositeUser) {
                AbstractCompositeUser
            }
        }
    }
//*/
    protected boolean childContains(Object o) {
        if (this.getChildCount() > 0 && this.children != null) {
            return this.children.stream().anyMatch(c -> (c instanceof TreeNodeAdapter) &&
                                                        o.equals(((TreeNodeAdapter) c)
                                                                         .getUserObject()));
        }
        return false;
    }

    /*
    @Override
    public void remove(MutableTreeNode mutableTreeNode) {
        super.remove(mutableTreeNode);
    }

    @Override
    public void setUserObject(Object o) {
        super.setUserObject(o);
    }

    @Override
    public Object getUserObject() {
        return super.getUserObject();
     */
}
