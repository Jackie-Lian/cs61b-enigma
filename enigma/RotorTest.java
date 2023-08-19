package enigma;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** The suite of all JUnit tests for the Rotor class.
 *  @author Jackie Lian
 */
public class RotorTest {

    @Test
    public void testConvertForward() {
        Permutation valid1 = new
                Permutation("(AELTPHQXRU)(BKNW)(CMOY)(DFG)(IV)(JZ)(S)",
                new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        Rotor rotor1 = new Rotor("Rotor1", valid1);
        rotor1.set('F');
        assertEquals(8, rotor1.convertForward(5));
    }

    @Test
    public void testConvertBackward() {
        Permutation valid1 = new
                Permutation("(AELTPHQXRU)(BKNW)(CMOY)(DFG)(IV)(JZ)(S)",
                new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        Rotor rotor1 = new Rotor("Rotor1", valid1);
        rotor1.set('F');
        assertEquals(7, rotor1.convertBackward(9));
    }
}
