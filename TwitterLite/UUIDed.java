import java.util.Objects;

/**
 * Implements some basic methods for UUIDable objects to prevent duplication.
 */
public class UUIDed implements Comparable<UUIDed> {
    protected final int    uuid;

    public UUIDed() {
        uuid = UUIDManager.getInstance().getNewUUID();
    }

    /**
     * @return The UUID
     */
    public int getUUID() {
        return this.uuid;
    }

    /**
     * Implemented to conform with standard conventions.
     *
     * @param o The object to compare to
     *
     * @return equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractUser abstractUser = (AbstractUser) o;
        return uuid == abstractUser.uuid;
    }

    /**
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public int compareTo(UUIDed uuided) {
        return Integer.compare(this.getUUID(), uuided.getUUID());
    }
}
