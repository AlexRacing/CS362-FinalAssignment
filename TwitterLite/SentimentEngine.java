public abstract class SentimentEngine {
    private static SentimentEngine instance = getSentimentEngine();

    // I think this is the builder pattern
    private static SentimentEngine getSentimentEngine() {
        // Done like this to facilitate replacement later by a more complex engine.
        return new SimpleSentimentEngine();
    }


    public static SentimentEngine getInstance()
    {
        return instance;
    }

    /**
     * The sentiment score will be positive if the sentiment is positive,
     * negative if negative, and the magnitude will larger the more
     * positive or negative it is.
     *
     * @param message The message to determine the sentiment of
     *
     * @return The sentiment score
     */
    public abstract double sentimentScore(Message message);

    public abstract double sentimentScore(String text);
}
