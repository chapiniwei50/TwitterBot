package org.cis120;

import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for FileLineIterator */
public class FileLineIteratorTest {

    /*
     * Here's a test to help you out, but you still need to write your own.
     */

    @Test
    public void testHasNextAndNext() {

        // Note we don't need to create a new file here in order to test out our
        // FileLineIterator if we do not want to. We can just create a
        // StringReader to make testing easy!
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("0, The end should come here.", li.next());
        assertTrue(li.hasNext());
        assertEquals("1, This comes from data with no duplicate words!", li.next());
        assertFalse(li.hasNext());
    }

    @Test
    public void testOneSentence() {
        String words = "0, just one sentence";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("0, just one sentence", li.next());
    }


    /* **** ****** **** WRITE YOUR TESTS BELOW THIS LINE **** ****** **** */
    @Test
    public void nullReader() {
        BufferedReader br = null;
        assertThrows(IllegalArgumentException.class, () -> {
            new FileLineIterator(br);
        });
    }

    @Test
    public void nullFilePath() {
        String filePath = null;
        assertThrows(IllegalArgumentException.class, () -> {
            new FileLineIterator(filePath);
        });
    }

    @Test
    public void fileToReaderNullFilePath() {
        String filePath = null;
        assertThrows(IllegalArgumentException.class, () -> {
            FileLineIterator.fileToReader(filePath);
        });
    }

    @Test
    public void fileToReaderFileDoesntExist() {
        String filePath = "akerugvqbiur";
        assertThrows(IllegalArgumentException.class, () -> {
            FileLineIterator.fileToReader(filePath);
        });
    }
    @Test
    public void testEmpty() {
        String words = "";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertFalse(li.hasNext());
    }

    @Test
    public void testNoMoreDataWhenNextCalled() {

        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("0, The end should come here.", li.next());
        assertTrue(li.hasNext());
        assertEquals("1, This comes from data with no duplicate words!", li.next());
        assertFalse(li.hasNext());
        assertThrows(NoSuchElementException.class, () -> {
            li.next();
        });

    }

}
