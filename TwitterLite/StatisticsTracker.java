import java.util.ArrayList;

/**
 * We haven't discussed this, but I think it's likely necessary.
 */
public class StatisticsTracker implements IUserVisitor, IObservable {
    private static StatisticsTracker instance = new StatisticsTracker();

    private static SentimentEngine sentimentEngine = SentimentEngine.getInstance();

    private static final int SCALE = 10;

    private static long     totalUsers       = 0;
    private static long     totalGroups      = 0;
    private static long     totalMessages    = 0;
    private static long     countTrueNeutral = 0;
    private static long[][] countBySentiment = new long[2][SCALE];

    private static double cummulativeSentiment  = 0.0;
    private static double cummulativeSentiment2 = 0.0;

    // Observable dependencies
    private ArrayList<IObserver> observers;

    private StatisticsTracker() {
        this.observers = new ArrayList<>();
    }

    public static StatisticsTracker getInstance() {
        return instance;
    }

    public void count(User newUser) {
        totalUsers++;

        notifyObservers(newUser);
    }

    public void count(UserGroup newUserGroup) {
        totalGroups++;

        notifyObservers(newUserGroup);
    }

    public void count(Message newMessage) {
        totalMessages++;

        this.countSentiment(sentimentEngine.sentimentScore(newMessage));

        notifyObservers(newMessage);
    }

    private void countSentiment(double sentiment) {
        if (sentiment == 0.0) countTrueNeutral++;
        else {
            cummulativeSentiment += sentiment;
            cummulativeSentiment2 += sentiment * sentiment;
            countBySentiment[(sentiment > 0) ? 0 : 1][rating(sentiment)]++;
        }
    }

    private int rating(double sentiment) {
        int rating = (int) (Math.abs(sentiment) * SCALE);
        if (rating >= SCALE) rating = SCALE - 1;
        return rating;
    }

    private double sentiment(int rating) {
        double sentiment = rating / ((double) SCALE);
        return sentiment;
    }

    /**
     * Use this method to completely poll an entire user structure.
     *
     * @param root
     */
    public void countAll(IUserVisitable root) {
        root.acceptVisitor(this);
    }

    // Visitor related methods

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

    // Observer related methods

    public void attachObserver(IObserver obs) {
        if (!this.observers.contains(obs)) this.observers.add(obs);
    }

    public void detachObserver(IObserver obs) {
        this.observers.remove(obs);
    }

    public void notifyObservers() {
        for (IObserver obs : this.observers) obs.update(this);
    }

    public void notifyObservers(Object content) {
        for (IObserver obs : this.observers) obs.update(this, content);
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
        return cummulativeSentiment / totalMessages;
    }

    public double stdSentiment() {
        double avg = averageSentiment();
        return Math.sqrt(cummulativeSentiment2 / totalMessages - avg * avg);
    }

    public double percentMorePositiveThan(double sentiment) {
        long total  = 0;
        int  rating = rating(sentiment);

        if (sentiment < 0.0) {
            total += countTrueNeutral;
            for (int i = 0; i < SCALE; i++) {
                total += countBySentiment[0][i];
            }
            for (int i = rating - 1; i >= 0; i--) {
                total += countBySentiment[1][i];
            }
        } else {
            for (int i = rating + 1; i < SCALE; i++) {
                total += countBySentiment[0][i];
            }
        }
        return total / (double) totalMessages;
    }

    public double percentAtLeastAsPositiveAs(double sentiment) {
        long total  = 0;
        int  rating = rating(sentiment);

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
        return total / (double) totalMessages;
    }

    public double percentRoughlyAsPositiveAs(double sentiment) {
        long total;

        if (sentiment == 0.0) total = countTrueNeutral;
        else {
            total = countBySentiment[(sentiment > 0) ? 0 : 1][rating(sentiment)];
        }

        return total / (double) totalMessages;
    }

    /**
     * This method should give a rough estimate of the positive and negative tweets,
     * dynamically determined to try to split the messages roughly equally between
     * positive, negative and neutral, where sentiment 0 messages are forced to be neutral.
     * This method returns a 2 by 2 array of doubles, of the form:
     * split[0][0] = percent positive
     * split[1][0] = sentiment boundary between positive and neutral
     * split[0][1] = percent negative
     * split[1][1] = sentiment boundary between negative and neutral
     * <p>
     * This method will not split evenly if messages are not well distributed.
     *
     * @return The split statistics.
     */
    public double[][] getRoughSentimentSplit() {
        double[][] split = new double[2][2];

        for (int i = 0; i < 2; i++) {
            int j;
            for (j = SCALE - 1; j >= 0 && split[0][i] < .333333333; j--) {
                split[0][i] += countBySentiment[i][j] / ((double) totalMessages);
            }
            split[1][i] = ((i == 0) ? 1 : -1) * sentiment(j + 1);
        }

        return split;
    }
}
