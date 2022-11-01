package org.cis120;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the class where everything you've worked on thus far comes together!
 * You can see that we've provided a path to a CSV file full of tweets and the
 * column from which they can be extracted. When run as an application, this
 * program builds a Markov Chain from the training data in the CSV file,
 * generates 10 random tweets, and prints them to the terminal.
 * <p>
 * This class also provides the writeTweetsToFile method, which can be used to
 * create a file containing randomly generated tweets.
 * <p>
 * Note: All IOExceptions thrown by writers should be caught and handled
 * properly.
 */
public class TwitterBot {

    /**
     * This is a path to the CSV file containing the tweets. The main method
     * below uses the tweets in this file when calling Twitterbot. If you want
     * to run the Twitterbot on the other files we provide, change this path to
     * a different file. (You may need to adjust the TWEET_COLUMN too.)
     */
    static final String PATH_TO_TWEETS = "files/dog_feelings_tweets.csv";
    static final int TWEET_COLUMN = 2;
    static final String PATH_TO_OUTPUT_TWEETS = "files/generated_tweets.txt";

    // The MarkovChain you'll be using to generate tweets
    MarkovChain mc;
    NumberGenerator ng;
    private int tweetC;
    BufferedReader f;

    /**
     * Given a column and a buffered reader, initializes the TwitterBot by
     * training the MarkovChain with sentences sourced from the reader. Uses
     * the RandomNumberGenerator().
     *
     * @param br          - a buffered reader containing tweet data
     * @param tweetColumn - the column in the reader where the text of the tweet
     *                    itself is stored
     */
    public TwitterBot(BufferedReader br, int tweetColumn) {
        this(br, tweetColumn, new RandomNumberGenerator());
    }

    /**
     * Given a column and a buffered reader, initializes the TwitterBot by
     * training the MarkovChain with all the sentences obtained as training data
     * from the buffered reader.
     *
     * @param br          - a buffered reader containing tweet data
     * @param tweetColumn - the column in the buffered reader where the text
     *                    of the tweet itself is stored
     * @param ng          - A NumberGenerator for the ng field, also to be
     *                    passed to MarkovChain
     */
    public TwitterBot(BufferedReader br, int tweetColumn, NumberGenerator ng) {
        mc = new MarkovChain(ng);
        this.ng = ng;
        tweetC = tweetColumn;
        f = br;
        List<List<String>> words = TweetParser.csvDataToTrainingData(f, tweetC);
        for (int i = 0; i < words.size(); i++) {
            this.mc.train((words.get(i)).iterator());
        }
    }

    /**
     * Given a List of Strings, prints those Strings to a file (one String per
     * line in the file). This method uses BufferedWriter, the flip side to
     * BufferedReader. Ensure that each tweet you generate is written on its own
     * line in the file produced.
     * <p>
     * You may assume none of the arguments or strings passed in will be null.
     * <p>
     * If the process of writing the data triggers an IOException, you should
     * catch it and stop writing. (You can also print an error message to the
     * terminal, but we will not test that behavior.)
     *
     * @param stringsToWrite - A List of Strings to write to the file
     * @param filePath       - the string containing the path to the file where
     *                       the tweets should be written
     * @param append         - a boolean indicating whether the new tweets
     *                       should be appended to the current file or should
     *                       overwrite its previous contents
     */
    public void writeStringsToFile(
            List<String> stringsToWrite, String filePath,
            boolean append
    ) {
        try {
            File file = Paths.get(filePath).toFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
            for (int i = 0; i < stringsToWrite.size(); i++) {
                bw.write(stringsToWrite.get(i));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }


    }

    /**
     * Generates tweets and writes them to a file.
     *
     * @param numTweets - the number of tweets that should be written
     * @param numChars  - the number of characters in each tweet
     * @param filePath  - the path to a file to write the tweets to
     * @param append    - a boolean indicating whether the new tweets should be
     *                  appended to the current file or should overwrite its
     *                  previous contents
     */
    public void writeTweetsToFile(
            int numTweets, int numChars, String filePath,
            boolean append
    ) {
        writeStringsToFile(generateTweets(numTweets, numChars), filePath, append);
    }

    /**
     * Generates a tweet of a given number of words by using the populated
     * MarkovChain. Remember in the writeup where we explained how to use
     * MarkovChain to pick a random starting word and then pick each subsequent
     * word based on the probability that it follows the one before? This is
     * where you implement that core logic!
     * <p>
     * Use the (assumed to be trained) MarkovChain as an iterator to build up a
     * String that represents the tweet that's returned.
     * <p>
     * 1. reset the MarkovChain (to prepare it to generate a new sentence)
     * 2. validate the numWords argument
     * 3. repeatedly generate new words to add to the tweet:
     * <p>
     * 3.a If the MarkovChain has no more values in its Iterator but the tweet
     * is not yet at the required number of words, use randomPunctuation() to
     * end the sentence and then reset() to begin the next sentence with a
     * random start word.
     * <p>
     * Your tweet should be properly formatted with one space between each word
     * and between sentences. It should not contain any leading or trailing
     * whitespace. You should leave the words uncapitalized, just as they are
     * from TweetParser. All tweets should end in punctuation.
     * <p>
     * You should return an empty string if there were no sentences available to
     * access when calling hasNext or if the number of words is 0. You also need
     * to do some input validation to make sure the number of words is not
     * negative.
     *
     * @param numWords - The desired number of words of the tweet to be
     *                 produced
     * @return a String representing a generated tweet
     * @throws IllegalArgumentException if numWords is negative
     */
    public String generateTweet(int numWords) {
        String finalSentence = "";
        this.mc.reset();
        if (numWords < 0) {
            throw new IllegalArgumentException();
        }
        if (numWords == 0 || !mc.hasNext()) {
            return "";
        }

        for (int i = 0; i < numWords; i++) {
            if (! mc.hasNext()) {
                finalSentence += randomPunctuation() + " ";
                mc.reset();
            }
            finalSentence += mc.next();
            if (i == numWords - 1) {
                finalSentence += randomPunctuation();
            } else if (mc.hasNext()) {
                finalSentence += " ";
            }

        }
        return finalSentence;
    }

    /**
     * Generates a series of tweets using generateTweetChars().
     *
     * @param numTweets - the number of tweets to generate
     * @param numChars  - the number of characters that each generated tweet
     *                  should have.
     * @return a List of Strings where each element is a tweet
     */
    public List<String> generateTweets(int numTweets, int numChars) {
        List<String> tweets = new ArrayList<String>();
        while (numTweets > 0) {
            tweets.add(generateTweetChars(numChars));
            numTweets--;
        }
        return tweets;
    }

    /**
     * Generates a tweet using generateTweet().
     *
     * @param numChars - The desired number of characters of the tweet to be
     *                 produced
     * @return a String representing a generated tweet
     * @throws IllegalArgumentException if numChars is negative
     */
    public String generateTweetChars(int numChars) {
        if (numChars < 0) {
            throw new IllegalArgumentException(
                    "tweet length cannot be negative"
            );
        }

        String tweet = "";
        int numWords = 1;
        while (true) {
            String newTweet = generateTweet(numWords);
            if (newTweet.length() > numChars) {
                return tweet;
            }
            tweet = newTweet;
            numWords++;
        }
    }

    /**
     * A helper function for providing a random punctuation String.
     *
     * @return a string containing just one punctuation character, specifically
     *         '.' 70% of the time and ';', '?', and '!' each 10% of the time.
     */
    public String randomPunctuation() {
        char[] puncs = { ';', '?', '!' };
        int m = ng.next(10);
        if (m < puncs.length) {
            return String.valueOf(puncs[m]);
        }
        return ".";
    }

    /**
     * A helper function to return the numerical index of the punctuation.
     *
     * @param punc - an input char to return the index of
     * @return the numerical index of the punctuation
     */
    public int fixPunctuation(char punc) {
        switch (punc) {
            case ';':
                return 0;
            case '?':
                return 1;
            case '!':
                return 2;
            default:
                return 3;
        }
    }

    /**
     * Returns true if the passed in string is punctuation.
     *
     * @param s - a string to check whether or not it's punctuation
     * @return true if the string is punctuation, false otherwise.
     */
    public boolean isPunctuation(String s) {
        return s.equals(";") || s.equals("?") || s.equals("!") || s.equals(".");
    }

    /**
     * A helper function to determine if a string ends in punctuation.
     *
     * @param s - an input string to check for punctuation
     * @return true if the string s ends in punctuation
     */
    public static boolean isPunctuated(String s) {
        if (s == null || s.equals("")) {
            return false;
        }
        char[] puncs = TweetParser.getPunctuation();
        for (char c : puncs) {
            if (s.charAt(s.length() - 1) == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints ten generated tweets to the console so you can see how your bot is
     * performing!
     */
    public static void main(String[] args) {
        BufferedReader br = FileLineIterator.fileToReader(PATH_TO_TWEETS);
        TwitterBot t = new TwitterBot(br, TWEET_COLUMN);
        List<String> tweets = t.generateTweets(10, 280); // 280 chars in a tweet
        for (String tweet : tweets) {
            System.out.println(tweet);
        }

        // You can also write randomly generated tweets to a file by
        // uncommenting the following code:
        // t.writeTweetsToFile(10, 280, PATH_TO_OUTPUT_TWEETS, false);
    }

    /**
     * Modifies all MarkovChains to output sentences in the order specified.
     * <p>
     * The goal of `fixDistribution` is to ensure that our underlying
     * probability distributions output a tweet in the order that we desire. Our
     * implementation does this by splitting us into 2 LNGs:
     * 1. TwitterBot LNG
     * This LNG serves to make sure our punctuation is output in the order we
     * expect.
     * 2. MarkovChain LNG
     * This LNG makes sure the tweets are output in
     * the proper order. This will be built by running `fixDistribution` on the
     * Markov Chain with punctuation replaced by null.
     * <p>
     * Assumes that the expected tweet is punctuated.
     *
     * @param tweet - an ordered list of words and punctuation that the
     *              MarkovChain should output.
     */
    public void fixDistribution(List<String> tweet) {
        List<String> puncs = java.util.Arrays.asList(".", "?", "!", ";");

        if (tweet == null) {
            throw new IllegalArgumentException(
                    "fixDistribution(): tweet argument must not be null."
            );
        } else if (tweet.size() == 0) {
            throw new IllegalArgumentException(
                    "fixDistribution(): tweet argument must not be empty."
            );
        } else if (!puncs.contains(tweet.get(tweet.size() - 1))) {
            throw new IllegalArgumentException(
                    "fixDistribution(): Passed in tweet must be punctuated."
            );
        }

        mc.fixDistribution(
                tweet.stream().map(x -> puncs.contains(x) ? null : x)
                        .collect(java.util.stream.Collectors.toList()),
                true
        );
        List<Integer> puncIndices = new LinkedList<>();
        for (int i = 0; i < tweet.size(); i++) {
            String curWord = tweet.get(i);

            if (isPunctuation(curWord)) {
                puncIndices.add(fixPunctuation(curWord.charAt(0)));
            }
        }
        ng = new ListNumberGenerator(puncIndices);
    }
}
