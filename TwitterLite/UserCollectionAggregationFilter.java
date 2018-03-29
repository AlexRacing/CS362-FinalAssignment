import java.util.Collection;

/**
 * Implementation of AggregationFilter, filtering out non-followed users.
 */
public class UserCollectionAggregationFilter extends MessageAggregationFilter {
    protected final Collection<User> users;

    public UserCollectionAggregationFilter(Collection<User> users, MessageAggregationVisitor aggregator) {
        super(aggregator);
        this.users = users;
    }

    @Override
    protected boolean shouldSkip(User user) {
        return !this.users.contains(user) || this.aggregator.shouldSkip(user);
    }
}
