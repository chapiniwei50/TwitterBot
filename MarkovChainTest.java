package org.cis120;

import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for MarkovChain */
public class MarkovChainTest {

    /*
     * Writing tests for Markov Chain can be a little tricky.
     * We provide a few tests below to help you out, but you still need
     * to write your own.
     */

    @Test
    public void testAddBigram() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("1", "2");
        assertTrue(mc.chain.containsKey("1"));
        ProbabilityDistribution<String> pd = mc.chain.get("1");
        assertTrue(pd.getRecords().containsKey("2"));
        assertEquals(1, pd.count("2"));
    }



    @Test
    public void testTrain() {
        MarkovChain mc = new MarkovChain();
        String sentence = "1 2 3";
        mc.train(Arrays.stream(sentence.split(" ")).iterator());
        assertEquals(3, mc.chain.size());
        ProbabilityDistribution<String> pd1 = mc.chain.get("1");
        assertTrue(pd1.getRecords().containsKey("2"));
        assertEquals(1, pd1.count("2"));
        ProbabilityDistribution<String> pd2 = mc.chain.get("2");
        assertTrue(pd2.getRecords().containsKey("3"));
        assertEquals(1, pd2.count("3"));
        ProbabilityDistribution<String> pd3 = mc.chain.get("3");
        assertTrue(pd3.getRecords().containsKey(null));
        assertEquals(1, pd3.count(null));
    }


    @Test
    public void testWalk() {
        /*
         * Using the sentences "CIS 120 rocks" and "CIS 120 beats CIS 160",
         * we're going to put some bigrams into the Markov Chain.
         *
         * While in the real world, we want the sentence we output to be random,
         * we don't want this in testing. For testing, we want to modify our
         * ProbabilityDistribution such that it will output a predictable chain
         * of words.
         *
         * Luckily, we've provided a `fixDistribution` method that will do this
         * for you! By calling `fixDistribution` with a list of words that you
         * expect to be output, the ProbabilityDistributions will be modified to
         * output your words in that order.
         *
         * See our below test for an example of how to use this.
         */

        String[] expectedWords = { "CIS", "120", "beats", "CIS", "120", "rocks" };
        MarkovChain mc = new MarkovChain();

        String sentence1 = "CIS 120 rocks";
        String sentence2 = "CIS 120 beats CIS 160";
        mc.train(Arrays.stream(sentence1.split(" ")).iterator());
        mc.train(Arrays.stream(sentence2.split(" ")).iterator());

        mc.reset("CIS"); // we start with "CIS" since that's the word our desired walk starts with
        mc.fixDistribution(new ArrayList<>(Arrays.asList(expectedWords)));

        for (int i = 0; i < expectedWords.length; i++) {
            assertTrue(mc.hasNext());
            String next = mc.next();
            assertEquals(expectedWords[i], next);
        }


    }

    /* **** ****** **** WRITE YOUR TESTS BELOW THIS LINE **** ****** **** */
    @Test
    public void testAddBigram2AppearsMultipleTimes() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("1", "2");
        mc.addBigram("1", "2");
        mc.addBigram("1", "2");
        mc.addBigram("3", "2");
        mc.addBigram("4", "2");
        assertTrue(mc.chain.containsKey("1"));
        assertTrue(mc.chain.containsKey("3"));
        assertTrue(mc.chain.containsKey("4"));
        assertFalse(mc.chain.containsKey("2"));
        ProbabilityDistribution<String> pd = mc.chain.get("1");
        assertTrue(pd.getRecords().containsKey("2"));
        assertEquals(3, pd.count("2"));
        ProbabilityDistribution<String> pd3 = mc.chain.get("3");
        assertTrue(pd.getRecords().containsKey("2"));
        assertEquals(1, pd3.count("2"));
        ProbabilityDistribution<String> pd4 = mc.chain.get("4");
        assertTrue(pd.getRecords().containsKey("2"));
        assertEquals(1, pd4.count("2"));
    }
    @Test
    public void testAddBigramFirstIsNull() {
        MarkovChain mc = new MarkovChain();
        assertThrows(IllegalArgumentException.class, () -> {
            mc.addBigram(null, "ef");
        });
    }
    @Test
    public void testTrainTwoSentences() {
        MarkovChain mc = new MarkovChain();
        String sentence1 = "CIS 120 rocks";
        String sentence2 = "CIS 120 beats CIS 160";
        mc.train(Arrays.stream(sentence1.split(" ")).iterator());
        mc.train(Arrays.stream(sentence2.split(" ")).iterator());
        assertEquals(5, mc.chain.size());
        ProbabilityDistribution<String> pd1 = mc.chain.get("CIS");
        assertTrue(pd1.getRecords().containsKey("120"));
        assertTrue(pd1.getRecords().containsKey("160"));
        assertEquals(2, pd1.count("120"));
        ProbabilityDistribution<String> pd2 = mc.chain.get("120");
        assertTrue(pd2.getRecords().containsKey("rocks"));
        assertTrue(pd2.getRecords().containsKey("beats"));
        assertEquals(1, pd2.count("rocks"));
        ProbabilityDistribution<String> pd3 = mc.chain.get("rocks");
        assertTrue(pd3.getRecords().containsKey(null));
        assertEquals(1, pd3.count(null));
    }
    @Test
    public void testTrainEmptySentence() {
        MarkovChain mc = new MarkovChain();
        String sentence1 = "";
        mc.train(Arrays.stream(sentence1.split(" ")).iterator());
        assertEquals(0, mc.chain.size());
    }
    @Test
    public void testTrainNullSentence() {
        MarkovChain mc = new MarkovChain();
        assertThrows(IllegalArgumentException.class, () -> {
            mc.train(null);
        });


    }
    @Test
    public void testResetStartNotPartOfTheChain() {
        MarkovChain mc = new MarkovChain();

        String sentence1 = "CIS 120 rocks";
        String sentence2 = "CIS 120 beats CIS 160";
        mc.train(Arrays.stream(sentence1.split(" ")).iterator());
        mc.train(Arrays.stream(sentence2.split(" ")).iterator());

        mc.reset("hi");
        assertTrue(mc.hasNext());
        String next = mc.next();
        assertEquals("hi", next);
        assertFalse(mc.hasNext());
        assertThrows(NoSuchElementException.class, () -> {
            mc.next();
        });

    }





}
