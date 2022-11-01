package org.cis120;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/** Tests for TweetParser */
public class TweetParserTest {

    // A helper function to create a singleton list from a word
    private static List<String> singleton(String word) {
        List<String> l = new LinkedList<String>();
        l.add(word);
        return l;
    }

    // A helper function for creating lists of strings
    private static List<String> listOfArray(String[] words) {
        List<String> l = new LinkedList<String>();
        for (String s : words) {
            l.add(s);
        }
        return l;
    }

    // Cleaning and filtering tests -------------------------------------------
    @Test
    public void removeURLsTest() {
        assertEquals("abc . def.", TweetParser.removeURLs("abc http://www.cis.upenn.edu. def."));
        assertEquals("abc", TweetParser.removeURLs("abc"));
        assertEquals("abc ", TweetParser.removeURLs("abc http://www.cis.upenn.edu"));
        assertEquals("abc .", TweetParser.removeURLs("abc http://www.cis.upenn.edu."));
        assertEquals(" abc ", TweetParser.removeURLs("http:// abc http:ala34?#?"));
        assertEquals(" abc  def", TweetParser.removeURLs("http:// abc http:ala34?#? def"));
        assertEquals(" abc  def", TweetParser.removeURLs("https:// abc https``\":ala34?#? def"));
        assertEquals("abchttp", TweetParser.removeURLs("abchttp"));
    }

    @Test
    public void testCleanWord() {
        assertEquals("abc", TweetParser.cleanWord("abc"));
        assertEquals("abc", TweetParser.cleanWord("ABC"));
        assertNull(TweetParser.cleanWord("@abc"));
        assertEquals("ab'c", TweetParser.cleanWord("ab'c"));
    }

    @Test
    public void testExtractColumnGetsCorrectColumn() {
        assertEquals(
                " This is a tweet.",
                TweetParser.extractColumn(
                        "wrongColumn, wrong column, wrong column!, This is a tweet.", 3
                )
        );
    }

    @Test
    public void parseAndCleanSentenceNonEmptyFiltered() {
        List<String> sentence = TweetParser.parseAndCleanSentence("abc #@#F");
        List<String> expected = new LinkedList<String>();
        expected.add("abc");
        assertEquals(expected, sentence);
    }


    @Test
    public void testParseAndCleanTweetRemovesURLS1() {
        List<List<String>> sentences = TweetParser
                .parseAndCleanTweet("abc http://www.cis.upenn.edu");
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(singleton("abc"));
        assertEquals(expected, sentences);
    }


    @Test
    public void testCsvDataToTrainingDataSimpleCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 1);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("the end should come here".split(" ")));
        expected.add(listOfArray("this comes from data with no duplicate words".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testCsvDataToTweetsSimpleCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, 1);
        List<String> expected = new LinkedList<String>();
        expected.add(" The end should come here.");
        expected.add(" This comes from data with no duplicate words!");
        assertEquals(expected, tweets);
    }



    /* **** ****** **** WRITE YOUR TESTS BELOW THIS LINE **** ****** **** */


    @Test
    public void parseAndCleanSentenceInvalidWordsInBetween() {
        List<String> sentence = TweetParser.parseAndCleanSentence("a @ hello! b");
        List<String> expected = new LinkedList<String>();
        expected.add("a");
        expected.add("b");
        assertEquals(expected, sentence);
    }
    @Test
    public void parseAndCleanSentenceEmpty() {
        List<String> sentence = TweetParser.parseAndCleanSentence("");
        List<String> expected = new LinkedList<String>();
        String[] array = {};
        expected = listOfArray(array);
        assertEquals(expected, sentence);
    }

    @Test
    public void parseAndCleanSentenceEmptyInvalidWord() {
        List<String> sentence = TweetParser.parseAndCleanSentence("!!$@sFF");
        List<String> expected = new LinkedList<String>();
        String[] array = {};
        expected = listOfArray(array);
        assertEquals(expected, sentence);
    }
    @Test
    public void parseAndCleanTweetNormalTwoSentences() {
        List<List<String>> sentence = TweetParser.parseAndCleanTweet("Hello! My name is Cindy.");
        List<List<String>> expected = new LinkedList<List<String>>();
        String[] array = {"my", "name", "is", "cindy"};
        expected.add(singleton("hello"));
        expected.add(listOfArray(array));
        assertEquals(expected, sentence);
    }
    @Test
    public void testParseAndCleanTweetRemovesURLinBetween() {
        List<List<String>> sentences = TweetParser
                .parseAndCleanTweet("abc. http://www.cis.upenn.edu def");
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(singleton("abc"));
        expected.add(singleton("def"));
        assertEquals(expected, sentences);
    }

    @Test
    public void testParseAndCleanTweetRemovesURLandBadWordsInBetween() {
        List<List<String>> sentences = TweetParser
                .parseAndCleanTweet("abc. FSF@ http://www.cis.upenn.edu def ghk! hi");
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(singleton("abc"));
        List<String> expectedlist = new LinkedList<String>();
        String[] array = {"def", "ghk"};
        expected.add(listOfArray(array));
        expected.add(singleton("hi"));
        assertEquals(expected, sentences);
    }
    @Test
    public void testCsvDataToTweets() {
        StringReader sr = new StringReader(
                "0, hi, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!\n" +
                        "1, 2!, it worked!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, 1);
        List<String> expected = new LinkedList<String>();
        expected.add(" hi");
        expected.add(" This comes from data with no duplicate words!");
        expected.add(" 2!");
        assertEquals(expected, tweets);
    }
    @Test
    public void testCsvDataToTweetsOutOfBound() {
        StringReader sr = new StringReader(
                "0, hi, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!\n" +
                        "1, 2, it worked!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, 2);
        List<String> expected = new LinkedList<String>();
        expected.add(" The end should come here.");
        expected.add(" it worked!");
        assertEquals(expected, tweets);
    }
    @Test
    public void testCsvDataToTweetsTweetColumnOutOfBound() {
        StringReader sr = new StringReader(
                "0, hi, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!\n" +
                        "1, 2, it worked!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, 5);
        assertEquals(0, tweets.size());
    }
    @Test
    public void testCsvDataToTweetsTweetColumnAllOutOfBound() {
        StringReader sr = new StringReader(
                "0, hi, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!\n" +
                        "1, 2, it worked!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, 5);
        assertEquals(0, tweets.size());
    }
    @Test
    public void testCsvDataToTweetsTweetColumnNegative() {
        StringReader sr = new StringReader(
                "0, hi, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!\n" +
                        "1, 2, it worked!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, -1);
        assertEquals(0, tweets.size());
    }
    @Test
    public void testCsvDataToTrainingDataSimpleCSVWithInvalid() {
        StringReader sr = new StringReader(
                "0, The end should come @skdfh here.\n" +
                        "1, This comes from @FGH data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 1);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("the end should come here".split(" ")));
        expected.add(listOfArray("this comes from data with no duplicate words".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testCsvDataToTrainingDataSimpleCSVWithEmptySentence() {
        StringReader sr = new StringReader(
                "hi this is the first column, The end should come @skdfh here.\n" +
                        "               \n" +
                        "hi this too, This comes from @FGH data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 0);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("hi this is the first column".split(" ")));
        expected.add(listOfArray("hi this too".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testCsvDataToTrainingDataSimpleCSVWithURL() {
        StringReader sr = new StringReader(
                "0, The end should come @skdfh here.\n" +
                        "8, here is the link  http://www.cis.upenn.edu to the class? \n" +
                        "1, This comes from @FGH data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 1);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("the end should come here".split(" ")));
        expected.add(listOfArray("here is the link to the class".split(" ")));
        expected.add(listOfArray("this comes from data with no duplicate words".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testExtractColumnGetsCorrectColumnNegative() {
        assertEquals(
                null,
                TweetParser.extractColumn(
                        "wrongColumn, wrong column, wrong column!, This is a tweet.", -1
                )
        );
    }
    @Test
    public void testExtractColumnGetsCorrectColumnOutOfBound() {
        assertEquals(
                null,
                TweetParser.extractColumn(
                        "wrongColumn, wrong column, wrong column!, This is a tweet.", 4
                )
        );
    }

    @Test
    public void testExtractColumnGetsCorrectColumnCSVNull() {
        assertEquals(
                null,
                TweetParser.extractColumn(
                        "", 4
                )
        );
    }


}

