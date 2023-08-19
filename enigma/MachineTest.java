package enigma;

import java.util.HashMap;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Jackie Lian
 */
public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTS ***** */

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

    private Machine mach1() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        mach.setRotors(SETTING1);
        return mach;
    }

    @Test
    public void testInsertRotors() {
        Machine mach = new Machine(AZ, 5, 3, ROTORS.values());
        mach.insertRotors(ROTORS1);
        assertEquals(5, mach.numRotors());
        assertEquals(3, mach.numPawls());
        assertEquals(AZ, mach.alphabet());
        assertEquals(ROTORS.get("B"), mach.getRotor(0));
        assertEquals(ROTORS.get("Beta"), mach.getRotor(1));
        assertEquals(ROTORS.get("III"), mach.getRotor(2));
        assertEquals(ROTORS.get("IV"), mach.getRotor(3));
        assertEquals(ROTORS.get("I"), mach.getRotor(4));
    }


    @Test
    public void testConvertChar() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(YF) (HZ)", AZ));
        assertEquals(25, mach.convert(24));
    }

    @Test
    public void testConvertMsg() {
        Machine mach = mach1();
        mach.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", AZ));
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                mach.convert("FROMHISSHOULDERHIAWATHA"));
    }

    @Test
    public void testConvert() {
        Machine m = new Machine(AZ, 5, 3, ROTORS.values());
        m.insertRotors(ROTORS1);
        m.setRotors("AXLE");
        m.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", AZ));
        assertEquals("Wrong convert", "QVPQ", m.convert("FROM"));
        m.insertRotors(ROTORS1);
        m.setRotors("AXLE");
        m.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", AZ));
        assertEquals("Wrong convert", "FROM", m.convert("QVPQ"));
    }

    @Test
    public void testConvertComplicated() {
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
        String test3 = "MADEOFSLIDINGFOLDINGROSEWOOD";
        String result3 = mach.convert(test3);
        assertEquals("FLPNXGXIXTYJUJRCAUGEUNCFMKUF", result3);
    }

    private static final HashMap<String, Rotor> ROTORS2 = new HashMap<>();

    static {
        HashMap<String, String> nav = TestUtils.NAVALA;
        ROTORS2.put("B", new Reflector("B", new Permutation(nav.get("B"), AZ)));
        ROTORS2.put("Beta",
                new FixedRotor("Beta",
                        new Permutation(nav.get("Beta"), AZ)));
        ROTORS2.put("I",
                new MovingRotor("I", new Permutation(nav.get("I"), AZ),
                        "Q"));
        ROTORS2.put("II",
                new MovingRotor("II", new Permutation(nav.get("II"), AZ),
                        "E"));
        ROTORS2.put("III",
                new MovingRotor("III",
                        new Permutation(nav.get("III"), AZ), "V"));
    }
    @Test
    public void testConverstion() {
        Machine m = new Machine(AZ, 5, 3, ROTORS2.values());
        m.insertRotors(new String[]{"B", "Beta", "I", "II", "III" });
        String setting2 = "AAAA";
        m.setRotors(setting2);
        String test = "HELLOWORLD";
        String result = m.convert(test);
        System.out.println(result);
        assertEquals("ILBDAAMTAZ", result);
    }


    @Test (expected = EnigmaException.class)
    public void testException() {
        Machine m = new Machine(AZ, 5, 5, ROTORS.values());
    }

    @Test (expected = EnigmaException.class)
    public void testException2() {
        Machine m = new Machine(AZ, 5, 4, ROTORS.values());
        m.insertRotors(ROTORS1);
    }

    @Test (expected = EnigmaException.class)
    public void testException3() {
        Machine m = new Machine(AZ, 5, 4, ROTORS.values());
        m.insertRotors(new String[]{ "E", "Beta", "III", "IV", "I" });
    }

    @Test (expected = EnigmaException.class)
    public void testException4() {
        Machine m = new Machine(AZ, 5, 4, ROTORS.values());
        m.insertRotors(new String[]{"Beta", "III", "IV", "I" });
    }


    @Test (expected = EnigmaException.class)
    public void testException5() {
        Machine m = new Machine(AZ, 5, 4, ROTORS.values());
        m.insertRotors(new String[]{"B", "III", "Beta", "IV", "I" });
    }
}
