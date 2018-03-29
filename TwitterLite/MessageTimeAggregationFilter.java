/**
 * Implementation of AggregationFilter, filtering out non-followed users.
 */
public class MessageTimeAggregationFilter extends MessageAggregationFilter {
    protected final long timecode;
    protected final boolean moreRecent;
    protected final boolean inclusive;

    public MessageTimeAggregationFilter(long timecode, boolean moreRecent, boolean inclusive,
                                        MessageAggregationVisitor aggregator) {
        super(aggregator);
        this.timecode = timecode;
        this.moreRecent = moreRecent;
        this.inclusive = inclusive;
    }

    @Override
    protected void consider(Message message) {
        boolean validTime = message.isNewerThan(this.timecode);
        if (!moreRecent) validTime = !validTime;
        if (inclusive) validTime = validTime || message.getTimecode() == timecode;

        if (validTime) this.aggregator.consider(message);
    }
}
