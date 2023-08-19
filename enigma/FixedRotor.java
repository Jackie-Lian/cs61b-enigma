package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Jackie Lian
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        _position = 0;
    }

    @Override
    public String toString() {
        return "FixedRotor " + name();
    }
}
