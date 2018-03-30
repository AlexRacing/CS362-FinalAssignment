import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SimpleSentimentEngine extends SentimentEngine {
    private static final String DATA_FILE = "SentiWords_1.1.txt";
    private static final String CLEAN_REGEX = ".,!?~#@*+&\"";
    private static final String SPACES_REGEX = "_";
    private static Map<String, Double> scoreMap = readScores();

    private static Map<String,Double> readScores() {
        Map<String,Double> map = new HashMap<>();
        String line, word;
        Double value, previous;

        File dataFile;
        try {
            dataFile = new File(DATA_FILE);
            try (Scanner data = new Scanner(dataFile)) {
                while (data.hasNext()) {
                    line = data.nextLine();

                    if (line.charAt(0) == '#') continue;

                    word = line.substring(0, line.indexOf('#'));
                    value = Double.valueOf(line.substring(line.indexOf('\t')));

                    previous = map.get(word);

                    // Average if already there, this assumes words occur at most twice
                    map.put(word, (previous != null) ? (value + previous)/2 : value);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Data file: "+DATA_FILE+" does not exist.");
        }

        return map;
    }

    /**
     * Will check bigrams, but not trigrams.
     *
     * @param message The message to determine the sentiment of
     *
     * @return
     */
    @Override
    public double sentimentScore(Message message) {
        return sentimentScore(message.getMessage());
    }

    /**
     * Will check bigrams, but not trigrams.
     *
     * @param text The message to determine the sentiment of
     *
     * @return
     */
    public double sentimentScore(String text) {
        String last = null;
        double score = 0;

        Double value, bigram, previous = null;

        String[] words = strip(text).split(" ");

        for (String w : words) {
            w = cleanWord(w);

            value = scoreMap.get(w);
            bigram = (last != null) ? scoreMap.get(last+'_'+w) : null;

            if (bigram != null) {
                if (previous != null) bigram -= previous; // replace with bigram
                score += bigram;
            } else if (value != null) score += value;
            else if ((w = root(w)) != null) { // Try to getUser the root and try again
                value = scoreMap.get(w);
                bigram = (last != null) ? scoreMap.get(last + '_' + w) : null;

                // try again
                if (bigram != null) {
                    if (previous != null) bigram -= previous; // replace with bigram
                    score += bigram;
                } else if (value != null) score += value;
            }

            last = w;
            previous = value;
        }

        return score/words.length;
    }

    private String strip(String text) {
        text = text.toLowerCase().replaceAll(SPACES_REGEX, " ");
        text = text.trim().replaceAll(CLEAN_REGEX, "");
        return text;
    }

    private String cleanWord(String w) {
        // Clean initial and final single quotes
        while (w.charAt(0) == '\'') w = w.substring(1);
        while (w.charAt(w.length()-1) == '\'') w = w.substring(0, w.length()-2);

        // Clean possessives (misses plurals, should be caught by rooting though.)
        if (w.endsWith("'s")) {
            w = w.substring(0, w.length()-3);
        }

        return w;
    }

    /**
     * Simple rooting attempt, misses odd plurals and verbs ending in e's.
     * The ed's may be overkill for this dataset.
     * @param w
     * @return
     */
    private String root(String w) {
        if (w.charAt(w.length()-1) == 's') return w.substring(0, w.length()-2);
        //if (w.endsWith("ed")) return w.substring(0, w.length()-3);
        return null; // No root found
    }
}
