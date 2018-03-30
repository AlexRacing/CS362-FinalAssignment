public class TestSentiment {
    /**
     * Output should be:
     *
         Test 1: 0.0
         Test 2: 0.12233333333333334
         Test 3: 0.130958
         Test 4: -0.04132333333333333
         Test 5: 0.149815
         Test 6: -0.08264666666666666
     *
     * @param args
     */
    public static void main(String[] args) {
        SentimentEngine engine = SentimentEngine.getInstance();

        System.out.println("Test 1: "+engine.sentimentScore("Hello World!"));

        System.out.println("Test 2: "+engine.sentimentScore(
                "COBOL is an awesome language, and a good use of our time."));

        System.out.println("Test 3: "+engine.sentimentScore("Lisp is a beautiful language."));

        System.out.println("Test 4: "+engine.sentimentScore("Trump isn't terrible."));

        System.out.println("Test 5: "+engine.sentimentScore("Truth is a lie."));

        System.out.println("Test 6: "+engine.sentimentScore("Trump Trump Trump!"));
    }
}
