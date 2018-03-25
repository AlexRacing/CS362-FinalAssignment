/**
 * We haven't discussed this, but I think it's likely necessary.
 */
public class StatisticsTracker implements IUserVisitor{
    private static StatisticsTracker instance = new StatisticsTracker();

    private static SentimentEngine sentimentEngine = SentimentEngine.getInstance();

    private static final int SCALE = 10;

    private static long     totalUsers       = 0;
    private static long     totalGroups      = 0;
    private static long     totalMessages    = 0;
    private static long     countTrueNeutral = 0;
    private static long[][] countBySentiment = new long[2][SCALE];

    private static double cummulativeSentiment = 0.0;

    private StatisticsTracker() {}

    public static StatisticsTracker getInstance()
    {
        return instance;
    }

    public void count(User newUser) {
        totalUsers++;
    }

    public void count(UserGroup newUserGroup) {
        totalGroups++;
    }

    public void count(Message newMessage) {
        totalMessages++;

        this.countSentiment(sentimentEngine.sentimentScore(newMessage));
    }

    private void countSentiment(double sentiment) {
        if (sentiment == 0.0) countTrueNeutral++;
        else {
            cummulativeSentiment += sentiment;
            countBySentiment[(sentiment >0) ? 0 : 1][rating(sentiment)]++;
        }
    }

    private int rating(double sentiment) {
        int rating = (int) (Math.abs(sentiment)*SCALE);
        if (rating >= SCALE) rating = SCALE - 1;
        return rating;
    }

    /**
     * Use this method to completely poll an entire user structure.
     *
     * @param root
     */
    public void countAll(IUser root) {
        root.acceptVisitor(this);
    }

    // Visitor

    /**
     * Use this method to completely poll an entire user structure.
     *
     * @param user User to count, including evey message
     */
    @Override
    public void visit(User user) {
        this.count(user);
        user.forEach(this::count);
    }

    /**
     * Use this method to completely poll an entire user structure.
     *
     * @param userGroup UserGroup to count, including every user and message
     */
    @Override
    public void visit(UserGroup userGroup) {
        this.count(userGroup);
        userGroup.forEach(this::count);
    }

    // Getters

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalGroups() {
        return totalGroups;
    }

    public long getTotalMessages() {
        return totalMessages;
    }

    public double averageSentiment() {
        return cummulativeSentiment/totalMessages;
    }

    public double percentMorePositiveThan(double sentiment) {
        long total = 0;
        int rating = rating(sentiment);

        if (sentiment < 0.0) {
            total += countTrueNeutral;
            for (int i = 0; i < SCALE; i++) {
                total += countBySentiment[0][i];
            }
            for (int i = rating-1; i >= 0; i--) {
                total += countBySentiment[1][i];
            }
        } else {
            for (int i = rating+1; i < SCALE; i++) {
                total += countBySentiment[0][i];
            }
        }
        return total/ (double) totalMessages;
    }

    public double percentAtLeastAsPositiveAs(double sentiment) {
        long total = 0;
        int rating = rating(sentiment);

        if (sentiment < 0.0) {
            total += countTrueNeutral;
            for (int i = 0; i < SCALE; i++) {
                total += countBySentiment[0][i];
            }
            for (int i = rating; i >= 0; i--) {
                total += countBySentiment[1][i];
            }
        } else {
            if (sentiment == 0.0) total += countTrueNeutral;
            for (int i = rating; i < SCALE; i++) {
                total += countBySentiment[0][i];
            }
        }
        return total/ (double) totalMessages;
    }

    public double percentRoughlyAsPositiveAs(double sentiment) {
        long total;

        if (sentiment == 0.0) total = countTrueNeutral;
        else {
            total = countBySentiment[(sentiment >0) ? 0 : 1][rating(sentiment)];
        }

        return total/ (double) totalMessages;
    }
}
