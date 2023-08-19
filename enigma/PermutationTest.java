package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;
import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Jackie Lian
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void testConstructor() {
        Permutation valid1 = new
                Permutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)",
                new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    @Test
    public void testPerm() {
        Permutation valid1 = new Permutation("(BACD)   ",
                new Alphabet("ABCD"));
        assertEquals(2, valid1.permute(0));
        assertEquals(1, valid1.permute(3));
        assertEquals(0, valid1.invert(2));
        assertEquals(3, valid1.invert(1));
        assertEquals('C', valid1.permute('A'));
        assertEquals('B', valid1.permute('D'));
        assertEquals('B', valid1.invert('A'));
        assertEquals('D', valid1.invert('B'));
    }

    @Test (expected = EnigmaException.class)
    public void testNoSpace() {
        Permutation invalid = new Permutation("(B A)(CD)",
                new Alphabet("ABCD"));
    }

    @Test
    public void testValid() {
        Permutation valid = new Permutation("(BA)   (CD)     ",
                new Alphabet("ABCD"));
        assertEquals(1, valid.permute(0));
        assertEquals(3, valid.permute(2));
        assertEquals(2, valid.permute(3));
    }

    @Test (expected = EnigmaException.class)
    public void testInvalid() {
        Permutation invalid = new Permutation("(BC(D", new Alphabet("ABCD"));
        Permutation invalid2 = new Permutation("(B\\D)", new Alphabet("ABCD"));
        Permutation invalid3 = new Permutation("(BC(D)", new Alphabet("ABCD"));
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        Permutation invalid1 = new Permutation("(AECD", new Alphabet("ABCD"));
        p.invert('F');

    }

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
}
