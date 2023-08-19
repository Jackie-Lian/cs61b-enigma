package enigma;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Main class.
 *  @author Jackie Lian
 */

public class MainTest {

    private static final Alphabet AZ = new Alphabet(TestUtils.UPPER_STRING);

    private static final HashMap<String, Rotor> ROTORS = new HashMap<>();

    static {
        HashMap<String, String> nav = TestUtils.NAVALA;
        ROTORS.put("B", new Reflector("B", new Permutation(nav.get("B"), AZ)));
        ROTORS.put("Beta",
                new FixedRotor("Beta",
                        new Permutation(nav.get("Beta"), AZ)));
        ROTORS.put("III",
                new MovingRotor("III",
                        new Permutation(nav.get("III"), AZ), "V"));
        ROTORS.put("IV",
                new MovingRotor("IV", new Permutation(nav.get("IV"), AZ),
                        "J"));
        ROTORS.put("I",
                new MovingRotor("I", new Permutation(nav.get("I"), AZ),
                        "Q"));
    }
    private static final String[] ROTORS1 = { "B", "Beta", "III", "IV", "I" };
    private static final String SETTING1 = "AXLE";

    @Test
    public void testPrintMessageLine() {
        String test = "FROMHISSHOULDERHIAWATHA";
        String result;
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        mach.setRotors(SETTING1);
        mach.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", AZ));
        result = mach.convert(test);
        assertEquals("QVPQSOKOILPUBKJZPISFXDW", result);
        String test2 = "TOOKTHECAMERAOFROSEWOOD";
        String result2 = mach.convert(test2);
        assertEquals("BHCNSCXNUOAATZXSRCFYDGU", result2);
    }
}
