package enigma;
import org.junit.Test;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Alphabet class.
 *  @author Jackie Lian
 */

public class AlphabetTest {

    @Test
    public void testAlphabet() {
        String valid1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String valid2 = "A92nksale";
        String valid3 = "1234567";
        String nonvalid1 = "eecs16b";
        String nonvalid2 = "baby";
        String nonvalid3 = "";
        String nonvalid4 = "hi t's me";
        Alphabet test1 = new Alphabet(valid1);
        Alphabet test2 = new Alphabet(valid2);
        Alphabet test3 = new Alphabet(valid3);
        char ch1 = 'V';
        char ch2 = '0';
        char ch3 = ' ';
        char ch4 = '_';
        assertTrue(test1.contains(ch1));
        assertFalse(test2.contains(ch2));
        assertFalse(test3.contains(ch3));
        assertFalse(test1.contains(ch3));
        assertEquals('B', test1.toChar(1));
        assertEquals('2', test2.toChar(2));
        assertEquals(24, test1.toInt('Y'));
    }
}
