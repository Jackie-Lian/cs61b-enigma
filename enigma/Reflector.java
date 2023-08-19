package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Jackie Lian
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        _position = 0;
        if (!perm.derangement()) {
            throw error("Invalid reflector: not a derangement");
        }
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    int convertBackward(int p) {
        throw error("Reflector cannot convert backwards");
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

    @Override
    public String toString() {
        return "Reflector " + name();
    }

}
