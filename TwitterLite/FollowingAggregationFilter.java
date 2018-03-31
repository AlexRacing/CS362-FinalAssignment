/**
 * Implementation of AggregationFilter, filtering out non-followed users.
 */
public class FollowingAggregationFilter extends MessageAggregationFilter {
    protected final User           user;

    public FollowingAggregationFilter(User user, MessageAggregationVisitor aggregator) {
        super(aggregator);
        this.user = user;
    }

    @Override
    protected boolean shouldSkip(User user) {
        return (!this.user.getFollowing().contains(user) && user != this.user) ||
               this.aggregator.shouldSkip(user);
    }
}
